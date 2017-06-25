package com.notinglife.android.LocationHelper.activity;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVUser;
import com.baidu.mapapi.SDKInitializer;
import com.notinglife.android.LocationHelper.R;
import com.notinglife.android.LocationHelper.dao.DeviceRawDao;
import com.notinglife.android.LocationHelper.domain.LocationDevice;
import com.notinglife.android.LocationHelper.fragment.AcqDataFragment;
import com.notinglife.android.LocationHelper.fragment.DeviceListFragment;
import com.notinglife.android.LocationHelper.fragment.MineFragment;
import com.notinglife.android.LocationHelper.utils.FileUtil;
import com.notinglife.android.LocationHelper.utils.LogUtil;
import com.notinglife.android.LocationHelper.utils.ToastUtil;
import com.notinglife.android.LocationHelper.view.NoScrollViewPager;
import com.notinglife.android.LocationHelper.view.SearchDialog;
import com.uuzuche.lib_zxing.activity.ZXingLibrary;

import java.io.File;
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

    //startActivityForResult 标志位
    private final static int REQUEST_CODE = 1028;
    private final static int REQUEST_IMAGE = 1029;
    private final static int RESULT_FROM_GALLERY = 1030;
    private final static int REQUEST_FROM_TOOLBAR = 1031;
    private final static int RESULT_FROM_TOOLBAR = 1032;

    //登录登出标志位
    private final static int ON_LOGIN = 10;
    private final static int ON_LOGOUT = 11;

    private Fragment contentFragment;
    private Fragment mapMarkFragment;
    private Fragment listDeviceFragment;
    private List<Fragment> allFragment;
    private List<String> permissionList;
    private MyFragmentPagerAdapter mFragmentPagerAdapter;
    private static final String TAG = "MainActivity";
    private Activity mActivity;
    private MenuItem mLogoutMenuItem;
    private MyHandler mHandler;
    private MyReceiver mReceiver;
    private LocalBroadcastManager broadcastManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //mLocationClient = new LocationClient(getApplicationContext());
        //mLocationClient.registerLocationListener(new MyLocationListener());
        SDKInitializer.initialize(getApplicationContext());
        ZXingLibrary.initDisplayOpinion(this);
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

    private void initView() {
        allFragment = new ArrayList<>();

        //动态注册本地广播，以便通知设备的添加和删除，可以及时更新UI
        broadcastManager = LocalBroadcastManager.getInstance(mActivity);
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.notinglife.android.action.ON_LOGOUT");
        mReceiver = new MyReceiver();
        broadcastManager.registerReceiver(mReceiver, filter);
        //添加handler用于更新UI
        mHandler = new MyHandler(this);

        contentFragment = new AcqDataFragment();
        listDeviceFragment = new DeviceListFragment();
        mapMarkFragment = new MineFragment();

        allFragment.add(contentFragment);
        allFragment.add(listDeviceFragment);
        allFragment.add(mapMarkFragment);
        mFragmentPagerAdapter = new MyFragmentPagerAdapter(this.getSupportFragmentManager(), allFragment);
        mViewPager.setAdapter(mFragmentPagerAdapter);
        mViewPager.setCurrentItem(0);
        mViewPager.setOffscreenPageLimit(4);

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

    private class MyFragmentPagerAdapter extends FragmentPagerAdapter {

        List<Fragment> fragmentList = new ArrayList<>();

        private MyFragmentPagerAdapter(FragmentManager fm, List<Fragment> list) {
            super(fm);
            fragmentList = list;
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_toolbar, menu);
        if (AVUser.getCurrentUser() != null){
            mLogoutMenuItem = menu.findItem(R.id.logout);
            mLogoutMenuItem.setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_search_button:
                showSearchDialog(mActivity);
                return true;
            case R.id.menu_scan_code:
                //扫码
                Intent intent = new Intent(this, CaptureActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
                return true;
            case R.id.menu_sendFile:
                DeviceRawDao mDao = new DeviceRawDao(mActivity);
                List<LocationDevice> mLocationDevices = mDao.queryAll();
                if (mLocationDevices == null || mLocationDevices.size() == 0) {
                    ToastUtil.showShortToast(mActivity, "当前没有数据，无法发送");
                }else {
                    FileUtil.saveListToFile(mActivity, mDao.queryAll());

                    Intent intentShareFile = new Intent(Intent.ACTION_SEND);
                    String myFilePath = mActivity.getExternalFilesDir("data").getPath();
                    File file = new File(myFilePath, "data.txt");
                    if (file.exists()) {
                        intentShareFile.setType("application/pdf");
                        intentShareFile.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + file));
                        intentShareFile.putExtra(Intent.EXTRA_SUBJECT,
                                "Sharing File...");
                        intentShareFile.putExtra(Intent.EXTRA_TEXT, "Sharing File...");
                        startActivity(Intent.createChooser(intentShareFile, "Share File"));
                    } else {
                        ToastUtil.showShortToast(mActivity, "没有数据文件");
                    }
                }

                break;
            case R.id.logout:
                AVUser.logOut();
                mLogoutMenuItem.setVisible(false);
                //通知recyclerview刷新
                Intent logoutIntent = new Intent("com.notinglife.android.action.ON_LOGOUT");
                logoutIntent.putExtra("flag", ON_LOGOUT);
                LocalBroadcastManager.getInstance(mActivity).sendBroadcast(logoutIntent);

                ToastUtil.showShortToast(getApplicationContext(),"已登出本系统");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //获取权限
    private void initPermission() {
        permissionList = new ArrayList<>();
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


    //自定义搜索框
    public static void showSearchDialog(Activity activity) {

        final SearchDialog mSearchDialog = new SearchDialog(activity, R.layout.device_mysearch_dialog);

        //自定义窗体参数
        Window dialogWindow = mSearchDialog.getWindow();
        dialogWindow.setGravity(Gravity.TOP);
        WindowManager.LayoutParams attributes = dialogWindow.getAttributes();
        DisplayMetrics metrics = activity.getResources().getDisplayMetrics();
        attributes.width = (int) (metrics.widthPixels * 0.95);
        attributes.height = (int) (metrics.heightPixels * 0.6);
        attributes.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND; //设置背景模糊
        attributes.dimAmount = 0.5f;
        //attributes.alpha = 0.9f; //对话框透明度
        mSearchDialog.getWindow().setAttributes(attributes);


        View view = mSearchDialog.getCustomView();
        TextView mTextView = (TextView) view.findViewById(R.id.tv_backspace);
        mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchDialog.dismiss();
            }
        });

        //展示对话框
        mSearchDialog.show();
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        AcqDataFragment item = (AcqDataFragment)mFragmentPagerAdapter.getItem(0);
        if(data!=null){  //避免直接返回，没有数据导致的空指针
            if (requestCode == REQUEST_CODE || requestCode == REQUEST_IMAGE || resultCode== RESULT_FROM_GALLERY) {
                item.onActivityResult(requestCode, resultCode, data);
                //LogUtil.i(resultCode+"-----");
            }
            LogUtil.i(TAG," resultCode "+ resultCode);
            if(resultCode == RESULT_FROM_TOOLBAR){
                LogUtil.i(TAG,"---"+ data.getData().toString());
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
                    mainActivity.mLogoutMenuItem.setVisible(false);
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