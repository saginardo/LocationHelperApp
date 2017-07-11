package com.notinglife.android.LocationHelper.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.notinglife.android.LocationHelper.R;
import com.notinglife.android.LocationHelper.domain.MsgData;
import com.notinglife.android.LocationHelper.utils.GlobalConstant;
import com.notinglife.android.LocationHelper.utils.LogUtil;
import com.notinglife.android.LocationHelper.utils.NetUtil;
import com.notinglife.android.LocationHelper.utils.OkHttpUtil;
import com.notinglife.android.LocationHelper.utils.SPUtil;
import com.notinglife.android.LocationHelper.utils.ToastUtil;

import java.io.IOException;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class HttpActivity extends AppCompatActivity {

    private static final String TAG = "HttpActivity";
    @BindView(R.id.bt_login)
    Button mBtLogin;
    @BindView(R.id.bt_query_devices)
    Button mBtQueryDevices;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_http);
        ButterKnife.bind(this);
        mContext = this;
        mBtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
        mBtQueryDevices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                queryDevices();
            }
        });
    }

    private void queryDevices() {
        NetUtil.NetState netState = NetUtil.getNetState(mContext);
        LogUtil.i(TAG, "网络类型" + netState);
        if (netState == NetUtil.NetState.NET_NO) {
            ToastUtil.showShortToast(mContext, "没有网络~~~");
        } else {
            OkHttpUtil.doGet(mContext, GlobalConstant.QUERY_DEVICES_URL, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    LogUtil.i(TAG, e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        MsgData msgData = new Gson().fromJson(response.body().string(), MsgData.class);
                        //110
                        if (msgData != null) {
                            LogUtil.i(TAG, msgData.code + "");
                            LogUtil.i(TAG, msgData.message);
                            LogUtil.i(TAG, msgData.devices.toString());
                        }
                        //104 101需要重新登录
                    } catch (JsonSyntaxException e) {
                        LogUtil.i(TAG, "json解析失败！");
                    }
                }
            });
        }
    }


    private void login() {

        LogUtil.i(TAG, "设备appUUID : " + GlobalConstant.getUUID(mContext));

        HashMap<String, String> keyValue = new HashMap<>();
        keyValue.put("username", "admin");
        keyValue.put("password", "123456");
        keyValue.put("appUUID", GlobalConstant.getUUID(mContext));
        OkHttpUtil.doPost(mContext, GlobalConstant.LOGIN_URL, keyValue, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                try {
                    MsgData msgData = new Gson().fromJson(response.body().string(), MsgData.class);
                    LogUtil.i(TAG, msgData.code + "");
                    //100代表登录成功
                    if (msgData.code == 100) {
                        String[] split = msgData.message.split("#");
                        if (split.length == 2) {
                            SPUtil.setString(mContext, "token1", split[0]);
                            SPUtil.setString(mContext, "token2", split[1]);
                        }

                    }
                } catch (JsonSyntaxException e) {
                    LogUtil.i(TAG, "json解析失败！");
                }

            }
        });
    }

}
