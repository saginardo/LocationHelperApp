package com.notinglife.android.LocationHelper.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.notinglife.android.LocationHelper.R;
import com.notinglife.android.LocationHelper.utils.FileUtil;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ManageDataActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.manage_data_toolBar)
    Toolbar mManageDataToolBar;
    @BindView(R.id.send_via_system_share)
    TextView mSendViaSystemShare;
    @BindView(R.id.backup_via_avcloud)
    TextView mBackupViaAvcloud;
    @BindView(R.id.download_all_data)
    Button mDownloadAllData;
    @BindView(R.id.delete_from_avcloud)
    Button mDeleteFromAvcloud;
    @BindView(R.id.delete_all_data)
    Button mDeleteAllData;
    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;

    private static final String TAG = "ManageDataActivity";
    private MyHandler mHandler;

    private final static int ON_DELETE_ALL_DATA = 8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_data);
        ButterKnife.bind(this);
        mHandler = new MyHandler(this);

        setSupportActionBar(mManageDataToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("数据管理");

        //设置点击事件
        mSendViaSystemShare.setOnClickListener(this);
        mBackupViaAvcloud.setOnClickListener(this);
        mDownloadAllData.setOnClickListener(this);
        mDeleteFromAvcloud.setOnClickListener(this);
        mDeleteAllData.setOnClickListener(this);
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send_via_system_share:
                FileUtil.sendFile(this,"data.txt");
                break;
            case R.id.backup_via_avcloud:
                FileUtil.backupViaCloud(this,"data.txt",mProgressBar);
                break;
            case R.id.download_all_data:
                FileUtil.downloadFromCloud(this,"data_cloud.txt",mProgressBar);
                break;
            case R.id.delete_from_avcloud:
                FileUtil.deleteFromCloud(this);
                break;
            case R.id.delete_all_data:
                FileUtil.deleteFromLocal(this);
                Intent intent = new Intent("com.notinglife.android.action.DATA_CHANGED");
                intent.putExtra("flag", ON_DELETE_ALL_DATA);
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                break;
            default:
                break;
        }
    }




    //用于更新mainActivity的UI
    private static class MyHandler extends Handler {

        WeakReference<ManageDataActivity> mActivity;

        MyHandler(ManageDataActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ManageDataActivity activity = mActivity.get();
            if (activity != null) {// 先判断弱引用是否为空，为空则不更新UI
                int flag = msg.what;
                //更新UI等逻辑

            }
        }
    }


}
