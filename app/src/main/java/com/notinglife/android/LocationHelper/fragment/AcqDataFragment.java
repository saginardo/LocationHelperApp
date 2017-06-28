package com.notinglife.android.LocationHelper.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.notinglife.android.LocationHelper.R;
import com.notinglife.android.LocationHelper.activity.CaptureActivity;
import com.notinglife.android.LocationHelper.dao.DeviceRawDao;
import com.notinglife.android.LocationHelper.domain.LocationDevice;
import com.notinglife.android.LocationHelper.utils.DialogUtil;
import com.notinglife.android.LocationHelper.utils.EditTextUtil;
import com.notinglife.android.LocationHelper.utils.ImageUtil;
import com.notinglife.android.LocationHelper.utils.LogUtil;
import com.notinglife.android.LocationHelper.utils.RegexValidator;
import com.notinglife.android.LocationHelper.utils.SPUtil;
import com.notinglife.android.LocationHelper.utils.ToastUtil;
import com.uuzuche.lib_zxing.activity.CodeUtils;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * ${DESCRIPTION}
 *
 * @author saginardo
 * @version ${VERSION}
 *          date 2017-06-13 19:25
 */

public class AcqDataFragment extends Fragment implements View.OnClickListener {


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
    @BindView(R.id.acq_toolbar)
    Toolbar mAcqToolbar;
    @BindView(R.id.tv_lat)
    TextView mTvLat;
    @BindView(R.id.tv_lng)
    TextView mTvLng;
    @BindView(R.id.tv_loc_mode)
    TextView mTvLocMode;
    @BindView(R.id.tv_loc_radius)
    TextView mTvLocRadius;


    private DeviceRawDao mDao;
    private LocationClient mLocationClient;

    private LocationDevice mLocationDevice;
    private LocationDevice undoSaveDevice;
    public Activity mActivity;
    private MyHandler mHandler;//用于更新UI的handler
    private Unbinder mUnbinder;

    private static final String TAG = "AcqDataFragment";

    //SpUtil用的key
    private static final String IsLocation = "IsLocation";
    private static final String ToSendLocation = "ToSendLocation";

    //showdialog 标志位
    private final static int DELETE_BY_ID = 0;
    private final static int DELETE_ALL = 1;
    private final static int UPDATE_DEVICE = 2;
    private final static int ON_SAVE_DATA = 3; //触发保存数据的标志位
    private final static int UNDO_SAVE = 4;
    private final static int ON_RECEIVE_LOCATION_DATA = 5;
    private final static int ON_RECEIVE_SCAN_RESULT = 6;
    private final static int ON_EDIT_DEVICE = 7;

    //startActivityForResult 标志位
    private final static int REQUEST_QR_CODE = 1028;
    private final static int REQUEST_QR_IMAGE = 1029;
    private final static int SCAN_RESULT_FROM_GALLERY = 1030;
    private final static int SCAN_REQUEST_FROM_TOOLBAR = 1031;
    private final static int SCAN_RESULT_FROM_TOOLBAR = 1032;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_acq_data, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        mStartLocation.setOnClickListener(this);
        mStopLocation.setOnClickListener(this);
        mSaveButton.setOnClickListener(this);
        mUndoSave.setOnClickListener(this);

        //设置输入框的属性
        EditTextUtil.editTextToUpperCase(mDeviceId, mMacAddress_1, mMacAddress_2,
                mMacAddress_3, mMacAddress_4, mMacAddress_5, mMacAddress_6);

        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mDao = new DeviceRawDao(mActivity);
        //分页查询
/*        List<LocationDevice> locationDevices = mDao.queryWithLimit("0,5");
        LogUtil.i(TAG, locationDevices.toString());*/

        //百度地图定位sdk初始化
        mLocationDevice = new LocationDevice();
        mLocationClient = new LocationClient(mActivity);
        mLocationClient.registerLocationListener(new MyLocationListener());
        initLocation();
        mHandler = new MyHandler(AcqDataFragment.this);

