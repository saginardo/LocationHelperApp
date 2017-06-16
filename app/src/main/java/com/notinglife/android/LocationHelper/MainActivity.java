package com.notinglife.android.LocationHelper;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
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

import com.baidu.mapapi.SDKInitializer;
import com.notinglife.android.LocationHelper.fragment.AcqDataFragment;
import com.notinglife.android.LocationHelper.fragment.DeviceListFragment;
import com.notinglife.android.LocationHelper.fragment.MapMarkFragment;
import com.notinglife.android.LocationHelper.ui.NoScrollViewPager;
import com.notinglife.android.LocationHelper.ui.SearchDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity {

    private List<String> permissionList;

    @BindView(R.id.toolBar)
    Toolbar mToolbar;

    @BindView(R.id.rg_bottom_button)
    RadioGroup mRadioGroup;
    @BindView(R.id.vp_content)
    NoScrollViewPager mViewPager;

    private Fragment contentFragment;
    private Fragment mapMarkFragment;
    private Fragment listDeviceFragment;
    private List<Fragment> allFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //mLocationClient = new LocationClient(getApplicationContext());
        //mLocationClient.registerLocationListener(new MyLocationListener());
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        initPermission();
        initView();

        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.rb_acq_data:
                        mViewPager.setCurrentItem(0, false);
                        break;
                    case R.id.rb_mark_data:
                        mViewPager.setCurrentItem(1, false);
                        break;
                    case R.id.rb_list_devices:
                        mViewPager.setCurrentItem(2, false);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void initView() {
        allFragment = new ArrayList<>();
        contentFragment = new AcqDataFragment();
        mapMarkFragment = new MapMarkFragment();
        listDeviceFragment = new DeviceListFragment();
        allFragment.add(contentFragment);
        allFragment.add(mapMarkFragment);
        allFragment.add(listDeviceFragment);

        mViewPager.setAdapter(new MyFragmentPagerAdapter(this.getSupportFragmentManager(), allFragment));
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
                        mRadioGroup.check(R.id.rb_mark_data);
                        break;
                    case 2:
                        mRadioGroup.check(R.id.rb_list_devices);
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
        getMenuInflater().inflate(R.menu.toolbar, menu);
        //MenuItem menuItem = menu.findItem(R.id.search_button);//
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search_button:
                //startActivity(new Intent(MainActivity.this, StartSearchActivity.class));
                showMyDialog(this,null,"测试标题","测试消息");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //获取权限
    private void initPermission() {
        permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
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


    public static void showMyDialog( Activity activity, final Handler handler, String title, String message) {

        final SearchDialog mSearchDialog = new SearchDialog(activity,R.layout.device_search_dialog);

        //自定义窗体参数
        Window dialogWindow = mSearchDialog.getWindow();
        dialogWindow.setGravity(Gravity.TOP);
        WindowManager.LayoutParams attributes = dialogWindow.getAttributes();
        DisplayMetrics metrics = activity.getResources().getDisplayMetrics();
        attributes.width = (int) (metrics.widthPixels * 0.98);
        attributes.height = (int) (metrics.heightPixels * 0.7);
        attributes.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND; //设置背景模糊
        attributes.dimAmount = 0.5f;
        attributes.alpha = 0.9f; //对话框透明度
        mSearchDialog.getWindow().setAttributes(attributes);


        View view = mSearchDialog.getCustomView();
        TextView mTextView = (TextView)view.findViewById(R.id.tv_backspace);
        mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchDialog.dismiss();
            }
        });




        //展示对话框
        mSearchDialog.show();
    }
}