package com.notinglife.android.LocationHelper.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.notinglife.android.LocationHelper.utils.GlobalConstant;
import com.notinglife.android.LocationHelper.utils.LogUtil;
import com.notinglife.android.LocationHelper.utils.MyLocalBroadcastManager;

import static com.notinglife.android.LocationHelper.utils.GlobalConstant.ACTION_ON_UPLOAD;

;


/**
 * 文件上传或下载的逻辑，一定要注意在 xml文件中配置服务
 *
 * @author saginardo
 * @version ${VERSION}
 *          date 2017-08-24 19:11
 */

public class FileService extends IntentService {

    private static final String TAG = "FileService";
    public static final String EXTRA_FILE_PATH = "EXTRA_FILE_PATH";

    public FileService() {
        super("FileService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null)
        {
            final String action = intent.getAction();
            if (action.equals(ACTION_ON_UPLOAD))
            {
                final String path = intent.getStringExtra(EXTRA_FILE_PATH);
                handleUploadFile(path);
            }
        }
    }

    private void handleUploadFile(String path) {
        try
        {
            //模拟上传耗时
            Thread.sleep(3000);
            LogUtil.i(TAG,"上传完成");

            MyLocalBroadcastManager.sendLocalBroadcast(getApplicationContext(),GlobalConstant.ACTION_ON_UPLOAD_RESULT,
                    GlobalConstant.ACTION_ON_UPLOAD_RESULT,"上传文件成功！");

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public static void startUploadFile(Context context, String path)
    {
        Intent intent = new Intent(context, FileService.class);
        intent.setAction(ACTION_ON_UPLOAD);
        intent.putExtra(EXTRA_FILE_PATH, path);
        context.startService(intent);
        LogUtil.i(TAG,"开启上传任务");
    }
}
