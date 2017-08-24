package com.notinglife.android.LocationHelper.activity;

import android.app.Activity;
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
import android.widget.Button;
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
import com.notinglife.android.LocationHelper.utils.DialogUtil;
import com.notinglife.android.LocationHelper.utils.EditTextUtil;
import com.notinglife.android.LocationHelper.utils.GlobalConstant;
import com.notinglife.android.LocationHelper.utils.MyLocalBroadcastManager;
import com.notinglife.android.LocationHelper.utils.RegexValidator;
import com.notinglife.android.LocationHelper.utils.SPUtil;
import com.notinglife.android.LocationHelper.utils.ToastUtil;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

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
    Button mBtRelocation;
    @BindView(R.id.tv_device_status)
    TextView mTvDeviceStatus;
    @BindView(R.id.tv_device_status_info)
    TextView mTvDeviceStatusInfo;


    private BaiduMap mBaiduMap;
    private static final String TAG = "DeviceDetailActivity";

    private static final String ToSendLocation = "ToSendLocation";
    private static final String IsLocation = "IsLocation";

    private LocationDevice mLocationDevice;
    private Unbinder mUnBinder;

    private final static int DELETE_BY_ID = 0;
    private final static int ON_RECEIVE_LOCATION_DATA = 5;
    private static final int ON_EDIT_DEVICE = 7;

    private final static int LOCATION_DEVICE_STATUS = 42;

    private MenuItem mToolBarEdit;
    private MenuItem mToolBarSave;
    private MenuItem mToolBarDelete;
    private MyHandler mHandler;
    private MyReceiver mReceiver;
    private LocalBroadcastManager mBroadcastManager;
    private int mPosition;
    private Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_detail);
        mActivity = this;

        mUnBinder = ButterKnife.bind(this);
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
        filter.addAction(GlobalConstant.ACTION_ON_RECEIVE_LOCATION_DATA); //注册接收百度地图回显信息
        mReceiver = new MyReceiver();
        mBroadcastManager.registerReceiver(mReceiver, filter);

        //添加handler用于更新UI
        mHandler = new MyHandler(this);

        Bundle bundle = getIntent().getBundleExtra(LOCATIONDEVICE);
        mPosition = getIntent().getIntExtra(DEVICEPOSITION, -1);
        mLocationDevice = (LocationDevice) bundle.getSerializable(LOCATIONDEVICE);

        if (mLocationDevice != null) {
            //设置设备信息回显
            mTvDeviceNumber.setText(mLocationDevice.mDeviceID);
            //依据状态 显示设备状态信息
            switch (mLocationDevice.mStatus) {
                case "Normal":
                    mTvDeviceStatusInfo.setText("正常");
                    mTvDeviceStatusInfo.setTextColor(getResources().getColor(R.color.colorLitleBlack));//改变颜色,正常为黑色
                    break;
                case "OffLine":
                    mTvDeviceStatusInfo.setText("离线");
                    mTvDeviceStatusInfo.setTextColor(getResources().getColor(R.color.colorRed));//改变颜色,离线为红色
                    break;
                default:
                    break;
            }

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
            public void onClick(View view) {
                //先判断 主页面是否在定位中
                if (SPUtil.getBoolean(DeviceDetailActivity.this, IsLocation, false)) {
                    if (SPUtil.getBoolean(getApplicationContext(),
                            ToSendLocation, false)) {
                        //如果获取，则再点一下为不获取
                        mBtRelocation.setSelected(false);
                        ToastUtil.showShortToast(getApplicationContext(), "已停止获取定位信息");
                        SPUtil.setBoolean(getApplicationContext(),
                                ToSendLocation, false);
                    } else {
                        //如果不获取，则再点一下为获取
                        mBtRelocation.setSelected(true);
                        ToastUtil.showShortToast(getApplicationContext(), "正在重新获取定位信息");
                        SPUtil.setBoolean(getApplicationContext(),
                                ToSendLocation, true);
                    }
                } else {
                    ToastUtil.showShortToast(getApplicationContext(), "请在数据采集页面重新获取定位信息");
                }
            }
        });

        //设置地图点
        LatLng ll = new LatLng(Double.parseDouble(mLocationDevice.mLatitude), Double.parseDouble(mLocationDevice.mLongitude));
        // TODO: 2017/6/29  应该设置marker，而不是我的位置
        navigateTo(ll);
    }

    //用于更新定位数据的Handler的UI
    private static class MyHandler extends Handler {
        WeakReference<DeviceDetailActivity> mActivity;

        MyHandler(DeviceDetailActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            DeviceDetailActivity activity = mActivity.get();
            if (activity != null) {// 先判断弱引用是否为空，为空则不更新UI
                int flag = msg.what;

                if (flag == ON_RECEIVE_LOCATION_DATA) {
                    //LogUtil.i(TAG, msg.obj.toString());
                    boolean receiveData = SPUtil.getBoolean(activity.getApplicationContext(),
                            ToSendLocation, false);
                    if (receiveData) {
                        LocationDevice locationDevice = (LocationDevice) msg.obj;
                        activity.mTvLatInfo.setText(locationDevice.mLatitude);
                        activity.mTvLngInfo.setText(locationDevice.mLongitude);
                        LatLng ll = new LatLng(Double.parseDouble(locationDevice.mLatitude), Double.parseDouble(locationDevice.mLongitude));
                        activity.navigateTo(ll);
                    }
                }
                //本类，菜单删除设备的对话框事件
                if (flag == DELETE_BY_ID) {
                    //发送删除单条设备的广播，让DeviceListFragment去删除
                    MyLocalBroadcastManager.sendLocalBroadcast(activity, "DATA_CHANGED", DELETE_BY_ID, DEVICEPOSITION,
                            activity.mPosition, "DEVICE_DATA", activity.mLocationDevice);
                    activity.finish();
                }
                //改变设备状态的对话框事件
                if (flag == LOCATION_DEVICE_STATUS) {
                    String status = (String) msg.obj;
                    switch (status) {
                        case "正常":
                            activity.mLocationDevice.mStatus = "Normal";
                            activity.mTvDeviceStatusInfo.setText(status);
                            activity.mTvDeviceStatusInfo.setTextColor(activity.getResources().getColor(R.color.colorLitleBlack)); //改变颜色
                            break;
                        case "离线":
                            activity.mLocationDevice.mStatus = "OffLine";
                            activity.mTvDeviceStatusInfo.setText(status);
                            activity.mTvDeviceStatusInfo.setTextColor(activity.getResources().getColor(R.color.colorRed));//改变颜色
                            break;
                        default:
                            break;
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
            Message message = Message.obtain();
            message.what = flag;

            if (flag == ON_RECEIVE_LOCATION_DATA) {
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
                mTvDeviceStatusInfo.setClickable(true);
                mTvDeviceStatusInfo.setTextColor(getResources().getColor(R.color.colorBlue));
                mTvDeviceStatusInfo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DialogUtil.showChoiceDialog(mActivity, mHandler, "请选择设备维护后状态", mLocationDevice.mStatus, LOCATION_DEVICE_STATUS, "正常", "离线");
                    }
                });

                mBtRelocation.setVisibility(View.VISIBLE);
                return true;
            case R.id.device_detail_save:

                //保存修改前的设备信息
                LocationDevice beforeChangeDevice = mLocationDevice;
                //LogUtil.i(TAG, "修改前设备信息：" + beforeChangeDevice.toString());
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
                    //LogUtil.i(TAG, "按照ID查询出的设备信息：" + tmpDevice1.toString());
                    if (!tmpDevice1.mDeviceID.equals(beforeChangeDevice.mDeviceID)) {
                        ToastUtil.showShortToast(getApplicationContext(), "设备号重复，不能修改");
                        return true;
                    }
                }
                LocationDevice tmpDevice2 = mDao.queryByMac(tmp2);
                if (tmpDevice2 != null) {
                    //查出其他设备的mac地址，且不为修改前地址，即修改后和其他设备重复；
                    //LogUtil.i(TAG, "按照MAC查询出的设备信息：" + tmpDevice2.toString());
                    if (!tmpDevice2.mMacAddress.equals(beforeChangeDevice.mMacAddress)) {
                        ToastUtil.showShortToast(getApplicationContext(), "设备MAC地址重复，不能修改");
                        return true;
                    }
                }
                //都没问题后，进行覆盖操作;
                // 这里不需要操作数据库，因为用户看到的是保存好的界面，回退到DeviceListFragment中，数据已经更新，重新进来也是新的数据
                mLocationDevice.mDeviceID = tmp1;
                mLocationDevice.mMacAddress = tmp2;
                mLocationDevice.mLatitude = mTvLatInfo.getText().toString();
                mLocationDevice.mLongitude = mTvLngInfo.getText().toString();

                //修改后，发送广播，通知DeviceListFragment和RepairDevicesActivity更新数据；position相互影响，不传递
                MyLocalBroadcastManager.sendLocalBroadcast(this, "DATA_CHANGED", ON_EDIT_DEVICE,
                        DEVICEPOSITION, -1, "DEVICE_DATA", mLocationDevice);
                ToastUtil.showShortToast(this, "成功保存修改");

                //修改成功才改变UI
                mToolBarEdit.setVisible(true);
                EditTextUtil.ISTOEDIT(false, mTvDeviceNumber, mMacEditText1, mMacEditText2,
                        mMacEditText3, mMacEditText4, mMacEditText5, mMacEditText6);
                mToolBarSave.setVisible(false);
                mTvDeviceStatusInfo.setClickable(false);
                mBtRelocation.setVisibility(View.GONE);

                //修改成功，停止发送数据
                SPUtil.getBoolean(getApplicationContext(), ToSendLocation, false);
                return true;

            case R.id.device_detail_delete:
                DialogUtil.showDeviceEditDialog(this, mHandler, "删除设备", "请确认删除设备", null, -1, DELETE_BY_ID);
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
        mUnBinder.unbind();
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
        //LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
        //LogUtil.i(TAG, "当前经纬度：" + latLng.toString());

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
    }
}
