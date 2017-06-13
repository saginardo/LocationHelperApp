package com.notinglife.android.LocationHelper;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.notinglife.android.LocationHelper.dao.DeviceRawDao;
import com.notinglife.android.LocationHelper.domain.LocationDevice;
import com.notinglife.android.LocationHelper.utils.EditTextUtil;
import com.notinglife.android.LocationHelper.utils.LogUtil;
import com.notinglife.android.LocationHelper.utils.MySqliteOpenHelper;
import com.notinglife.android.LocationHelper.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.notinglife.android.LocationHelper.utils.EditTextUtil.generateMacAddress;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public LocationClient mLocationClient;
    private List<String> permissionList;
    private BaiduMap mBaiduMap;
    private boolean isFirstLocate = true;
    private boolean isFirstDrawPoint = true;
    private boolean isFirstMark = true;
    private LocationDevice mLocationDevice;
    private LocationDevice tmpDevice;
    private int mLocModeValue = -1;

    @BindView(R.id.mapview)
    MapView mMapView;
    @BindView(R.id.tv_lat_info)
    TextView mLatTextView;
    @BindView(R.id.tv_lng_info)
    TextView mLngTextView;
    @BindView(R.id.tv_loc_mode_value)
    TextView mLocMode;
    @BindView(R.id.tv_loc_radius_value)
    TextView mRadiusValue;

    @BindView(R.id.tv_device_number)
    EditText mDeviceId;
    @BindView(R.id.editText1)
    EditText mMacAddress_1;
    @BindView(R.id.editText2)
    EditText mMacAddress_2;
    @BindView(R.id.editText3)
    EditText mMacAddress_3;
    @BindView(R.id.editText4)
    EditText mMacAddress_4;
    @BindView(R.id.editText5)
    EditText mMacAddress_5;
    @BindView(R.id.editText6)
    EditText mMacAddress_6;

    @BindView(R.id.bt_start_gps)
    Button mStartLocation;
    @BindView(R.id.bt_stop_gps)
    Button mStopLocation;
    @BindView(R.id.bt_save_data)
    Button mSaveButton;
    @BindView(R.id.bt_undo_save)
    Button mUndoSave;
    @BindView(R.id.bt_mark_data)
    Button mMarkData;
    @BindView(R.id.bt_watch_data)
    Button mWatchData;

    private DeviceRawDao mDao;
    private AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(new MyLocationListener());
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        initPermission();
        initData();
        initView();

        mStartLocation.setOnClickListener(this);
        mStopLocation.setOnClickListener(this);
        mSaveButton.setOnClickListener(this);
        mWatchData.setOnClickListener(this);
        mMarkData.setOnClickListener(this);
        mUndoSave.setOnClickListener(this);

    }

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

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationClient.stop();
        mMapView.onDestroy();
        mBaiduMap.setMyLocationEnabled(false);
    }


    private void initView() {
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

    }
    @Override
    public void onClick(View v) {
        List<LocationDevice> list = null;
        LocationDevice deviceToDo = null;//查询出来的临时对象

        switch (v.getId()) {
            case R.id.bt_start_gps:
                if (!isOPen(getApplicationContext())) {
                    ToastUtil.showShortToast(getApplicationContext(), "请先打开GPS");
                    // 转到手机设置界面，用户设置GPS
                    Intent intent = new Intent(
                            Settings.ACTION_LOCATION_SOURCE_SETTINGS);

                    startActivity(intent);
                }else {
                    ToastUtil.showShortToast(MainActivity.this, "定位中");
                    mLocationClient.start();
                }
                break;

            case R.id.bt_stop_gps:
                ToastUtil.showShortToast(MainActivity.this, "停止定位");
                mLocationClient.stop();
                break;

            case R.id.bt_save_data:

                if (!TextUtils.isEmpty(mLocationDevice.mLatitude)) {

                    String deviceid = mDeviceId.getText().toString();
                    String macaddress = generateMacAddress(mMacAddress_1,mMacAddress_2,
                            mMacAddress_3,mMacAddress_4,mMacAddress_5,mMacAddress_6);

                    LogUtil.i(macaddress);
                    if (deviceid.equals("")) {
                        ToastUtil.showShortToast(getApplicationContext(), "设备号码不能为空");
                        return;
                    }
                    if (macaddress!=null && macaddress.equals("")) {
                        ToastUtil.showShortToast(getApplicationContext(), "MAC地址不能为空");
                        return;
                    }
                    if (macaddress!=null && macaddress.length() < 17) {
                        ToastUtil.showShortToast(getApplicationContext(), "MAC地址信息不全");
                        return;
                    }

                    mLocationDevice.mDeivceId = deviceid;
                    mLocationDevice.mMacAddress = macaddress;

                    LogUtil.i("设备ID:" + mLocationDevice.mDeivceId + " ,MAC地址: " + mLocationDevice.mMacAddress + ", 经度为："
                            + mLocationDevice.mLatitude + ", 纬度为：" + mLocationDevice.mLongitude);

                    if (mLocModeValue != BDLocation.TypeGpsLocation) {
                        ToastUtil.showShortToast(getApplicationContext(), "请等待GPS定位数据");
                        return;
                    }

                    deviceToDo = mDao.queryById(deviceid);
                    if (deviceToDo != null) {
                        ToastUtil.showShortToast(getApplicationContext(), "不能重复添加设备，请查看设备是否已保存");
                        return;
                    }
                    mDao.add(mLocationDevice);
                    this.tmpDevice = mLocationDevice;

                    ToastUtil.showShortToast(getApplicationContext(), "添加设备信息成功");

                } else {
                    ToastUtil.showShortToast(MainActivity.this, "请先获取GPS地址");
                }
                break;

            case R.id.bt_watch_data:
                Intent intent = new Intent(MainActivity.this, DeviceDetailActivity.class);
                startActivity(intent);
                break;

            case R.id.bt_mark_data:
                list = mDao.queryAll();

                if (list == null || list.size() == 0) {
                    ToastUtil.showShortToast(getApplicationContext(), "当前没有数据");
                    return;
                }

                LatLng latlng = new LatLng(Double.parseDouble(list.get(0).mLatitude), Double.parseDouble(list.get(0).mLongitude));
                moveMap(latlng);
                MarkPoint(list);
                break;


            case R.id.bt_undo_save:
                //// FIXME: 2017/6/13 自增ID最大的是否是最近需要撤销的
                if(tmpDevice== null){
                    ToastUtil.showShortToast(getApplicationContext(),"还未保存设备，无法撤销保存");
                    return;
                }
                deviceToDo = mDao.queryById(tmpDevice.mDeivceId);
                if(deviceToDo==null){
                    ToastUtil.showShortToast(getApplicationContext(),"还未保存该设备，无法撤销保存");
                    return;
                }
                // FIXME: 2017/6/13 重构对话框
                showDeleteByIdDialog(v,tmpDevice.mDeivceId);

            default:
                break;
        }
    }

    private void initPermission() {
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

    }


    //初始化配置信息
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

    //移动地图位置
    private void navigateTo(BDLocation location) {
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
    }



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
    }

    public class MyLocationListener implements BDLocationListener {
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
    }


    public static boolean isOPen(final Context context) {
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
    }

    // TODO: 2017/6/13 继续进行抽取
    private void showDeleteByIdDialog(View view, final String deleteString) {

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
    }


    private void moveMap(LatLng point) {
        MapStatus mMapStatus = new MapStatus.Builder()
                .target(point)
                .zoom(16f)
                .build();
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        mBaiduMap.setMapStatus(mMapStatusUpdate);
    }
}