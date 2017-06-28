package com.notinglife.android.LocationHelper.activity;

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

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.system_setting_toolBar)
    Toolbar mSystemSettingToolBar;
    @BindView(R.id.primary_location_mode)
    TextView mPrimaryLocationMode;
    @BindView(R.id.location_time)
    TextView mLocationTime;


    private MyHandler mHandler;
    private final static int LOCATION_BACKGROUND_TIME = 40;
    private final static int LOCATION_MODE = 41;
    private static final String TAG = "SettingsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);

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
                if(flag==LOCATION_BACKGROUND_TIME){
                    String location_background_time = (String) msg.obj;
                    LogUtil.i(TAG,"选择的按钮是："+location_background_time);
                }
            }
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.primary_location_mode:
                DialogUtil.showChoiceDialog(this, mHandler, "请选择定位模式", LOCATION_MODE, "混合模式","仅使用GPS","仅使用网络");
                break;
            case R.id.location_time:
                DialogUtil.showChoiceDialog(this, mHandler, "请选择后台定位时长",LOCATION_BACKGROUND_TIME, "60秒","120秒","300秒","无限制");
                break;
            default:
                break;
        }
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
}
