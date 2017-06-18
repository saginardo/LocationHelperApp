package com.notinglife.android.LocationHelper.utils;

import android.util.Log;

/**
 * ${DESCRIPTION}
 *
 * @author saginardo
 * @version ${VERSION}
 *          date 2017-06-09 9:23
 */

public class LogUtil {
    /** 日志输出级别NONE */
    public static final int LEVEL_NONE = 0;
    /** 日志输出级别E */
    public static final int LEVEL_ERROR =1;
    /** 日志输出级别W */
    public static final int LEVEL_WARN = 2;
    /** 日志输出级别I */
    public static final int LEVEL_INFO = 3;
    /** 日志输出级别D */
    public static final int LEVEL_DEBUG = 4;
    /** 日志输出级别V */
    public static final int LEVEL_VERBOSE = 5;

    /** 日志输出时的TAG */
    //private static String mTag = "LogUtils";
    /** 是否允许输出log */
    private static int mDebuggable = LEVEL_DEBUG;

    /** 以级别为 d 的形式输出LOG */
    public static void v(String mTag,String msg) {
        if (mDebuggable >= LEVEL_VERBOSE) {
            Log.v(mTag, msg);
        }
    }

    /** 以级别为 d 的形式输出LOG */
    public static void d(String mTag,String msg) {
        if (mDebuggable >= LEVEL_DEBUG) {
            Log.d(mTag, msg);
        }
    }

    /** 以级别为 i 的形式输出LOG */
    public static void i(String mTag,String msg) {
        if (mDebuggable >= LEVEL_INFO) {
            Log.i(mTag, msg);
        }
    }

    /** 以级别为 w 的形式输出LOG */
    public static void w(String mTag,String msg) {
        if (mDebuggable >= LEVEL_WARN) {
            Log.w(mTag, msg);
        }
    }

    /** 以级别为 w 的形式输出Throwable */
    public static void w(String mTag,Throwable tr) {
        if (mDebuggable >= LEVEL_WARN) {
            Log.w(mTag, "", tr);
        }
    }

    /** 以级别为 w 的形式输出LOG信息和Throwable */
    public static void w(String mTag,String msg, Throwable tr) {
        if (mDebuggable >= LEVEL_WARN && null != msg) {
            Log.w(mTag, msg, tr);
        }
    }

    /** 以级别为 e 的形式输出LOG */
    public static void e(String mTag,String msg) {
        if (mDebuggable >= LEVEL_ERROR) {
            Log.e(mTag, msg);
        }
    }

    /** 以级别为 e 的形式输出Throwable */
    public static void e(String mTag,Throwable tr) {
        if (mDebuggable >= LEVEL_ERROR) {
            Log.e(mTag, "", tr);
        }
    }

    /** 以级别为 e 的形式输出LOG信息和Throwable */
    public static void e(String mTag, String msg, Throwable tr) {
        if (mDebuggable >= LEVEL_ERROR && null != msg) {
            Log.e(mTag, msg, tr);
        }
    }
}
