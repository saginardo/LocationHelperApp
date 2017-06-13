package com.notinglife.android.LocationHelper;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import com.baidu.location.LocationClient;
import com.baidu.mapapi.map.BaiduMap;
import com.notinglife.android.LocationHelper.dao.DeviceRawDao;
import com.notinglife.android.LocationHelper.domain.LocationDevice;
import com.notinglife.android.LocationHelper.fragment.ContentFragment;
import com.notinglife.android.LocationHelper.fragment.ListDeviceFragment;
import com.notinglife.android.LocationHelper.fragment.MapMarkFragment;
import com.notinglife.android.LocationHelper.utils.LogUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

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

    @BindView(R.id.toolBar)
    Toolbar mToolbar;
//    @BindView(R.id.mapview)
//    MapView mMapView;

    @BindView(R.id.rb_acq_data)
    Button mBTAcqData;
    @BindView(R.id.rb_mark_data)
    Button mBTMarkData;
    @BindView(R.id.rb_list_devices)
    Button mBTListDevice;

    private DeviceRawDao mDao;
    private Fragment contentFragment;
    private Fragment mapMarkFragment;
    private Fragment listDeviceFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //mLocationClient = new LocationClient(getApplicationContext());
        //mLocationClient.registerLocationListener(new MyLocationListener());
        //SDKInitializer.initialize(getApplicationContext());

        setContentView(R.layout.activity_main_content);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        //initPermission();
        //initData();
        initView();
        //initFragment();
        setSelect(0);//默认显示第一屏

        mBTAcqData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelect(0);
                LogUtil.i("采集按钮被点击了");
            }
        });

        mBTMarkData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelect(1);
                LogUtil.i("地图按钮被点击了");
            }
        });

        mBTListDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelect(2);
                LogUtil.i("设备被点击了");
            }
        });

    }

    private void initFragment() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();// 开始事务
        // 用fragment替换帧布局;
        // 参1:帧布局容器的id; 这里我要替换
        // 参2:是要替换的fragment;
        // 参3:标记
        // transaction.replace(R.id.fl_left_menu, new LeftMenuFragment(),TAG_LEFT_MENU);

        transaction.replace(R.id.rl_main_content, new ContentFragment(), TAG_ACQ_DATA);
        transaction.commit();// 提交事务
        // Fragment fragment = fm.findFragmentByTag(TAG_LEFT_MENU);//根据标记找到对应的fragment
    }

    private void initView() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rb_acq_data:
                setSelect(0);
                LogUtil.i("采集按钮被点击了");
                break;
            case R.id.rb_list_devices:
                setSelect(1);
                break;
            case R.id.rb_mark_data:
                setSelect(2);
                break;
        }
    }

    private void setSelect(int i) {

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();//创建一个事务

        hideFragment(transaction);//我们先把所有的Fragment隐藏了，然后下面再开始处理具体要显示的Fragment

        switch (i) {
            case 0:
                if (contentFragment == null) {
                    contentFragment = new ContentFragment();
                    transaction.add(R.id.rl_main_content, contentFragment);//将微信聊天界面的Fragment添加到Activity中
                } else {
                    transaction.show(contentFragment);
                }
                break;
            case 1:
                if (mapMarkFragment == null) {
                    mapMarkFragment = new MapMarkFragment();
                    transaction.add(R.id.rl_main_content, mapMarkFragment);
                } else {
                    transaction.show(mapMarkFragment);
                }
                break;
            case 2:
                if (listDeviceFragment == null) {
                    listDeviceFragment = new ListDeviceFragment();
                    transaction.add(R.id.rl_main_content, listDeviceFragment);
                } else {
                    transaction.show(listDeviceFragment);
                }
                break;
            default:
                break;
        }
        transaction.commit();//提交事务
    }

    /*
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
/*

    private void initData() {
        //创建一个帮助类对象
        MySqliteOpenHelper mySqliteOpenHelper = new MySqliteOpenHelper(getApplicationContext());
        //调用getReadableDatabase方法,来初始化数据库的创建
        SQLiteDatabase db = mySqliteOpenHelper.getReadableDatabase();
        mDao = new DeviceRawDao(getApplicationContext());
        mLocationDevice = new LocationDevice();
        //用来支持撤销功能的临时设备
        tmpDevice = new LocationDevice();
    }
*/

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


/*    private void initView() {
        EditTextUtil.editTextToUpperCase(mDeviceId,mMacAddress_1,mMacAddress_2,
                mMacAddress_3,mMacAddress_4,mMacAddress_5,mMacAddress_6);

        //获取地图对象
        mBaiduMap = mMapView.getMap();
        List<LocationDevice> list = mDao.queryAll();
        if (list != null && list.get(0).mDeivceId !=null) {
            LatLng point = new LatLng(Double.parseDouble(list.get(0).mLatitude)
                    , Double.parseDouble(list.get(0).mLongitude));
            moveMap(point);
        }

    }*/



