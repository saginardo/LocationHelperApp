package com.notinglife.android.LocationHelper.activity;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.notinglife.android.LocationHelper.R;
import com.notinglife.android.LocationHelper.adapter.MyFragmentPagerAdapter;
import com.notinglife.android.LocationHelper.fragment.AcqDataFragment;
import com.notinglife.android.LocationHelper.fragment.DeviceListFragment;
import com.notinglife.android.LocationHelper.fragment.MineFragment;
import com.notinglife.android.LocationHelper.view.NoScrollViewPager;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity {


    @BindView(R.id.toolBar)
    Toolbar mToolbar;

    @BindView(R.id.rg_bottom_button)
    RadioGroup mRadioGroup;
    @BindView(R.id.vp_content)
    NoScrollViewPager mViewPager;


    //登录登出标志位
    private final static int ON_LOGIN = 20;
    private final static int ON_CONFIRM_LOGOUT = 21;
    private final static int ON_LOGOUT = 22;

    //startActivityForResult 标志位
    private final static int REQUEST_QR_CODE = 1028;
    private final static int REQUEST_QR_IMAGE = 1029;
    private final static int SCAN_RESULT_FROM_GALLERY = 1030;
    private final static int SCAN_REQUEST_FROM_TOOLBAR = 1031;
    private final static int SCAN_RESULT_FROM_TOOLBAR = 1032;

    private MyFragmentPagerAdapter mFragmentPagerAdapter;
    private static final String TAG = "MainActivity";
    private Activity mActivity;
    private MyHandler mHandler;
    private LocalBroadcastManager mBroadcastManager;
    private MyReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mActivity = this;
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        initPermission();
        initView();

        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.rb_acq_data:
                        mViewPager.setCurrentItem(0, true);
                        break;
                    case R.id.rb_devices:
                        mViewPager.setCurrentItem(1, true);
                        break;
                    case R.id.rb_my_page:
                        mViewPager.setCurrentItem(2, true);
                        break;
                    default:
                        break;
                }
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBroadcastManager.unregisterReceiver(mReceiver);
    }

    private void initView() {
        List<Fragment> allFragment = new ArrayList<>();

        //动态注册本地广播
        mBroadcastManager = LocalBroadcastManager.getInstance(mActivity);
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.notinglife.android.action.ON_LOGOUT"); //注册登出事件
        mReceiver = new MyReceiver();
        mBroadcastManager.registerReceiver(mReceiver, filter);

        //添加handler用于更新UI
        mHandler = new MyHandler(this);

        //向viewpager中添加fragment
        Fragment contentFragment = new AcqDataFragment();
        Fragment deviceListFragment = new DeviceListFragment();
        Fragment mineFragment = new MineFragment();
        allFragment.add(contentFragment);
        allFragment.add(deviceListFragment);
        allFragment.add(mineFragment);
        mFragmentPagerAdapter = new MyFragmentPagerAdapter(this.getSupportFragmentManager(), allFragment);
        mViewPager.setAdapter(mFragmentPagerAdapter);
        mViewPager.setCurrentItem(0);
        mViewPager.setOffscreenPageLimit(4);
        //viewpager页面监听事件
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        mRadioGroup.check(R.id.rb_acq_data);
                        break;
                    case 1:
                        mRadioGroup.check(R.id.rb_devices);
                        break;
                    case 2:
                        mRadioGroup.check(R.id.rb_my_page);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    //6.0版本后获取动态权限
    private void initPermission() {
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.CAMERA);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(this, permissions, 1);
        }
    }

    //获取权限后回调
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(this, "必须同意所以权限", Toast.LENGTH_LONG).show();
                            finish();
                            return;
                        }
                    }
                    //initLocation();
                } else {
                    finish();
                }
                break;
            default:
        }
    }



    @Override
    public void onActivityResult(final int requestCode, final int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if(data!=null){  //避免直接返回，没有数据导致的空指针
            if (requestCode == REQUEST_QR_CODE || requestCode == REQUEST_QR_IMAGE || resultCode== SCAN_RESULT_FROM_GALLERY) {
                AcqDataFragment item = (AcqDataFragment)mFragmentPagerAdapter.getItem(0);
                item.onActivityResult(requestCode, resultCode, data);
            }

            if(resultCode == SCAN_RESULT_FROM_TOOLBAR){
                AcqDataFragment item = (AcqDataFragment)mFragmentPagerAdapter.getItem(0);
                item.setIntentData(data);
            }
        }

    }

    //用于更新mainActivity的UI
    private static class MyHandler extends Handler {

        WeakReference<MainActivity> mMainActivity;
        MyHandler(MainActivity mainActivity) {
            mMainActivity = new WeakReference<>(mainActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            MainActivity mainActivity = mMainActivity.get();
            if (mainActivity != null) {// 先判断弱引用是否为空，为空则不更新UI
                int flag = msg.what;
                //接收注销本地广播，更新UI等逻辑
                if (flag == ON_LOGOUT) {
                    //注销后MainActivity的UI更新
                    //mainActivity.mLogoutMenuItem.setVisible(false);//已经在mineFragment中更新了UI
                }
            }
        }
    }


    //用于更新UI事件的广播
    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //接收到本地广播后先判断标志位
            int flag = intent.getIntExtra("flag", -1);

            if (flag == ON_LOGOUT) {
                Message message = Message.obtain();
                message.what = ON_LOGOUT;
                mHandler.sendMessage(message);
            }
        }
    }


}