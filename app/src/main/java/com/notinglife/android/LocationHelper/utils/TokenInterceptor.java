package com.notinglife.android.LocationHelper.utils;

import android.content.Context;

import com.notinglife.android.LocationHelper.domain.MsgData;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import static com.notinglife.android.LocationHelper.utils.GlobalConstant.REFRESH_TOKEN;

/**
 * ${DESCRIPTION}
 *
 * @author saginardo
 * @version ${VERSION}
 *          date 2017-07-10 8:10
 */

public class TokenInterceptor implements Interceptor {
    private static final String TAG = "TokenInterceptor";
    private Context mContext;

    public TokenInterceptor(Context context) {
        this.mContext = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);

        LogUtil.i(TAG, "response.code=" + response.code());

        //根据和服务端的约定判断token过期
        if (isTokenExpired(response)) {
            LogUtil.i(TAG, "静默自动刷新Token,然后重新请求数据!");
            //同步请求方式，获取最新的Token
            String refreshToken = getNewToken(mContext);
            LogUtil.i(TAG,"获取到同步网络请求结果，新的Token是 : "+ refreshToken);

            //使用新的Token，完成最近一次拦截的请求，新的请求会重新读取本地SP文件
            Request newRequest = chain.request()
                    .newBuilder()
                    .header("token", refreshToken)
                    .build();
            //重新请求
            return chain.proceed(newRequest);
        }
        return response;
    }

    /**
     * 根据Response，判断Token是否失效
     *
     * @param response
     * @return
     */
    private boolean isTokenExpired(Response response) {
        if (response.code() == 404) {
            LogUtil.i(TAG,"HTTP响应状态码："+response.code());
            //这个header由服务器判断 token过期后，添加到respone中，注意key-value匹配
            LogUtil.i(TAG,"Header响应状态码："+response.header("code"));

            if(response.header("code").equals("102")){
                return true;
            }
        }
        return false;
    }

    /**
     * 同步请求方式，获取最新的Token
     *
     * @return
     */
    private String getNewToken(Context context) throws IOException {

        HashMap<String, String> keyValue = new HashMap<>();
        keyValue.put("token",SPUtil.getString(context,"token2",""));
        keyValue.put("refreshToken", GlobalConstant.getUUID(context));
        LogUtil.i(TAG,"正在准备刷新token");
        MsgData msgData = OkHttpUtil.doSyncPost(context, REFRESH_TOKEN, keyValue);

        if (msgData != null && msgData.code==103) {
            LogUtil.i(TAG,"同步请求的得到的msgData数据："+msgData.toString());
            //更换本地过期的token
            SPUtil.setString(mContext,"token1",msgData.message);
            return msgData.message;
        }
        return "";
    }


}
