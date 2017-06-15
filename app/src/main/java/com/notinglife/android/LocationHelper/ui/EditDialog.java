package com.notinglife.android.LocationHelper.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.notinglife.android.LocationHelper.R;
import com.notinglife.android.LocationHelper.domain.LocationDevice;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * ${DESCRIPTION}
 *
 * @author saginardo
 * @version ${VERSION}
 *          date 2017-06-12 14:39
 */

public class EditDialog extends Dialog {

    @BindView(R.id.tv_edit_device_title)
    TextView mDialogTitle;
    @BindView(R.id.tv_is_delete_all)
    TextView mIsDeleteAll;

    @BindView(R.id.rl_device_id)
    RelativeLayout mRLDeviceID;
    @BindView(R.id.rl_device_lat_lng)
    RelativeLayout mRLDeviceLatLng;
    @BindView(R.id.rl_device_mac)
    RelativeLayout mRLDeviceMac;



    @BindView(R.id.tv_device_id)
    TextView mTVDeviceId;
    @BindView(R.id.tv_device_lat)
    TextView mTVDeviceLat;
    @BindView(R.id.tv_device_lng)
    TextView mTVDeviceLng;
    @BindView(R.id.tv_device_mac)
    TextView mTVDeviceMac;

    @BindView(R.id.et_device_id)
    EditText mETDeviceId;
    @BindView(R.id.et_device_lat)
    EditText mETDeviceLat;
    @BindView(R.id.et_device_lng)
    EditText mETDeviceLng;
    @BindView(R.id.et_device_mac)
    EditText mETDeviceMac;

    @BindView(R.id.bt_accept)
    Button mPositiveButton;//确定按钮
    @BindView(R.id.bt_cancel)
    Button mNegativeButton;//取消按钮

    private onPositiveOnclickListener positiveOnclickListener;//确定按钮被点击了的监听器
    private onNegativeOnclickListener negativeOnclickListener;//取消按钮被点击了的监听器

    private String dialogTitle;
    private String message;
    private LocationDevice tmpDevice;

    //showdialog标志位
    private int mFlag;
    private final static int DELETE_BY_ID = 0;
    private final static int DELETE_ALL = 1;
    private final static int UPDATE_DEVICE = 2;
    private final static int ON_SAVE_DATA = 3; //触发保存数据的标志位
    private final static int UNDO_SAVE = 4;
    private final static int ON_RECEIVE_LOCATION_DATA = 5;

    public EditDialog(Context context, String title, String msg) {
        super(context, R.style.MyDialog);
        dialogTitle  = title;
        message = msg;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_show_dialog);
        //按空白处不能取消动画
        setCanceledOnTouchOutside(false);
        ButterKnife.bind(this);

        //初始化界面控件
        initView();
        //初始化界面数据
        initData();
        //初始化界面控件的事件
        initEvent();

    }
    /**
     * 初始化界面控件的显示数据回显
     */
    private void initData() {

        if(dialogTitle!=null && !dialogTitle.equals("")){
            mDialogTitle.setText(dialogTitle);
        }
        if(message!=null && !message.equals("")){
            mIsDeleteAll.setVisibility(View.VISIBLE);
            mIsDeleteAll.setText(message);
        }
    }

    private void initView() {

        if(tmpDevice!=null){
            mETDeviceId.setText(tmpDevice.mDeivceId);
            mETDeviceLat.setText(tmpDevice.mLatitude);
            mETDeviceLng.setText(tmpDevice.mLongitude);
            mETDeviceMac.setText(tmpDevice.mMacAddress);
        }else {
            mRLDeviceID.setVisibility(View.GONE);
            mRLDeviceLatLng.setVisibility(View.GONE);
            mRLDeviceMac.setVisibility(View.GONE);
        }
        if(mFlag==DELETE_BY_ID || mFlag == UNDO_SAVE){
            mETDeviceId.setEnabled(false);
            mETDeviceLat.setEnabled(false);
            mETDeviceLng.setEnabled(false);
            mETDeviceMac.setEnabled(false);
        }
    }
    /**
     * 初始化界面的确定和取消监听器
     */
    private void initEvent() {
        //设置确定按钮被点击后，向外界提供监听
        mPositiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (positiveOnclickListener != null) {
                    positiveOnclickListener.onPositiveClick();
                }
            }
        });
        //设置取消按钮被点击后，向外界提供监听
        mNegativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (negativeOnclickListener != null) {
                    negativeOnclickListener.onNegativeClick();
                }
            }
        });
    }

    public void setDeviceInfo(LocationDevice locationDevice) {
        tmpDevice = new LocationDevice();
        tmpDevice = locationDevice;
    }

    public LocationDevice getDeviceInfo() {

        tmpDevice.mDeivceId = mETDeviceId.getText().toString();
        tmpDevice.mLatitude = mETDeviceLat.getText().toString();
        tmpDevice.mLongitude = mETDeviceLng.getText().toString();
        tmpDevice.mMacAddress = mETDeviceMac.getText().toString();
        return tmpDevice;
    }
    /**
     * 设置确定按钮的显示内容和监听
     *
     * @param onYesOnclickListener
     */
    public void setPositiveOnclickListener(onPositiveOnclickListener onYesOnclickListener) {
        this.positiveOnclickListener = onYesOnclickListener;
    }

    /**
     * 设置取消按钮的显示内容和监听
     *
     * @param onNoOnclickListener
     */
    public void setNegativeOnclickListener(onNegativeOnclickListener onNoOnclickListener) {
        this.negativeOnclickListener = onNoOnclickListener;
    }

    public void setFlag(int flag) {
        mFlag = flag;
    }

    /**
     * 设置确定按钮和取消被点击的接口
     */
    public interface onPositiveOnclickListener {
        void onPositiveClick();
    }

    public interface onNegativeOnclickListener {
        void onNegativeClick();
    }
}
