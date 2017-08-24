package com.notinglife.android.LocationHelper.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import java.io.Serializable;

/**
 * ${DESCRIPTION}
 *
 * @author saginardo
 * @version ${VERSION}
 *          date 2017-06-29 21:11
 */

public class MyLocalBroadcastManager {

    /**
     * 抽取本地广播发送的方法
     *
     * @param context   广播依赖的Activity 对象
     * @param action     广播的action
     * @param flag       广播的类型标志位
     * @param extraKey   intent的额外数据 String类型 键
     * @param extraValue intent的额外数据 int类型 值
     * @param bundleName 额外bundle数据 键
     * @param t          额外bundle数据 值，实现了Serializable的泛型对象
     */
    public static <T extends Serializable> void sendLocalBroadcast(Activity activity, String action, int flag
            , String extraKey, int extraValue, String bundleName, T t) {
        Intent intent = new Intent("com.notinglife.android.action." + action);
        intent.putExtra("flag", flag);
        if (extraKey != null) {
            intent.putExtra(extraKey, extraValue);
        }
        if (bundleName != null) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(bundleName, t);
            intent.putExtra(bundleName, bundle);
        }
        LocalBroadcastManager.getInstance(activity).sendBroadcast(intent);
    }

    public static void sendLocalBroadcast(Context context, String action, String extraString, String extraValue) {
        Intent intent = new Intent(action);
        if (extraString != null) {
            intent.putExtra(extraString, extraValue);
        }
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}
