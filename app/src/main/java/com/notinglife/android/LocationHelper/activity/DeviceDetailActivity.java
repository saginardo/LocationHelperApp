package com.notinglife.android.LocationHelper.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.notinglife.android.LocationHelper.R;
import com.notinglife.android.LocationHelper.dao.DeviceRawDao;
import com.notinglife.android.LocationHelper.domain.LocationDevice;
import com.notinglife.android.LocationHelper.utils.EditTextUtil;
import com.notinglife.android.LocationHelper.utils.LogUtil;
import com.notinglife.android.LocationHelper.utils.RegexValidator;
import com.notinglife.android.LocationHelper.utils.SPUtil;
import com.notinglife.android.LocationHelper.utils.ToastUtil;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.notinglife.android.LocationHelper.R.id.device_detail_edit;


public class DeviceDetailActivity extends AppCompatActivity {

    public static final String LOCATIONDEVICE = "LOCATIONDEVICE";
    public static final String DEVICEPOSITION = "DEVICEPOSITION";

    @BindView(R.id.device_detail_toolBar)
    Toolbar mDeviceDetailToolBar;
    @BindView(R.id.tv_device_id)
    TextView mTvDeviceId;
    @BindView(R.id.tv_device_number)
    EditText mTvDeviceNumber;
    @BindView(R.id.tv_device_mac)
    TextView mTvDeviceMac;
    @BindView(R.id.tv_lat)
    TextView mTvLat;
    @BindView(R.id.tv_lat_info)
    TextView mTvLatInfo;
    @BindView(R.id.tv_lng)
    TextView mTvLng;
    @BindView(R.id.tv_lng_info)
    TextView mTvLngInfo;
    @BindView(R.id.mapview)
    MapView mMapView;
    @BindView(R.id.macEditText1)
    EditText mMacEditText1;
    @BindView(R.id.macEditText2)
    EditText mMacEditText2;
    @BindView(R.id.macEditText3)
    EditText mMacEditText3;
    @BindView(R.id.macEditText4)
    EditText mMacEditText4;
    @BindView(R.id.macEditText5)
    EditText mMacEditText5;
    @BindView(R.id.macEditText6)
    EditText mMacEditText6;
    @BindView(R.id.bt_relocation)
    TextView mBtRelocation;


    private BaiduMap mBaiduMap;
    private Boolean isFirstLocate = true;
    private static final String TAG = "DeviceDetailActivity";
    private static final String IS_TO_SEND_LOCATION_DATA = "is_to_send_location_data";
    private LocationDevice mLocationDevice;

    private final static int ON_RECEIVE_LOCATION_DATA = 5;
    private static final int ON_EDIT_DEVICE = 7;

