package com.notinglife.android.LocationHelper.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.DisplayMetrics;
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
import com.baidu.mapapi.map.BaiduMap;
import com.notinglife.android.LocationHelper.R;
import com.notinglife.android.LocationHelper.dao.DeviceRawDao;
import com.notinglife.android.LocationHelper.domain.LocationDevice;
import com.notinglife.android.LocationHelper.ui.EditDialog;
import com.notinglife.android.LocationHelper.utils.EditTextUtil;
import com.notinglife.android.LocationHelper.utils.LogUtil;
import com.notinglife.android.LocationHelper.utils.ToastUtil;

import java.lang.ref.WeakReference;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * ${DESCRIPTION}
 *
 * @author saginardo
 * @version ${VERSION}
 *          date 2017-06-13 19:25
 */

public class ContentFragment extends BaseFragment implements View.OnClickListener {


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

    private DeviceRawDao mDao;
    private LocationClient mLocationClient;
    private List<String> permissionList;
    private BaiduMap mBaiduMap;
    private boolean isFirstLocate = true;
    private boolean isFirstDrawPoint = true;
    private boolean isFirstMark = true;
    private boolean isStopLocate = false;

    private LocationDevice mLocationDevice;
    private LocationDevice tmpDevice;
    private int mLocModeValue = -1;
    private EditDialog mEditDialog;

    private MyHandler mHandler;//用于更新UI的handler

    private Handler handler; //用于传递数据的handler
    public void setHandler(Handler handler){
        this.handler = handler;
    }

    //showdialog标志位
    private final static int DELETE_BY_ID = 0;
    private final static int DELETE_ALL = 1;
    private final static int UPDATE_DEVICE = 2;
    private final static int ON_SAVE_DATA = 3; //触发保存数据的标志位
    private final static int UNDO_SAVE = 4;
    private final static int ON_RECEIVE_LOCATION_DATA = 5;

    @Override
    public View initView() {

        View view = View.inflate(mActivity, R.layout.fragment_acq_data, null);

        ButterKnife.bind(this, view);

        mStartLocation.setOnClickListener(this);
        mStopLocation.setOnClickListener(this);
        mSaveButton.setOnClickListener(this);
        // FIXED: 2017/6/13 撤销对话框还没有重构
        mUndoSave.setOnClickListener(this);
        //设置输入框的属性
        EditTextUtil.editTextToUpperCase(mDeviceId,mMacAddress_1, mMacAddress_2,
                mMacAddress_3, mMacAddress_4, mMacAddress_5, mMacAddress_6);
        return view;
    }


    @Override
    public void initData() {

        mDao = new DeviceRawDao(mActivity);
        //百度地图定位sdk初始化
        mLocationDevice = new LocationDevice();
        mLocationClient = new LocationClient(mActivity);
        mLocationClient.registerLocationListener(new MyLocationListener());
        initLocation();

        mHandler = new MyHandler(ContentFragment.this);
    }


    private static class MyHandler extends Handler {

        WeakReference<ContentFragment> mFragment;

