package com.notinglife.android.LocationHelper.application;

import android.app.Application;

import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVOSCloud;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.uuzuche.lib_zxing.activity.ZXingLibrary;

/**
 * ${DESCRIPTION}
 *
 * @author saginardo
 * @version ${VERSION}
 *          date 2017-06-19 14:56
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //百度地图的初始化
        SDKInitializer.initialize(getApplicationContext());
        SDKInitializer.setCoordType(CoordType.BD09LL);//默认为BD09LL坐标

        ZXingLibrary.initDisplayOpinion(this);

        // 初始化参数依次为 this, AppId, AppKey
        AVOSCloud.initialize(this,"uoQqqT0dwOsryKRxT8JcsbPM-9Nh9j0Va","0sxHHozNS4MfAwYMReXXehPD");
        AVOSCloud.setDebugLogEnabled(false);
        AVAnalytics.enableCrashReport(this, false);



    }
}
