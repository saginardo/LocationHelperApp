package com.notinglife.android.LocationHelper.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

    @BindView(R.id.bt_accept)
    Button mPositiveButton;//确定按钮
    @BindView(R.id.bt_cancel)
    Button mNegativeButton;//取消按钮
    @BindView(R.id.tv_edit_device_title)
    TextView mDialogTitle;

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

    private onPositiveOnclickListener positiveOnclickListener;//确定按钮被点击了的监听器
    private onNegativeOnclickListener negativeOnclickListener;//取消按钮被点击了的监听器

    private String deviceId;
    private String deviceLat;
    private String deviceLng;
    private String deviceMac;

    private LocationDevice tmpDevice;

    public EditDialog(Context context) {
        super(context, R.style.MyDialog);
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_edit_item);
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

    /**
     * 初始化界面控件的显示数据回显
     */
    private void initData() {

    }

    private void initView() {
        mETDeviceId.setText(tmpDevice.mDeivceId);
        mETDeviceLat.setText(tmpDevice.mLatitude);
        mETDeviceLng.setText(tmpDevice.mLongitude);
        mETDeviceMac.setText(tmpDevice.mMacAddress);
    }

    public void setDeviceInfo(LocationDevice locationDevice) {
        tmpDevice = new LocationDevice();
        tmpDevice = locationDevice;

/*      deviceId = locationDevice.mDeivceId;
        deviceLat = locationDevice.mLatitude;
        deviceLng = locationDevice.mLongitude;
        deviceMac = locationDevice.mMacAddress;*/
    }

    public LocationDevice getDeviceInfo() {

        tmpDevice.mDeivceId = mETDeviceId.getText().toString();
        tmpDevice.mLatitude = mETDeviceLat.getText().toString();
        tmpDevice.mLongitude = mETDeviceLng.getText().toString();
        tmpDevice.mMacAddress = mETDeviceMac.getText().toString();
        return tmpDevice;
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
