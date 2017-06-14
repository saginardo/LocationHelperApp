package com.notinglife.android.LocationHelper;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.baidu.location.LocationClient;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.notinglife.android.LocationHelper.dao.DeviceRawDao;
import com.notinglife.android.LocationHelper.domain.LocationDevice;
import com.notinglife.android.LocationHelper.fragment.ContentFragment;
import com.notinglife.android.LocationHelper.fragment.ListDeviceFragment;
import com.notinglife.android.LocationHelper.fragment.MapMarkFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity{

    private static final String TAG_ACQ_DATA = "TAG_ACQ_DATA";
    private static final String TAG_MAP_MARK = "TAG_MAP_MARK";
    private static final String TAG_LIST_DEVICE = "TAG_LIST_DEVICE";

    public LocationClient mLocationClient;
    private List<String> permissionList;
    private BaiduMap mBaiduMap;
    private boolean isFirstLocate = true;
    private boolean isFirstDrawPoint = true;
    private boolean isFirstMark = true;
    private LocationDevice mLocationDevice;
    private LocationDevice tmpDevice;
    private int mLocModeValue = -1;
    private LocalBroadcastManager broadcastManager;
    private BroadcastReceiver mReceiver;


    @BindView(R.id.toolBar)
    Toolbar mToolbar;

    @BindView(R.id.rg_bottom_button)
    RadioGroup mRadioGroup;

    private DeviceRawDao mDao;
    private Fragment contentFragment;
    private Fragment mapMarkFragment;
    private Fragment listDeviceFragment;


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
        setSelect(0);//默认显示第一屏

        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.rb_acq_data:
                        setSelect(0);
                       // LogUtil.i("采集按钮被点击了");
                        break;
                    case R.id.rb_mark_data:
                        setSelect(1);
                       //LogUtil.i("地图按钮被点击了");
                        break;
                    case R.id.rb_list_devices:
                        setSelect(2);
                       //LogUtil.i("设备被点击了");
                        break;

                    default:
                        break;
                }
            }
        });
    }

    private void initView() {

    }

    private void setSelect(int i) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();//创建一个事务
        hideFragment(transaction);//我们先把所有的Fragment隐藏了，然后下面再开始处理具体要显示的Fragment

        switch (i) {
            case 0:
                if (contentFragment == null) {
                    contentFragment = new ContentFragment();
                    transaction.add(R.id.rl_main_content, contentFragment,TAG_ACQ_DATA);//将微信聊天界面的Fragment添加到Activity中
                } else {
                    transaction.show(contentFragment);
                }
                break;
            case 1:
                if (mapMarkFragment == null) {
                    mapMarkFragment = new MapMarkFragment();
                    transaction.add(R.id.rl_main_content, mapMarkFragment,TAG_MAP_MARK);
                } else {
                    transaction.show(mapMarkFragment);
                }
                break;
            case 2:
                if (listDeviceFragment == null) {
                    listDeviceFragment = new ListDeviceFragment();
                    transaction.add(R.id.rl_main_content, listDeviceFragment,TAG_LIST_DEVICE);
                } else {
                    transaction.show(listDeviceFragment);
                }
                break;
            default:
                break;
        }
        transaction.commit();//提交事务
    }

    /**
     * 隐藏所有的Fragment
     * */
    private void hideFragment(FragmentTransaction transaction) {
        if (contentFragment != null) {
            transaction.hide(contentFragment);
        }
        if (mapMarkFragment != null) {
            transaction.hide(mapMarkFragment);
        }
        if (listDeviceFragment != null) {
            transaction.hide(listDeviceFragment);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }


    @Override
    protected void onResume() {
        super.onResume();
        //mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //  mMapView.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //mLocationClient.stop();
        // mMapView.onDestroy();
        // mBaiduMap.setMyLocationEnabled(false);
    }

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
    //获取到权限后回调
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