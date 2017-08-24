package com.notinglife.android.LocationHelper.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
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
import com.notinglife.android.LocationHelper.dao.DeviceRawDao;
import com.notinglife.android.LocationHelper.domain.LocationDevice;
import com.notinglife.android.LocationHelper.service.FileService;
import com.notinglife.android.LocationHelper.service.IService;
import com.notinglife.android.LocationHelper.utils.FileUtil;
import com.notinglife.android.LocationHelper.utils.GlobalConstant;
import com.notinglife.android.LocationHelper.utils.MyLocalBroadcastManager;
import com.notinglife.android.LocationHelper.utils.ToastUtil;

import java.lang.ref.WeakReference;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

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
    private Unbinder mUnBinder;

    private final static int ON_DELETE_ALL_DATA = 8;
    private MyConn mConn;
    private IService mService;
    private LocalBroadcastManager mBroadcastManager;
    private MyReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_data);
        mUnBinder = ButterKnife.bind(this);
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

        mConn = new MyConn();

        //Intent intent = new Intent(this, DownloadService.class);
        //bindService(intent, mConn,BIND_AUTO_CREATE);

        registerReceiver();
    }

    private void registerReceiver() {
        //动态注册本地广播
        mBroadcastManager = LocalBroadcastManager.getInstance(getApplicationContext());
        IntentFilter filter = new IntentFilter();
        filter.addAction(GlobalConstant.ACTION_ON_UPLOAD_RESULT);
        mReceiver = new MyReceiver();
        mBroadcastManager.registerReceiver(mReceiver, filter);
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


    // FIXME: 2017/7/1 对话框确认
    @Override
    public void onClick(View v) {
        DeviceRawDao mDao = new DeviceRawDao(this);
        List<LocationDevice> list;
        switch (v.getId()) {
            case R.id.send_via_system_share:
                list = mDao.queryAll();
                FileUtil.sendFile(this, list, "data.txt");
                break;
            case R.id.backup_via_avcloud:
                list = mDao.queryAll();
                //FileUtil.backupViaCloud(this,list,"data.txt",mProgressBar);
                addUploadTask(v);
                break;
            case R.id.download_all_data:
                FileUtil.downloadFromCloud(this, "data_cloud.txt", mProgressBar);
                break;
            case R.id.delete_from_avcloud:

                break;
            case R.id.delete_all_data:
                mDao.deleteAll();
                FileUtil.removeFile(this);
                MyLocalBroadcastManager.sendLocalBroadcast(this, "DATA_CHANGED", ON_DELETE_ALL_DATA,
                        null, -1, null, null);
                break;
            default:
                break;
        }
    }


    public void addUploadTask(View view) {
        //模拟路径
        String path = "/sdcard/imgs/" + "test.png";
        FileService.startUploadFile(getApplicationContext(), path);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnBinder.unbind();
        //unbindService(mConn);
        mBroadcastManager.unregisterReceiver(mReceiver);
    }


    public class MyConn implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            //iBinder对象就是 服务定义的 MyBinder
            mService = (IService) iBinder;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    }

    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(GlobalConstant.ACTION_ON_UPLOAD_RESULT)) {
                String result = intent.getStringExtra(GlobalConstant.ACTION_ON_UPLOAD_RESULT);
                ToastUtil.showShortToast(getApplicationContext(), result);
            }

        }
    }


}
