package com.notinglife.android.LocationHelper.utils;

import android.content.Context;

import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;


/**
 * 自动管理Cookies
 *
 * @author saginardo
 * @version ${VERSION}
 *          date 2017-07-09 15:02
 */

public class CookiesManager implements CookieJar {

    private Context mContext;
    private static final String TAG = "CookiesManager";
    private PersistentCookieStore cookieStore;

    public CookiesManager(Context context) {
        this.mContext = context;
        cookieStore = new PersistentCookieStore(mContext);
    }

    @Override
    public void saveFromResponse(HttpUrl httpUrl, List<Cookie> cookies) {
        if (cookies != null && cookies.size() > 0) {
            for (Cookie item : cookies) {
                cookieStore.add(httpUrl, item);
            }
        }
    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl httpUrl) {
        return cookieStore.get(httpUrl);
    }
}