        MyHandler(ContentFragment fragment) {
            mFragment = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ContentFragment fragment = mFragment.get();
            if (fragment != null) {// 先判断弱引用是否为空，为空则不更新UI

                int flag = msg.what;
                //接收到撤销请求，显示并更新对话框UI
                if (flag == UNDO_SAVE) {
                    int position = msg.arg1;
                    LocationDevice tmpDevice = (LocationDevice) msg.obj;
                    if (tmpDevice != null) {
                        //撤销上一次保存设备
                        fragment.mDao.deleteById(tmpDevice.mId);
                        ToastUtil.showShortToast(fragment.mActivity, "成功撤销上一次保存");
                    }
                }
                //接收到位置信息回调，更新UI中textviw数据
                if (flag == ON_RECEIVE_LOCATION_DATA) {
                    LocationDevice tmpDevice = (LocationDevice) msg.obj;
                    fragment.mLatTextView.setText(tmpDevice.mLatitude);
                    fragment.mLngTextView.setText(tmpDevice.mLongitude);
                    fragment.mRadiusValue.setText(tmpDevice.mRadius + "米");

                    if (tmpDevice.mLocMode == BDLocation.TypeGpsLocation) {
                        fragment.mLocMode.setText("GPS定位");
                    } else {
                        fragment.mLocMode.setText("网络定位");
                    }
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        List<LocationDevice> list = null;
        LocationDevice deviceToDo = null;//查询出来的临时对象

        switch (v.getId()) {
            case R.id.bt_start_gps:
                if (!isOPen(mActivity)) {
                    ToastUtil.showShortToast(mActivity, "请先打开GPS");
                    // 转到手机设置界面，用户设置GPS
                    Intent intent = new Intent(
                            Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                } else {
                    ToastUtil.showShortToast(mActivity, "定位中");
                    mLocationClient.start();
                    isStopLocate = false;
                }
                break;

            case R.id.bt_stop_gps:
                // FIXED: 2017/6/14 增加一个判断符，如果停止定位了 必须重新开启才允许保存数据
                ToastUtil.showShortToast(mActivity, "停止定位");
                mLocationClient.stop();
                isStopLocate = true;
                break;

            case R.id.bt_save_data:

                if (!isStopLocate) {
                    // TODO: 2017/6/14 判断逻辑过重，考虑是否增加独立方法来判断
                    //mLocationDevice是new出来的，自身一定不为null，但是其属性值全为null

                    if (!TextUtils.isEmpty(mLocationDevice.mLatitude)) {

                        String deviceId = mDeviceId.getText().toString();


                        String macAddress = EditTextUtil.generateMacAddress(mMacAddress_1, mMacAddress_2,
                                mMacAddress_3, mMacAddress_4, mMacAddress_5, mMacAddress_6);

                        LogUtil.i(macAddress);
                        if (deviceId.equals("")) {
                            ToastUtil.showShortToast(mActivity, "设备号码不能为空");
                            return;
                        }
                        if (macAddress != null && macAddress.equals("")) {
                            ToastUtil.showShortToast(mActivity, "MAC地址不能为空");
                            return;
                        }
                        if (macAddress != null && macAddress.length() < 17) {
                            ToastUtil.showShortToast(mActivity, "MAC地址信息不全");
                            return;
                        }

                        mLocationDevice.mDeivceId = deviceId;
                        mLocationDevice.mMacAddress = macAddress;

                        LogUtil.i("设备ID:" + mLocationDevice.mDeivceId + " ,MAC地址: " + mLocationDevice.mMacAddress + ", 经度为："
                                + mLocationDevice.mLatitude + ", 纬度为：" + mLocationDevice.mLongitude);

                        if (mLocationDevice.mLocMode != BDLocation.TypeGpsLocation) {
                            ToastUtil.showShortToast(mActivity, "请等待GPS定位数据");
                            return;
                        }

                        deviceToDo = mDao.queryById(deviceId);
                        if (deviceToDo != null) {
                            ToastUtil.showShortToast(mActivity, "不能重复添加设备，请查看设备是否已保存");
                            return;
                        }
                        mDao.add(mLocationDevice);
                        this.tmpDevice = new LocationDevice();
                        this.tmpDevice = mLocationDevice;

                        Message message = new Message();
                        message.what = ON_SAVE_DATA;
                        message.obj = mLocationDevice;
                        if(handler!=null){
                            handler.sendMessage(message);
                        }
                        ToastUtil.showShortToast(mActivity, "添加设备信息成功");
                    } else {
                        ToastUtil.showShortToast(mActivity, "请先获取GPS地址");
                    }

                } else {
                    ToastUtil.showShortToast(mActivity, "请重新获取定位信息");
                }
                break;

            case R.id.bt_undo_save:
                //// TODO: 2017/6/13 自增ID最大的是否是最近需要撤销的
                // tmpDevice
                if (tmpDevice == null) {
                    ToastUtil.showShortToast(mActivity, "还未保存设备，无法撤销保存");
                    return;
                }
                LogUtil.i(tmpDevice.toString());
                deviceToDo = mDao.queryById(tmpDevice.mDeivceId);
                if (deviceToDo == null) {
                    ToastUtil.showShortToast(mActivity, "还未保存该设备，无法撤销保存");
                    return;
                }
                // FIXED: 2017/6/13 重构对话框
                showDialog(v, "是否撤销如下保存", null, tmpDevice, -1, UNDO_SAVE);

            default:
                break;
        }
    }

    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(final BDLocation bdLocation) {
/*            if (bdLocation.getLocType() == BDLocation.TypeGpsLocation
                    || bdLocation.getLocType() == BDLocation.TypeNetWorkLocation) {
                navigateTo(bdLocation);
            }*/
            mLocationDevice.mLatitude = String.valueOf(bdLocation.getLatitude());
            mLocationDevice.mLongitude = String.valueOf(bdLocation.getLongitude());
            mLocationDevice.mRadius = String.valueOf(bdLocation.getRadius());
            mLocationDevice.mLocMode = bdLocation.getLocType();

            Message locationMsg = Message.obtain();
            locationMsg.what = ON_RECEIVE_LOCATION_DATA;
            locationMsg.obj = mLocationDevice;
            mHandler.sendMessage(locationMsg);
        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {
        }

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mLocationClient.stop();
    }

    public static boolean isOPen(final Context context) {
        LocationManager locationManager
                = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        //boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (gps) {
            return true;
        }
        return false;
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
                            Toast.makeText(mActivity, "必须同意所以权限", Toast.LENGTH_LONG).show();
                            //finish();
                            return;
                        }
                    }
                    initLocation();
                } else {
                    //finish();
                }
                break;
            default:
        }
    }

    //还是通过 equals方法获取在mList中的位置
    private void showDialog(View view, String title, String message, final LocationDevice locationDevice, final int position, final int flag) {

        mEditDialog = new EditDialog(mActivity, title, message);
        mEditDialog.setDeviceInfo(locationDevice);
        mEditDialog.setFlag(flag);

        //自定义窗体参数
        WindowManager.LayoutParams attributes = mEditDialog.getWindow().getAttributes();
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        attributes.width = (int) (metrics.widthPixels * 0.9);
        attributes.height = (int) (metrics.heightPixels * 0.9);
        attributes.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        attributes.dimAmount = 0.5f;
        mEditDialog.getWindow().setAttributes(attributes);

        final LocationDevice[] tmpDevice = {new LocationDevice()};
        //响应确定键的点击事件
        mEditDialog.setPositiveOnclickListener(new EditDialog.onPositiveOnclickListener() {
            @Override
            public void onPositiveClick() {
                Message msg = Message.obtain();
                //清除所有数据的对话框
                if (flag == DELETE_ALL) {
                    msg.what = flag;
                    msg.obj = null;
                    msg.arg1 = -1;
                    mHandler.sendMessage(msg);
                } else if (flag == UNDO_SAVE) {
                    //撤销保存对话框
                    msg.what = flag;
                    msg.obj = locationDevice;
                    msg.arg1 = -1;
                } else {
                    tmpDevice[0] = mEditDialog.getDeviceInfo();
                    msg.what = flag;
                    msg.obj = tmpDevice[0];
                    msg.arg1 = position;
                    mHandler.sendMessage(msg);
                }
                mEditDialog.dismiss();
            }
        });

        //响应取消键的点击事件
        mEditDialog.setNegativeOnclickListener(new EditDialog.onNegativeOnclickListener() {
            @Override
            public void onNegativeClick() {
                mEditDialog.dismiss();
            }
        });
        //展示对话框
        mEditDialog.show();
    }
}
