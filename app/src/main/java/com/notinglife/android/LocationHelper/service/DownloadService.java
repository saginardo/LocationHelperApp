package com.notinglife.android.LocationHelper.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.notinglife.android.LocationHelper.utils.LogUtil;
import com.notinglife.android.LocationHelper.utils.ToastUtil;


public class DownloadService extends Service {
    private static final String TAG = "DownloadService";

    public DownloadService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        //当Activity使用 BindService时，先调用onCreate，然后在走onBind方法
        LogUtil.i(TAG,"onBind 绑定服务");
        return new MyBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.i(TAG,"onCreate 开启服务");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.i(TAG,"onStartCommand 开启服务");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        LogUtil.i(TAG,"onUnbind 解绑服务");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.i(TAG,"onDestroy 删除服务");
    }

    public void test(){
        ToastUtil.showShortToast(getApplicationContext(),"测试方法");
    }


    //通过接口暴露需要对外提供的方法
    private class MyBinder extends Binder implements IService {
        //定义一个中间方法

        public void callTest(){
            test();
        }
    }
}
