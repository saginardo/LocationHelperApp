package com.notinglife.android.LocationHelper.utils;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.notinglife.android.LocationHelper.domain.MsgData;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * ${DESCRIPTION}
 *
 * @author saginardo
 * @version ${VERSION}
 *          date 2017-07-09 15:06
 */

public class OkHttpUtil {
    private static final String TAG = "OkHttpUtil";

    private static OkHttpClient client = null;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private OkHttpUtil() {
    }

    public static OkHttpClient getInstance(Context context) {
        if (client == null) {
            synchronized (OkHttpUtil.class) {
                if (client == null) {
                    //noinspection ConstantConditions
                    client = new OkHttpClient.Builder()
                            //.cookieJar(new CookiesManager(context))
                            .connectTimeout(5, TimeUnit.SECONDS)
                            .writeTimeout(5, TimeUnit.SECONDS)
                            .readTimeout(10, TimeUnit.SECONDS)
                            .cache(new Cache(context.getExternalCacheDir(), GlobalConstant.CACHE_SIZE))
                            .addInterceptor(new TokenInterceptor(context))
                            .build();
                }
            }
        }
        return client;
    }

    /**
     * Get请求
     *
     * @param url
     * @param callback
     */

    /*
     * 缓存策略需要服务器携带 header
     * Expires: Mon, 10 Jul 2017 12:51:47 GMT
     * Cache-Control: max-age=60
     * 即服务器设置
     *   response.setDateHeader("Expires", System.currentTimeMillis() + 60 * 1000);
     *   response.setHeader("Cache-Control", "max-age=60");
     */
    public static void doGet(Context context, String url, Callback callback) {

        Request request = new Request.Builder()
                .url(url)
                .addHeader("token", SPUtil.getString(context, "token1", ""))
                .addHeader("appUUID", GlobalConstant.getUUID(context))
                //.cacheControl(new CacheControl.Builder().maxAge(60, TimeUnit.SECONDS).build())
                .build();
        Call call = getInstance(context).newCall(request);
        call.enqueue(callback);

    }


    /**
     * Post请求发送键值对数据
     *
     * @param url
     * @param mapParams
     * @param callback
     */
    public static void doPost(Context context, String url, Map<String, String> mapParams, Callback callback) {
        FormBody.Builder builder = new FormBody.Builder();
        for (String key : mapParams.keySet()) {
            builder.add(key, mapParams.get(key));
        }
        Request request = new Request.Builder()
                .url(url)
                .addHeader("token", SPUtil.getString(context, "token1", ""))
                .addHeader("appUUID", GlobalConstant.getUUID(context))
                .post(builder.build())
                .build();
        Call call = getInstance(context).newCall(request);
        call.enqueue(callback);
    }

    public static void doPostJSON(Context context, String url, String json, Callback callback) {

        RequestBody requestBody = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .addHeader("token", SPUtil.getString(context, "token1", ""))
                .addHeader("appUUID", GlobalConstant.getUUID(context))
                .post(requestBody)
                .build();
        Call call = getInstance(context).newCall(request);
        call.enqueue(callback);
    }


    public static MsgData doSyncPost(Context context, String url, Map<String, String> mapParams) {
        FormBody.Builder builder = new FormBody.Builder();
        for (String key : mapParams.keySet()) {
            builder.add(key, mapParams.get(key));
        }
        Request request = new Request.Builder()
                .url(url)
                .addHeader("token", SPUtil.getString(context, "token1", ""))
                .addHeader("appUUID", GlobalConstant.getUUID(context))
                .post(builder.build())
                .build();
        Call call = getInstance(context).newCall(request);
        try {
            Response response = call.execute(); //同步请求
            if (response.isSuccessful()) {
                try {
                    return new Gson().fromJson(response.body().string(), MsgData.class);
                } catch (JsonSyntaxException e) {
                    LogUtil.i(TAG, "json解析失败！");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * Post请求发送JSON数据
     *
     * @param url
     * @param jsonParams
     * @param callback
     */
    public static void doPost(Context context, String url, String jsonParams, Callback callback) {
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8")
                , jsonParams);
        Request request = new Request.Builder()
                .url(url)
                .addHeader("token", SPUtil.getString(context, "token1", ""))
                .addHeader("appUUID", GlobalConstant.getUUID(context))
                .post(body)
                .build();
        Call call = getInstance(context).newCall(request);
        call.enqueue(callback);
    }


    /**
     * 上传文件
     *
     * @param url
     * @param pathName
     * @param fileName
     * @param callback
     */
    public static void doFile(Context context, String url, String pathName, String fileName, Callback callback) {
        //判断文件类型
        MediaType MEDIA_TYPE = MediaType.parse(judgeType(pathName));
        //创建文件参数
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(MEDIA_TYPE.type(), fileName,
                        RequestBody.create(MEDIA_TYPE, new File(pathName)));
        //发出请求参数
        Request request = new Request.Builder()
                .header("Authorization", "Client-ID " + "9199fdef135c122")
                .addHeader("token", SPUtil.getString(context, "token1", ""))
                .addHeader("appUUID", GlobalConstant.getUUID(context))
                .url(url)
                .post(builder.build())
                .build();
        Call call = getInstance(context).newCall(request);
        call.enqueue(callback);

    }

    /**
     * 根据文件路径判断MediaType
     *
     * @param path
     * @return
     */
    private static String judgeType(String path) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String contentTypeFor = fileNameMap.getContentTypeFor(path);
        if (contentTypeFor == null) {
            contentTypeFor = "application/octet-stream";
        }
        return contentTypeFor;
    }

    /**
     * 下载文件
     *
     * @param url
     * @param fileDir
     * @param fileName
     */
    public static void downFile(Context context, String url, final String fileDir, final String fileName) {
        Request request = new Request.Builder()
                .url(url)
                .addHeader("token", SPUtil.getString(context, "token1", ""))
                .addHeader("appUUID", GlobalConstant.getUUID(context))
                .build();
        Call call = getInstance(context).newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int length;
                FileOutputStream fos = null;
                try {
                    is = response.body().byteStream();
                    File file = new File(fileDir, fileName);
                    fos = new FileOutputStream(file);
                    while ((length = is.read(buf)) != -1) {
                        fos.write(buf, 0, length);
                    }
                    fos.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (is != null) is.close();
                    if (fos != null) fos.close();
                }
            }
        });
    }

}
