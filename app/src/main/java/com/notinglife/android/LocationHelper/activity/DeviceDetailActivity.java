package com.notinglife.android.LocationHelper.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
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
import com.notinglife.android.LocationHelper.utils.ToastUtil;

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


    private BaiduMap mBaiduMap;
    private Boolean isFirstLocate = true;
    private static final String TAG = "DeviceDetailActivity";
    private LocationDevice mLocationdevice;

    private static final int ON_EDIT_DEVICE = 7;
    private MenuItem mToolBarEdit;
    private MenuItem mToolBarSave;
    private MenuItem mToolBarDelete;

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
        Bundle bundle = getIntent().getBundleExtra(LOCATIONDEVICE);
        mLocationdevice = (LocationDevice) bundle.getSerializable(LOCATIONDEVICE);
        if (mLocationdevice != null) {
            //设置设备信息回显
            mTvDeviceNumber.setText(mLocationdevice.mDeviceID);
            String macAddress = mLocationdevice.mMacAddress;
            String[] split = macAddress.split(":");
            mMacEditText1.setText(split[0]);
            mMacEditText2.setText(split[1]);
            mMacEditText3.setText(split[2]);
            mMacEditText4.setText(split[3]);
            mMacEditText5.setText(split[4]);
            mMacEditText6.setText(split[5]);
            mTvLatInfo.setText(mLocationdevice.mLatitude);
            mTvLngInfo.setText(mLocationdevice.mLongitude);

            EditTextUtil.editTextToUpperCase(mTvDeviceNumber, mMacEditText1, mMacEditText2,
                    mMacEditText3, mMacEditText4, mMacEditText5, mMacEditText6);

            //默认只能查看设备信息，不能编辑
            EditTextUtil.ISTOEDIT(false,mTvDeviceNumber, mMacEditText1, mMacEditText2,
                    mMacEditText3, mMacEditText4, mMacEditText5, mMacEditText6);


        }

        //设置地图点
        LatLng ll = new LatLng(Double.parseDouble(mLocationdevice.mLatitude), Double.parseDouble(mLocationdevice.mLongitude));
        navigateTo(ll);
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
                EditTextUtil.ISTOEDIT(true,mTvDeviceNumber, mMacEditText1, mMacEditText2,
                        mMacEditText3, mMacEditText4, mMacEditText5, mMacEditText6);
                mToolBarEdit.setVisible(false);
                mToolBarSave.setVisible(true);
                return true;
            case R.id.device_detail_save:

                mToolBarEdit.setVisible(true);
                EditTextUtil.ISTOEDIT(false,mTvDeviceNumber, mMacEditText1, mMacEditText2,
                        mMacEditText3, mMacEditText4, mMacEditText5, mMacEditText6);
                mToolBarSave.setVisible(false);

                // TODO: 2017/6/25 是否添加重新获取定位数据的功能
                //保存修改前的设备信息
                LocationDevice beforeChangeDevice = mLocationdevice;
                LogUtil.i(TAG,"修改前设备信息："+ beforeChangeDevice.toString());

                //获取修改后的信息
                String tmp1 = mTvDeviceNumber.getText().toString();
                String tmp2 = EditTextUtil.generateMacAddress(mMacEditText1, mMacEditText2,
                        mMacEditText3, mMacEditText4, mMacEditText5, mMacEditText6);

                //判断是否直接修改为空值或者不合适的值
                    //设备号不服 或者 MAC地址不匹配 提示用户
                if(!RegexValidator.isDeviceID(tmp1)){
                    ToastUtil.showShortToast(getApplicationContext(),"设备ID输入错误");

                    return true;
                }
                if(!RegexValidator.isMacAddress(tmp2)){
                    ToastUtil.showShortToast(getApplicationContext(),"设备MAC地址输入错误");

                    return true;
                }

                //没有修改数据，直接点了保存
                if(beforeChangeDevice.mDeviceID.equals(tmp1) && beforeChangeDevice.mMacAddress.equals(tmp2)){
                    ToastUtil.showShortToast(getApplicationContext(),"没有任何修改");

                    return true;
                }

                //对比本地数据库是否重复添加，也允许修改部分属性
                DeviceRawDao mDao = new DeviceRawDao(getApplicationContext());
                LocationDevice tmpDevice1 = mDao.queryById(tmp1);
                if(tmpDevice1!=null){
                    //查出其他设备号，且不为修改前设备号，即修改后和其他设备重复；
                    LogUtil.i(TAG,"按照ID查询出的设备信息："+tmpDevice1.toString());
                    if(!tmpDevice1.mDeviceID.equals(beforeChangeDevice.mDeviceID)){
                        ToastUtil.showShortToast(getApplicationContext(),"设备号重复，不能修改");
                        return true;
                    }

                }
                LocationDevice tmpDevice2 = mDao.queryByMac(tmp2);
                if(tmpDevice2!=null){
                    //查出其他设备的mac地址，且不为修改前地址，即修改后和其他设备重复；
                    LogUtil.i(TAG,"按照MAC查询出的设备信息："+tmpDevice2.toString());
                    if(!tmpDevice2.mMacAddress.equals(beforeChangeDevice.mMacAddress)){
                        ToastUtil.showShortToast(getApplicationContext(),"设备MAC地址重复，不能修改");
                        return true;
                    }

                }

                //都没问题后，进行覆盖操作
                mLocationdevice.mDeviceID = tmp1;
                mLocationdevice.mMacAddress = tmp2;

                //发送本地广播
                Intent intent = new Intent("com.notinglife.android.action.DATA_DELETE");
                intent.putExtra("flag", ON_EDIT_DEVICE);
                intent.putExtra(DEVICEPOSITION,getIntent().getIntExtra(DEVICEPOSITION,-1)); //把设备position传回 DeviceListFragment中，用于更新UI
                Bundle bundle = new Bundle();
                bundle.putSerializable("on_edit_device", mLocationdevice);
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
