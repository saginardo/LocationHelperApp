package com.notinglife.android.LocationHelper.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.notinglife.android.LocationHelper.R;
import com.notinglife.android.LocationHelper.utils.DialogUtil;
import com.notinglife.android.LocationHelper.utils.LogUtil;
import com.notinglife.android.LocationHelper.utils.MyLocalBroadcastManager;
import com.notinglife.android.LocationHelper.utils.SPUtil;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.system_setting_toolBar)
    Toolbar mSystemSettingToolBar;
    @BindView(R.id.primary_location_mode)
    TextView mPrimaryLocationMode;
    @BindView(R.id.location_time)
    TextView mLocationTime;


    private MyHandler mHandler;

    //修改设置的广播的标志位
    private final static int LOCATION_MODE = 40;
    private final static int LOCATION_TIME = 41;

    private static final String TAG = "SettingsActivity";
    //SpUtil用的key
    private static final String IsLocation = "IsLocation";
    private static final String ToSendLocation = "ToSendLocation";
    private final static String LocationMode = "LocationMode";
    private final static String LocationTime = "LocationTime";
    private Unbinder mUnBinder;
    private Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mUnBinder = ButterKnife.bind(this);
        mActivity = this;

        setSupportActionBar(mSystemSettingToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("系统设置");

        mPrimaryLocationMode.setOnClickListener(this);
        mLocationTime.setOnClickListener(this);
        mHandler = new MyHandler(this);
    }


    private static class MyHandler extends Handler {
        WeakReference<SettingsActivity> mActivity;
        MyHandler(SettingsActivity activity) {
            mActivity = new WeakReference<>(activity);
        }
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            final SettingsActivity activity = mActivity.get();
            if (activity != null) {// 先判断弱引用是否为空，为空则不更新UI
                int flag = msg.what;
                if (flag == LOCATION_MODE) {
                    String location_mode = (String) msg.obj;
                    LogUtil.i(TAG, "选择的按钮是：" + location_mode);
                    //只有用户正在定位时候，发送广播，让主页面重新定位
                    if (SPUtil.getBoolean(activity.getApplicationContext(), IsLocation, false)) {
                        MyLocalBroadcastManager.sendLocalBroadcast(activity.mActivity,"ON_CHANGE_LOCATION_CHOICE",LOCATION_MODE,
                                null,-1,null,null);
                    }
                }

                if (flag == LOCATION_TIME) {
                    String location_time = (String) msg.obj;
                    LogUtil.i(TAG, "选择的按钮是：" + location_time);
                    //只有用户正在定位时候，发送广播，让主页面重新定位
                    if (SPUtil.getBoolean(activity.getApplicationContext(), IsLocation, false)) {
                        MyLocalBroadcastManager.sendLocalBroadcast(activity.mActivity,"ON_CHANGE_LOCATION_CHOICE",LOCATION_TIME,
                                null,-1,null,null);
                    }
                }
            }
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.primary_location_mode:
                DialogUtil.showChoiceDialog(this, mHandler, "请选择定位模式", LOCATION_MODE, "混合模式", "仅使用GPS", "仅使用网络");
                break;
            case R.id.location_time:
                DialogUtil.showChoiceDialog(this, mHandler, "请选择后台定位时长", LOCATION_TIME, "60秒", "120秒", "300秒", "无限制");
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnBinder.unbind();
    }
}
