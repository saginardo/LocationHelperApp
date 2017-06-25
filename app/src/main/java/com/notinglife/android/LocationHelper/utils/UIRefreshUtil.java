package com.notinglife.android.LocationHelper.utils;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.avos.avoscloud.AVUser;

/**
 * ${DESCRIPTION}
 *
 * @author saginardo
 * @version ${VERSION}
 *          date 2017-06-21 9:11
 */

public class UIRefreshUtil {


    //登录登出标志位
    private final static int ON_LOGIN = 10;
    private final static int ON_LOGOUT = 11;


    public static void onLogout(Context context){
        AVUser.logOut();
        //通过本地广播通知刷新UI
        Intent logoutIntent = new Intent("com.notinglife.android.action.ON_LOGOUT");
        logoutIntent.putExtra("flag", ON_LOGOUT);
        LocalBroadcastManager.getInstance(context).sendBroadcast(logoutIntent);
    }
}