        mAcqToolbar.setTitle("数据采集");
        mAcqToolbar.inflateMenu(R.menu.acq_data_toolbar);
        mAcqToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.menu_search_button:
                        DialogUtil.showSearchDialog(mActivity);
                        break;
                    case R.id.menu_scan_code:
                        //扫码
                        Intent intent = new Intent(mActivity, CaptureActivity.class);
                        startActivityForResult(intent, REQUEST_QR_CODE);
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLocationClient.stop();
    }


    private static class MyHandler extends Handler {

        WeakReference<AcqDataFragment> mFragment;

        MyHandler(AcqDataFragment fragment) {
            mFragment = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            AcqDataFragment fragment = mFragment.get();
            if (fragment != null) {// 先判断弱引用是否为空，为空则不更新UI

                //接收到撤销请求，显示并更新对话框UI
                switch (msg.what){
                    case UNDO_SAVE:
                        int position = msg.arg1;
                        LocationDevice tmpDevice = (LocationDevice) msg.obj;
                        if (tmpDevice != null) {
                            //还需要通知 recyclerview 删除对应元素
                            Intent intent = new Intent("com.notinglife.android.action.UNDO_SAVE");
                            intent.putExtra("flag", UNDO_SAVE);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("undo_save_device", tmpDevice);
                            intent.putExtra("undo_save_device", bundle);
                            LocalBroadcastManager.getInstance(fragment.mActivity).sendBroadcast(intent);
                            ToastUtil.showShortToast(fragment.mActivity, "成功撤销上一次保存");
                        }
                        break;
                    case ON_RECEIVE_LOCATION_DATA:
                        LocationDevice tmpDevice2 = (LocationDevice) msg.obj;
                        if (tmpDevice2 != null) {
                            fragment.mLatTextView.setText(tmpDevice2.mLatitude);
                            fragment.mLngTextView.setText(tmpDevice2.mLongitude);
                            fragment.mRadiusValue.setText(tmpDevice2.mRadius + "米");

                            if (tmpDevice2.mLocMode == BDLocation.TypeGpsLocation) {
                                fragment.mLocMode.setText("GPS定位");

                            } else {
                                fragment.mLocMode.setText("网络定位");
                            }
                            //通过sharedpreferences读取配置，是否发送定位信息
                            boolean sendData = SPUtil.getBoolean(fragment.mActivity, ToSendLocation, false);
                            LogUtil.i(TAG,"是否发送数据 : "+(sendData?"是的":"不是"));
                            if(sendData){
                                Intent intent = new Intent("com.notinglife.android.action.ON_RECEIVE_LOCATION_DATA");
                                intent.putExtra("flag", ON_RECEIVE_LOCATION_DATA);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("ON_RECEIVE_LOCATION_DATA", tmpDevice2);
                                intent.putExtra("ON_RECEIVE_LOCATION_DATA", bundle);
                                LocalBroadcastManager.getInstance(fragment.mActivity).sendBroadcast(intent);
                            }
                        }
                        break;
                    case ON_RECEIVE_SCAN_RESULT:
                        LocationDevice tmpDevice3 = (LocationDevice) msg.obj;

                        if (tmpDevice3 != null) {
                            //设备号和mac地址同时验证合格，才设置给UI
                            if(RegexValidator.isDeviceID(tmpDevice3.mDeviceID) && RegexValidator.isMacAddress(tmpDevice3.mMacAddress)){
                                fragment.mDeviceId.setText(tmpDevice3.mDeviceID);
                                String macAddress = tmpDevice3.mMacAddress; //16:11:32:17:12:18
                                String[] split = macAddress.split(":");
                                fragment.mMacAddress_1.setText(split[0]);
                                fragment.mMacAddress_2.setText(split[1]);
                                fragment.mMacAddress_3.setText(split[2]);
                                fragment.mMacAddress_4.setText(split[3]);
                                fragment.mMacAddress_5.setText(split[4]);
                                fragment.mMacAddress_6.setText(split[5]);
                                ToastUtil.showShortToast(fragment.mActivity, "MAC地址解析成功");
                            } else {
                                fragment.mDeviceId.setText("");
                                fragment.mMacAddress_1.setText("");
                                fragment.mMacAddress_2.setText("");
                                fragment.mMacAddress_3.setText("");
                                fragment.mMacAddress_4.setText("");
                                fragment.mMacAddress_5.setText("");
                                fragment.mMacAddress_6.setText("");
                                ToastUtil.showShortToast(fragment.mActivity, "MAC地址解析错误，请检查二维码是否合法");
                            }
                        }
                        break;
                }

            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_start_gps:
                if (!isOPen(mActivity)) {
                    ToastUtil.showShortToast(mActivity, "请先打开GPS");
                    // 转到手机设置界面，用户设置GPS
                    Intent intent = new Intent(
                            Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                } else {
                    if(!SPUtil.getBoolean(getActivity(),IsLocation,false)){
                        SPUtil.setBoolean(getActivity(),IsLocation,true);
                        ToastUtil.showShortToast(mActivity, "定位中");
                        mLocationClient.start();
                    }else {
                        //如果已经定位，那么不用再 start
                        ToastUtil.showShortToast(mActivity, "已在后台定位");
                    }
                }
                break;

            case R.id.bt_stop_gps:
                if(SPUtil.getBoolean(getActivity(),IsLocation,false)){
                    ToastUtil.showShortToast(mActivity, "停止定位");
                    mLocationClient.stop();
                    SPUtil.setBoolean(getActivity(),IsLocation,false);
                }else {
                    ToastUtil.showShortToast(mActivity, "已停止定位");
                }
                break;

            case R.id.bt_save_data:
                String deviceId = mDeviceId.getText().toString();
                String macAddress = EditTextUtil.generateMacAddress(mMacAddress_1, mMacAddress_2,
                        mMacAddress_3, mMacAddress_4, mMacAddress_5, mMacAddress_6);

                if (SPUtil.getBoolean(getActivity(),IsLocation,false)) {

                    if (TextUtils.isEmpty(mLocationDevice.mLatitude)) {
                        ToastUtil.showShortToast(mActivity, "请先获取GPS地址");
                        return;
                    }

                    //判断定位模式
                    if (mLocationDevice.mLocMode != BDLocation.TypeGpsLocation) {
                        ToastUtil.showShortToast(mActivity, "请等待GPS定位数据");
                        return;
                    }

                    //通过正则表达式判断 ID 和MAC地址
                    if (!RegexValidator.isDeviceID(deviceId)) {
                        ToastUtil.showShortToast(mActivity, "设备号码不正确");
                        return;
                    }
                    if (!RegexValidator.isMacAddress(macAddress)) {
                        ToastUtil.showShortToast(mActivity, "MAC地址不正确");
                        return;
                    }


                    //ID不能重复添加
                    LocationDevice deviceToDo1 = mDao.queryById(deviceId);
                    if (deviceToDo1 != null) {
                        ToastUtil.showShortToast(mActivity, "不能重复添加相同ID的设备");
                        return;
                    }
                    //MAC地址不能重复添加
                    LocationDevice deviceToDo2 = mDao.queryByMac(macAddress);
                    if (deviceToDo2 != null) {
                        ToastUtil.showShortToast(mActivity, "不能重复添加相同MAC地址的设备");
                        return;
                    }

                    mLocationDevice.mDeviceID = deviceId;
                    mLocationDevice.mMacAddress = macAddress;
                    //LogUtil.i("设备ID:" + mLocationDevice.mDeiviceId + " ,MAC地址: " + mLocationDevice.mMacAddress + ", 经度为："
                    //        + mLocationDevice.mLatitude + ", 纬度为：" + mLocationDevice.mLongitude);
                    mDao.add(mLocationDevice);

                    //临时保存 撤销功能使用
                    this.undoSaveDevice = mLocationDevice;

                    //通知recyclerview刷新
                    Intent intent = new Intent("com.notinglife.android.action.DATA_CHANGED");
                    intent.putExtra("flag", ON_SAVE_DATA);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("on_save_data", mLocationDevice);
                    intent.putExtra("on_save_data", bundle);
                    LocalBroadcastManager.getInstance(mActivity).sendBroadcast(intent);
                    ToastUtil.showShortToast(mActivity, "添加设备信息成功");
                } else {
                    ToastUtil.showShortToast(mActivity, "已停止定位，请重新获取定位信息");
                }
                break;

            case R.id.bt_undo_save:
                LocationDevice tmpDevice = mDao.queryLastSave();
                //// TODO: 2017/6/13 自增ID最大的是否是最近需要撤销的
                if (undoSaveDevice == null) {
                    ToastUtil.showShortToast(mActivity, "当前还未保存，无法撤销");
                    return;
                }
                if (tmpDevice.mDeviceID.equals(undoSaveDevice.mDeviceID)) {
                    //LogUtil.i("等待撤销的对象 "+tmpDevice.toString());
                    DialogUtil.showDeviceDialog(v, mActivity, mHandler, "是否撤销如下保存", null, tmpDevice, -1, UNDO_SAVE);
                } else {
                    ToastUtil.showShortToast(mActivity, "当前保存设备，无法撤销保存");
                }
                break;
            default:
                break;
        }
    }

    private class MyLocationListener implements BDLocationListener {
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
            LogUtil.i(TAG, " 百度SDK回显消息----" + mLocationDevice.toString());
            mHandler.sendMessage(locationMsg);
        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {
        }

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
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setScanSpan(5000);
        //设置百度坐标
        option.setCoorType("bd09ll");
        option.setOpenGps(true); // 打开gps
        //可选，设置是否需要地址信息，默认不需要
        option.setIsNeedAddress(false);
        mLocationClient.setLocOption(option);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_QR_CODE) {
            //处理扫描结果（在界面上显示）
            if (null != data) {
                Bundle bundle = data.getExtras();
                if (bundle == null) {
                    return;
                }
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                    String result = bundle.getString(CodeUtils.RESULT_STRING);
                    sendHandlerMsg(result);
                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    ToastUtil.showShortToast(mActivity, "解析二维码失败");
                }
            }
        }

        if (requestCode == REQUEST_QR_IMAGE || resultCode == SCAN_RESULT_FROM_GALLERY) {
            if (data != null) {
                Uri uri = data.getData();
                try {
                    CodeUtils.analyzeBitmap(ImageUtil.getImageAbsolutePath(mActivity, uri), new CodeUtils.AnalyzeCallback() {
                        @Override
                        public void onAnalyzeSuccess(Bitmap mBitmap, String result) {
                            sendHandlerMsg(result);
                        }

                        @Override
                        public void onAnalyzeFailed() {
                            Toast.makeText(mActivity, "扫描图片二维码失败", Toast.LENGTH_LONG).show();
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }

    //从菜单设置的扫一扫，MainActivity得到菜单的扫一扫的数据
    public void setIntentData(Intent intent) {
        Intent intent1 = intent;
        if (intent1 != null) {
            Uri uri = intent1.getData();
            try {
                CodeUtils.analyzeBitmap(ImageUtil.getImageAbsolutePath(mActivity, uri), new CodeUtils.AnalyzeCallback() {
                    @Override
                    public void onAnalyzeSuccess(Bitmap mBitmap, String result) {
                        sendHandlerMsg(result);
                    }

                    @Override
                    public void onAnalyzeFailed() {
                        Toast.makeText(mActivity, "扫描图片二维码失败", Toast.LENGTH_LONG).show();
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //解析二维码结果，拆分字符串，并发送给本fragment的handler
    private void sendHandlerMsg(String result) {
        if (!TextUtils.isEmpty(result)) {
            String[] split1 = result.split(":");
            if (!split1[0].equals("LocationHelper")) {
                ToastUtil.showShortToast(mActivity, "不支持该格式的二维码，请检查二维码是否正确");
            } else {
                String[] split2 = result.split("//"); // LocationHelper://00033#16:11:32:17:12:18
                String split3 = split2[1]; //00033#16:11:32:17:12:18
                String[] split4 = split3.split("#");
                LocationDevice locationDevice = new LocationDevice();
                locationDevice.mDeviceID = split4[0];
                locationDevice.mMacAddress = split4[1];

                Message tmpMsg = Message.obtain();
                tmpMsg.what = ON_RECEIVE_SCAN_RESULT;
                tmpMsg.obj = locationDevice;
                mHandler.sendMessage(tmpMsg);
            }
        }
    }


}