    private MenuItem mToolBarEdit;
    private MenuItem mToolBarSave;
    private MenuItem mToolBarDelete;
    private MyHandler mHandler;
    private MyReceiver mReceiver;
    private LocalBroadcastManager mBroadcastManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_detail);

        ButterKnife.bind(this);
        SDKInitializer.initialize(getApplicationContext());

        setSupportActionBar(mDeviceDetailToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);

        initView();

    }

    private void initView() {
        //动态注册本地广播
        mBroadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.notinglife.android.action.ON_RECEIVE_LOCATION_DATA"); //注册接收百度地图回显信息
        mReceiver = new MyReceiver();
        mBroadcastManager.registerReceiver(mReceiver, filter);

        //添加handler用于更新UI
        mHandler = new MyHandler(this);


        Bundle bundle = getIntent().getBundleExtra(LOCATIONDEVICE);
        mLocationDevice = (LocationDevice) bundle.getSerializable(LOCATIONDEVICE);
        if (mLocationDevice != null) {
            //设置设备信息回显
            mTvDeviceNumber.setText(mLocationDevice.mDeviceID);
            String macAddress = mLocationDevice.mMacAddress;
            String[] split = macAddress.split(":");
            mMacEditText1.setText(split[0]);
            mMacEditText2.setText(split[1]);
            mMacEditText3.setText(split[2]);
            mMacEditText4.setText(split[3]);
            mMacEditText5.setText(split[4]);
            mMacEditText6.setText(split[5]);
            mTvLatInfo.setText(mLocationDevice.mLatitude);
            mTvLngInfo.setText(mLocationDevice.mLongitude);

            EditTextUtil.editTextToUpperCase(mTvDeviceNumber, mMacEditText1, mMacEditText2,
                    mMacEditText3, mMacEditText4, mMacEditText5, mMacEditText6);

            //默认只能查看设备信息，不能编辑
            EditTextUtil.ISTOEDIT(false, mTvDeviceNumber, mMacEditText1, mMacEditText2,
                    mMacEditText3, mMacEditText4, mMacEditText5, mMacEditText6);


        }

        //是否从AcqDataFragment获取新的定位信息
        mBtRelocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean value = SPUtil.getBoolean(getApplicationContext(),
                        IS_TO_SEND_LOCATION_DATA, false);

                if(SPUtil.getBoolean(DeviceDetailActivity.this,"ISLOCATING",false)){
                    if(value){
                        //如果获取，则再点一下为不获取
                        ToastUtil.showShortToast(getApplicationContext(),"已停止获取定位信息");
                        SPUtil.setBoolean(getApplicationContext(),
                                IS_TO_SEND_LOCATION_DATA, false);
                    }else {
                        //如果不获取，则再点一下为获取
                        ToastUtil.showShortToast(getApplicationContext(),"正在重新获取定位信息");
                        SPUtil.setBoolean(getApplicationContext(),
                                IS_TO_SEND_LOCATION_DATA, true);
                    }
                }else {
                    ToastUtil.showShortToast(getApplicationContext(),"请在数据采集页面重新获取定位信息");
                }


            }
        });

        //设置地图点
        LatLng ll = new LatLng(Double.parseDouble(mLocationDevice.mLatitude), Double.parseDouble(mLocationDevice.mLongitude));
        navigateTo(ll);
    }


    //用于更新定位数据的Handler的UI
    private static class MyHandler extends Handler {

        WeakReference<DeviceDetailActivity> mDetailActivity;

        MyHandler(DeviceDetailActivity deviceDetailActivity) {
            mDetailActivity = new WeakReference<>(deviceDetailActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            DeviceDetailActivity deviceDetailActivity = mDetailActivity.get();
            if (deviceDetailActivity != null) {// 先判断弱引用是否为空，为空则不更新UI
                int flag = msg.what;
                //接收注销本地广播，更新UI等逻辑
                if (flag == ON_RECEIVE_LOCATION_DATA) {
                    LogUtil.i(TAG, msg.obj.toString());
                    boolean receiveData = SPUtil.getBoolean(deviceDetailActivity.getApplicationContext(),
                            IS_TO_SEND_LOCATION_DATA, false);
                    if (receiveData) {
                        LocationDevice locationDevice = (LocationDevice) msg.obj;
                        deviceDetailActivity.mTvLatInfo.setText(locationDevice.mLatitude);
                        deviceDetailActivity.mTvLngInfo.setText(locationDevice.mLongitude);
                    }
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
            LocationDevice tmpDevice = (LocationDevice) intent.getBundleExtra("ON_RECEIVE_LOCATION_DATA").getSerializable("ON_RECEIVE_LOCATION_DATA");
            if (flag == ON_RECEIVE_LOCATION_DATA) {
                Message message = Message.obtain();
                message.what = ON_RECEIVE_LOCATION_DATA;
                message.obj = tmpDevice;
                mHandler.sendMessage(message);
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.device_detail_toolbar, menu);
        mToolBarEdit = menu.findItem(R.id.device_detail_edit);
        mToolBarSave = menu.findItem(R.id.device_detail_save);
        mToolBarDelete = menu.findItem(R.id.device_detail_delete);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case device_detail_edit:
                //LogUtil.i(TAG, "设备编辑按钮被点击");

                //设置设备信息可编辑
                EditTextUtil.ISTOEDIT(true, mTvDeviceNumber, mMacEditText1, mMacEditText2,
                        mMacEditText3, mMacEditText4, mMacEditText5, mMacEditText6);
                mToolBarEdit.setVisible(false);
                mToolBarSave.setVisible(true);
                mBtRelocation.setVisibility(View.VISIBLE);
                return true;
            case R.id.device_detail_save:

                mToolBarEdit.setVisible(true);
                EditTextUtil.ISTOEDIT(false, mTvDeviceNumber, mMacEditText1, mMacEditText2,
                        mMacEditText3, mMacEditText4, mMacEditText5, mMacEditText6);
                mToolBarSave.setVisible(false);
                mBtRelocation.setVisibility(View.GONE);

                //保存修改前的设备信息
                LocationDevice beforeChangeDevice = mLocationDevice;
                LogUtil.i(TAG, "修改前设备信息：" + beforeChangeDevice.toString());

                //获取修改后的信息
                String tmp1 = mTvDeviceNumber.getText().toString();
                String tmp2 = EditTextUtil.generateMacAddress(mMacEditText1, mMacEditText2,
                        mMacEditText3, mMacEditText4, mMacEditText5, mMacEditText6);

                //判断是否直接修改为空值或者不合适的值
                //设备号不服 或者 MAC地址不匹配 提示用户
                if (!RegexValidator.isDeviceID(tmp1)) {
                    ToastUtil.showShortToast(getApplicationContext(), "设备ID输入错误");
                    return true;
                }
                if (!RegexValidator.isMacAddress(tmp2)) {
                    ToastUtil.showShortToast(getApplicationContext(), "设备MAC地址输入错误");
                    return true;
                }

                //对比本地数据库是否重复添加，也允许修改部分属性
                DeviceRawDao mDao = new DeviceRawDao(getApplicationContext());
                LocationDevice tmpDevice1 = mDao.queryById(tmp1);
                if (tmpDevice1 != null) {
                    //查出其他设备号，且不为修改前设备号，即修改后和其他设备重复；
                    LogUtil.i(TAG, "按照ID查询出的设备信息：" + tmpDevice1.toString());
                    if (!tmpDevice1.mDeviceID.equals(beforeChangeDevice.mDeviceID)) {
                        ToastUtil.showShortToast(getApplicationContext(), "设备号重复，不能修改");
                        return true;
                    }

                }
                LocationDevice tmpDevice2 = mDao.queryByMac(tmp2);
                if (tmpDevice2 != null) {
                    //查出其他设备的mac地址，且不为修改前地址，即修改后和其他设备重复；
                    LogUtil.i(TAG, "按照MAC查询出的设备信息：" + tmpDevice2.toString());
                    if (!tmpDevice2.mMacAddress.equals(beforeChangeDevice.mMacAddress)) {
                        ToastUtil.showShortToast(getApplicationContext(), "设备MAC地址重复，不能修改");
                        return true;
                    }

                }

                //都没问题后，进行覆盖操作
                mLocationDevice.mDeviceID = tmp1;
                mLocationDevice.mMacAddress = tmp2;

                //发送DATA_CHANGED本地广播
                Intent intent = new Intent("com.notinglife.android.action.DATA_CHANGED");
                intent.putExtra("flag", ON_EDIT_DEVICE);
                intent.putExtra(DEVICEPOSITION, getIntent().getIntExtra(DEVICEPOSITION, -1)); //把设备position传回 DeviceListFragment中，用于更新UI
                Bundle bundle = new Bundle();
                bundle.putSerializable("on_edit_device", mLocationDevice);
                intent.putExtra("on_edit_device", bundle);
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                ToastUtil.showShortToast(this, "成功保存修改");

                return true;
            case R.id.device_detail_delete:
                LogUtil.i(TAG, "设备删除按钮被点击");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
        mBaiduMap.setMyLocationEnabled(true);
        mBroadcastManager.unregisterReceiver(mReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }

    //移动地图位置
    private void navigateTo(LatLng latLng) {
        if (isFirstLocate) {
            //LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
            //LogUtil.i("当前经纬度：" + ll.toString());

            MapStatus mMapStatus = new MapStatus.Builder()
                    .target(latLng)
                    .zoom(16f)
                    .build();
            MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);

            mBaiduMap.setMyLocationEnabled(true);
            MyLocationData.Builder builder = new MyLocationData.Builder();
            builder.latitude(latLng.latitude);
            builder.longitude(latLng.longitude);
            MyLocationData locationData = builder.build();
            mBaiduMap.setMyLocationData(locationData);

            mBaiduMap.setMapStatus(mMapStatusUpdate);
            isFirstLocate = false;
        }
    }
}
