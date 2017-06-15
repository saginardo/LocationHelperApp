package com.notinglife.android.LocationHelper;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
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
import android.view.Menu;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.notinglife.android.LocationHelper.fragment.ContentFragment;
import com.notinglife.android.LocationHelper.fragment.ListDeviceFragment;
import com.notinglife.android.LocationHelper.fragment.MapMarkFragment;

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
    ViewPager mViewPager;

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
        setContentView(R.layout.activity_main_content);

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
                    case R.id.rb_mark_data:
                        mViewPager.setCurrentItem(1, true);
                        break;
                    case R.id.rb_list_devices:
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
        contentFragment = new ContentFragment();
        mapMarkFragment = new MapMarkFragment();
        listDeviceFragment = new ListDeviceFragment();
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
        return true;
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
}