/*    private void initPermission() {
        permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(MainActivity.this, permissions, 1);
        } else {
            initLocation();
        }

    }*/

/*    //初始化配置信息
    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setLocationMode(LocationClientOption.LocationMode.Device_Sensors);
        //可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setScanSpan(5000);
        //设置百度坐标
        option.setCoorType("bd09ll");
        option.setOpenGps(true); // 打开gps
        //可选，设置是否需要地址信息，默认不需要
        option.setIsNeedAddress(false);
        mLocationClient.setLocOption(option);
    }*/

/*
    //获取到权限后回调
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(MainActivity.this, "必须同意所以权限", Toast.LENGTH_LONG).show();
                            finish();
                            return;
                        }
                    }
                    initLocation();
                } else {
                    finish();
                }
                break;
            default:
        }
    }
*/

    //移动地图位置
/*    private void navigateTo(BDLocation location) {
        if (isFirstLocate) {
            LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
            LogUtil.i("当前经纬度：" + ll.toString());

            MapStatus mMapStatus = new MapStatus.Builder()
                    .target(ll)
                    .zoom(18f)
                    .build();
            MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);

            mBaiduMap.setMyLocationEnabled(true);
            MyLocationData.Builder builder = new MyLocationData.Builder();
            builder.latitude(location.getLatitude());
            builder.longitude(location.getLongitude());
            MyLocationData locationData = builder.build();
            mBaiduMap.setMyLocationData(locationData);

            mBaiduMap.setMapStatus(mMapStatusUpdate);
            isFirstLocate = false;
        }
    }*/

/*
    private void MarkPoint(List<LocationDevice> list) {

        mBaiduMap.clear();
        for (LocationDevice device : list) {
            LatLng point = new LatLng(Double.parseDouble(device.mLatitude), Double.parseDouble(device.mLongitude));
            //第一次移动到当前位置
            if (isFirstDrawPoint) {
                moveMap(point);
                isFirstDrawPoint = false;
            }
            //构建Marker图标
            BitmapDescriptor bitmap = BitmapDescriptorFactory
                    .fromResource(R.drawable.location_marker);
            //构建MarkerOption，用于在地图上添加Marker
            OverlayOptions option = new MarkerOptions()
                    .position(point)
                    .icon(bitmap);
            //在地图上添加Marker，并显示
            mBaiduMap.addOverlay(option);
        }
    }*/

    /*public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(final BDLocation bdLocation) {
            LogUtil.i("成功接收回显消息");

            if (bdLocation.getLocType() == BDLocation.TypeGpsLocation
                    || bdLocation.getLocType() == BDLocation.TypeNetWorkLocation) {
                navigateTo(bdLocation);
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String latitude = String.valueOf(bdLocation.getLatitude());
                    String longitude = String.valueOf(bdLocation.getLongitude());
                    String radius = String.valueOf(bdLocation.getRadius());

                    mLocationDevice.mLatitude = latitude;
                    mLocationDevice.mLongitude = longitude;

                    mLatTextView.setText(latitude);
                    mLngTextView.setText(longitude);
                    mRadiusValue.setText(radius + "米");
                    mLocModeValue = bdLocation.getLocType();

                    if (bdLocation.getLocType() == BDLocation.TypeGpsLocation) {
                        mLocMode.setText("GPS定位");
                    } else {
                        mLocMode.setText("网络定位");
                    }
                    //LogUtil.i("经度为：" + latitude);
                    //LogUtil.i("精度为：" + radius);
                }
            });

        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {
        }
    }*/


/*    public static boolean isOPen(final Context context) {
        LocationManager locationManager
                = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
        //boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (gps) {
            return true;
        }
        return false;
    }*/

    // TODO: 2017/6/13 继续进行抽取
    /*private void showDeleteByIdDialog(View view, final String deleteString) {

        builder=new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.alert);
        builder.setTitle("撤销保存");
        builder.setMessage("请确认是否撤销刚保存的设备信息？");

        //监听下方button点击事件
        builder.setPositiveButton(R.string.postive_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                LogUtil.i("确定按钮按下");
                int num1 = mDao.deleteById(deleteString);

                if (num1 < 1) {
                    ToastUtil.showShortToast(getApplicationContext(), "撤销失败，请查看是否已保存");
                } else {
                    ToastUtil.showShortToast(getApplicationContext(), "成功撤销保存");
                }
            }
        });
        builder.setNegativeButton(R.string.negative_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                LogUtil.i("取消按钮按下");
            }
        });

        //设置对话框是可取消的
        builder.setCancelable(true);
        AlertDialog dialog=builder.create();
        dialog.show();
    }*/


/*    private void moveMap(LatLng point) {
        MapStatus mMapStatus = new MapStatus.Builder()
                .target(point)
                .zoom(16f)
                .build();
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        mBaiduMap.setMapStatus(mMapStatusUpdate);
    }*/
}