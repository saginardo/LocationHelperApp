package com.notinglife.android.LocationHelper.utils;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;

import java.util.UUID;

/**
 * 全局常量
 *
 * @author saginardo
 * @version ${VERSION}
 *          date 2017-07-09 16:06
 */

public class GlobalConstant {
    //URL "http://192.168.123.21/device/appLogin.action"
    public static final String WEB_BASE_URL = "http://192.168.123.21/device/";
    public static final String LOGIN_URL = WEB_BASE_URL + "appLogin.action";
    public static final String REGISTER_URL = WEB_BASE_URL + "appRegister.action";


    public static final String REFRESH_TOKEN = WEB_BASE_URL + "refreshToken.action";

    //"http://192.168.123.21/device/queryDevicesToJson.action"
    public static final String QUERY_DEVICES_URL = WEB_BASE_URL + "queryDevicesToJson.action";

    public static final Integer CACHE_SIZE = 1024 * 1024 * 20; //20M空间

    //设备UUID

    public static String getUUID(Context context) {
        String ANDROID_ID = Settings.System.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        String m_szDevIDShort = "35" + Build.BOARD.length() % 10
                + Build.BRAND.length() % 10 + Build.SUPPORTED_ABIS[0].length() % 10
                + Build.DEVICE.length() % 10 + Build.DISPLAY.length() % 10
                + Build.HOST.length() % 10 + Build.ID.length() % 10
                + Build.MANUFACTURER.length() % 10
                + Build.MODEL.length() % 10
                + Build.PRODUCT.length() % 10
                + Build.TAGS.length() % 10
                + Build.TYPE.length() % 10
                + Build.USER.length() % 10;

        return new UUID(m_szDevIDShort.hashCode(), ANDROID_ID.hashCode()).toString();
    }


    public final static Integer ON_RESET_PASSWORD = 32;
    public final static Integer ON_LOGIN_SUCCESS = 33;
    public final static Integer ON_LOGIN_FAILED = 34;
    public final static Integer ON_LOGIN_ERROR = 35;
    public final static Integer ON_LOGIN_TIMEOUT = 36;

    //网络传输json数据的code 标志位
    public static final Integer MSG_LOGIN_SUCCESS = 100;  // 100  登录成功
    public static final Integer MSG_LOGIN_FAILED = 101; // 101  用户名和密码不匹配，请重试!
    public static final Integer MSG_TOEKN_EXPIRES = 102; // 102  token过期，请重新申请
    public static final Integer MSG_TOEKN_UPDATE = 103;// 103  token更新成功
    public static final Integer MSG_TOEKN_ERROR = 104;  // 104  token认证失败，重新登录

    public static final Integer MSG_REGISTER_SUCCESS = 105; // 105 注册成功
    public static final Integer MSG_REGISTER_NAMEREPEAT = 106;// 106 用户名重复
    public static final Integer MSG_REGISTER_EMAILREPEAT = 107; // 107 邮箱重复

    public static final Integer MSG_QUERYDEVICE_SUCCESS = 110; // 110  成功查询所有设备
    public static final Integer MSG_QUERYDEVICE_FAILED = 111; //  111  查询所有设备失败


}
