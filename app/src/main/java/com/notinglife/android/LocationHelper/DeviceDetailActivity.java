package com.notinglife.android.LocationHelper;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import com.notinglife.android.LocationHelper.domain.LocationDevice;

import butterknife.BindView;
import butterknife.ButterKnife;


public class DeviceDetailActivity extends AppCompatActivity {

    public static final String LOCATIONDEVICE = "LOCATIONDEVICE";

    //@BindView(R.id.device_detail_image_view)
    //ImageView mDeviceDetailImageView;
    //@BindView(R.id.toolBar)
    //Toolbar mToolBar;
    //@BindView(R.id.collapsing_toolbar)
   // CollapsingToolbarLayout mCollapsingToolbar;
    //@BindView(R.id.device_detail_appBar)
    //AppBarLayout mDeviceDetailAppBar;
    //@BindView(R.id.fab)
    //FloatingActionButton mFab;
    @BindView(R.id.tv_device_backspace)
    TextView mBackspace;

    @BindView(R.id.tv_device_id)
    TextView mTextView;

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
    @BindView(R.id.tv_lat_info)
    TextView mLatTextView;
    @BindView(R.id.tv_lng_info)
    TextView mLngTextView;

    @BindView(R.id.mapview)
    MapView mMapView;

    private BaiduMap mBaiduMap;
    private Boolean isFirstLocate = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_detail);

        ButterKnife.bind(this);
        SDKInitializer.initialize(getApplicationContext());


        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        //实现回退功能
        mBackspace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Bundle bundle = getIntent().getBundleExtra(LOCATIONDEVICE);
        LocationDevice locationdevice =(LocationDevice) bundle.getSerializable(LOCATIONDEVICE);
        if(locationdevice!=null){
            //mCollapsingToolbar.setTitle("设备编号:"+locationdevice.mDeivceId);
            mTextView.setText("设备编号:"+locationdevice.mDeivceId);

            String macAddress = locationdevice.mMacAddress;
            String[] split = macAddress.split(":");
            mMacAddress_1.setText(split[0]);
            mMacAddress_2.setText(split[1]);
            mMacAddress_3.setText(split[2]);
            mMacAddress_4.setText(split[3]);
            mMacAddress_5.setText(split[4]);
            mMacAddress_6.setText(split[5]);

            mLatTextView.setText(locationdevice.mLatitude);
            mLngTextView.setText(locationdevice.mLongitude);
        }

        LatLng ll = new LatLng(Double.parseDouble(locationdevice.mLatitude), Double.parseDouble(locationdevice.mLongitude));
        navigateTo(ll);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
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
