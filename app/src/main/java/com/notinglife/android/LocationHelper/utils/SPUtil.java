package com.notinglife.android.LocationHelper.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 用于操作SharedPreferences的工具类
 *
 * @author saginardo
 * @version v1.0
 * date 2017-04-25 10:18
 */

public class SPUtil {

    public static boolean getBoolean(Context context, String key, boolean defaultValue){
        SharedPreferences config = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        return config.getBoolean(key,defaultValue);
    }


    public static void setBoolean(Context context, String key, boolean value){
        SharedPreferences config = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        config.edit().putBoolean(key,value).apply();
    }

    public static String getString(Context context, String key, String defaultValue){
        SharedPreferences config = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        return config.getString(key,defaultValue);
    }

    public static void setString(Context context, String key, String value){
        SharedPreferences config = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        config.edit().putString(key,value).apply();
    }

    public static int getInt(Context context, String key, int defaultValue){
        SharedPreferences config = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        return config.getInt(key,defaultValue);
    }

    public static void setInt(Context context, String key, int value){
        SharedPreferences config = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        config.edit().putInt(key,value).apply();
    }
}
