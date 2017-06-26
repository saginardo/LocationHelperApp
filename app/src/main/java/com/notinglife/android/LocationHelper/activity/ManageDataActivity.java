package com.notinglife.android.LocationHelper.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.ProgressCallback;
import com.avos.avoscloud.SaveCallback;
import com.notinglife.android.LocationHelper.R;
import com.notinglife.android.LocationHelper.dao.DeviceRawDao;
import com.notinglife.android.LocationHelper.domain.LocationDevice;
import com.notinglife.android.LocationHelper.utils.FileUtil;
import com.notinglife.android.LocationHelper.utils.LogUtil;
import com.notinglife.android.LocationHelper.utils.ToastUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ManageDataActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.manage_data_toolBar)
    Toolbar mManageDataToolBar;
    @BindView(R.id.send_via_system_share)
    TextView mSendViaSystemShare;
    @BindView(R.id.backup_file_via_avcloud)
    Button mBackupFileViaAvcloud;
    @BindView(R.id.backup_database_via_avcloud)
    TextView mBackupDatabaseViaAvcloud;
    @BindView(R.id.download_all_data)
    Button mDownloadAllData;
    @BindView(R.id.sync_with_avcloud)
    Button mSyncWithAvcloud;
    @BindView(R.id.delete_all_data)
    Button mDeleteAllData;
    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;

    private static final String TAG = "ManageDataActivity";
    private MyHandler mHandler;

    private static final int ON_BACKUP_DATABASE=40;

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
        mBackupFileViaAvcloud.setOnClickListener(this);
        mBackupDatabaseViaAvcloud.setOnClickListener(this);
        mDownloadAllData.setOnClickListener(this);
        mSyncWithAvcloud.setOnClickListener(this);
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
                sendFile(this);
                break;
            case R.id.backup_file_via_avcloud:
                backupFile(this);
                break;
            case R.id.backup_database_via_avcloud:
                backupDatabase(this);
                break;
            case R.id.download_all_data:
                break;
            case R.id.sync_with_avcloud:
                break;
            case R.id.delete_all_data:
                break;
            default:
                break;
        }
    }


    public void sendFile(Activity activity) {
        DeviceRawDao mDao = new DeviceRawDao(activity);
        List<LocationDevice> mLocationDevices = mDao.queryAll();
        if (mLocationDevices == null || mLocationDevices.size() == 0) {
            ToastUtil.showShortToast(activity, "当前没有数据，无法发送");
        } else {
            FileUtil.saveListToFile(activity, mLocationDevices);
            Intent intentShareFile = new Intent(Intent.ACTION_SEND);
            String myFilePath = activity.getExternalFilesDir("data").getPath();
            File file = new File(myFilePath, "data.txt");
            if (file.exists()) {
                intentShareFile.setType("text/plain");
                intentShareFile.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + file));
                intentShareFile.putExtra(Intent.EXTRA_SUBJECT,
                        "Sharing File...");
                intentShareFile.putExtra(Intent.EXTRA_TEXT, "Sharing File...");
                startActivity(Intent.createChooser(intentShareFile, "Share File"));
            } else {
                ToastUtil.showShortToast(activity, "没有数据文件");
            }
        }

    }

    private void backupFile(final Activity activity) {
        //存为文件 _File表
        DeviceRawDao mDao = new DeviceRawDao(activity);
        List<LocationDevice> mLocationDevices = mDao.queryAll();
        if (mLocationDevices == null || mLocationDevices.size() == 0) {
            ToastUtil.showShortToast(activity, "当前没有数据，无法发送");
        } else {
            FileUtil.saveListToFile(activity, mLocationDevices);
            String myFilePath = activity.getExternalFilesDir("data").getPath();
            mProgressBar.setVisibility(View.VISIBLE);
            try {
                final AVFile file = AVFile.withAbsoluteLocalPath("DeviceData.txt", myFilePath + "/data.txt");

                file.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(AVException e) {
                        if (e == null) {
                            LogUtil.i(TAG, file.getUrl());//保存成功后，返回一个唯一的 Url 地址
                            mProgressBar.setVisibility(View.GONE);
                            ToastUtil.showShortToast(activity, "云备份文件成功");
                        }
                    }
                }, new ProgressCallback() {
                    @Override
                    public void done(Integer integer) {
                        // 上传进度数据，integer 介于 0 和 100。
                        mProgressBar.setProgress(integer);
                    }
                });

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void backupDatabase(final Activity activity) {
        DeviceRawDao mDao = new DeviceRawDao(activity);
        List<LocationDevice> mLocationDevices = mDao.queryAll();
        if (mLocationDevices == null || mLocationDevices.size() == 0) {
            ToastUtil.showShortToast(activity, "当前没有数据，无法发送");
        } else {
            //存为对象 LocationDevices
            ArrayList<AVObject> devices = new ArrayList<>();
            for (LocationDevice tmpDevice : mLocationDevices) {
                AVObject object = new AVObject("LocationDevices");
                object.put("DeviceID", tmpDevice.mDeviceID);
                object.put("MacAddress", tmpDevice.mMacAddress);
                object.put("Latitude", tmpDevice.mLatitude);
                object.put("Longitude", tmpDevice.mLongitude);
                devices.add(object);
            }
            ToastUtil.showShortToast(activity.getApplicationContext(), "正在备份中，请稍后");
            // FIXME: 2017/6/26 添加用户的AVRelation
            AVObject.saveAllInBackground(devices, new SaveCallback() {
                @Override
                public void done(AVException e) {
                    if (e == null) {
                        Message msg = Message.obtain();
                        msg.what = ON_BACKUP_DATABASE;
                        msg.obj ="云备份数据库成功" ;
                        mHandler.sendMessage(msg);
                    } else {
                        e.printStackTrace();
                    }
                }
            });

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
                //接收注销本地广播，更新UI等逻辑
                if (flag == ON_BACKUP_DATABASE) {
                    ToastUtil.showShortToast(activity.getApplicationContext(), msg.obj.toString() );
                }
            }
        }
    }


}
