package com.notinglife.android.LocationHelper.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avos.avoscloud.AVUser;
import com.notinglife.android.LocationHelper.R;
import com.notinglife.android.LocationHelper.activity.LoginActivity;
import com.notinglife.android.LocationHelper.utils.LogUtil;
import com.notinglife.android.LocationHelper.utils.ToastUtil;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * ${DESCRIPTION}
 *
 * @author saginardo
 * @version ${VERSION}
 *          date 2017-06-13 19:30
 */

public class MineFragment extends Fragment {


    @BindView(R.id.iv_user_image)
    ImageView mIvUserImage;
    @BindView(R.id.tv_login_title)
    TextView mTvLoginTitle;
    @BindView(R.id.tv_login_hint)
    TextView mTvLoginHint;
    @BindView(R.id.mine_device_to_repair)
    Button mMineDeviceToRepair;
    @BindView(R.id.mine_full_info)
    Button mMineFullInfo;
    @BindView(R.id.mine_setting)
    Button mMineSetting;
    @BindView(R.id.mine_about)
    Button mMineAbout;
    @BindView(R.id.rl_user_info)
    RelativeLayout mRlUserInfo;
    @BindView(R.id.mine_logout)
    Button mMineLogout;


    private Activity mActivity;
    private MyHandler mHandler;
    private LocalBroadcastManager broadcastManager;
    private MyReceiver mReceiver;

    private static final String TAG = "MineFragment";

    //登录登出标志位
    private final static int ON_LOGIN = 10;
    private final static int ON_LOGOUT = 11;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
        mHandler = new MyHandler(MineFragment.this);


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = View.inflate(mActivity, R.layout.fragment_mine, null);
        ButterKnife.bind(this, view);

        if (AVUser.getCurrentUser() != null) {
            LogUtil.i(TAG, AVUser.getCurrentUser().toString());
            String username = AVUser.getCurrentUser().getUsername();
            String email = AVUser.getCurrentUser().getEmail();
            mTvLoginTitle.setText(username);
            mTvLoginHint.setText("欢迎使用本系统");
            mMineLogout.setVisibility(View.VISIBLE);
        } else {
            mTvLoginTitle.setText("请登录系统");
            mTvLoginHint.setText("登陆后同步云端");
            mMineLogout.setVisibility(View.GONE);
        }

        mTvLoginTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTvLoginTitle.getText().toString().equals("请登录系统")) {
                    startActivity(new Intent(mActivity, LoginActivity.class));
                    mActivity.finish();
                }
            }
        });
        mMineLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AVUser.logOut();
                //通过本地广播通知刷新UI
                Intent logoutIntent = new Intent("com.notinglife.android.action.ON_LOGOUT");
                logoutIntent.putExtra("flag", ON_LOGOUT);
                LocalBroadcastManager.getInstance(mActivity).sendBroadcast(logoutIntent);

               ToastUtil.showShortToast(mActivity,"已登出本系统");
            }
        });

        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //动态注册本地广播，以便通知设备的添加和删除，可以及时更新recyclerview
        broadcastManager = LocalBroadcastManager.getInstance(mActivity);
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.notinglife.android.action.ON_LOGOUT");
        mReceiver = new MyReceiver();
        broadcastManager.registerReceiver(mReceiver, filter);
        mHandler = new MyHandler(MineFragment.this);


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private static class MyHandler extends Handler {

        WeakReference<MineFragment> mFragment;

        MyHandler(MineFragment fragment) {
            mFragment = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            MineFragment fragment = mFragment.get();
            if (fragment != null) {// 先判断弱引用是否为空，为空则不更新UI
                int flag = msg.what;
                //接收注销本地广播，更新UI等逻辑
                if (flag == ON_LOGOUT) {
                    //注销的UI更新
                    fragment.mTvLoginTitle.setText("请登录系统");
                    fragment.mTvLoginHint.setText("登陆后同步云端");
                    fragment.mMineLogout.setVisibility(View.GONE);
                }
            }
        }
    }

    //用于更新登录登出逻辑的广播
    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //接收到本地广播后先判断标志位
            int flag = intent.getIntExtra("flag", -1);

            if (flag == ON_LOGOUT) {
                //Bundle on_save_data = intent.getBundleExtra("on_save_data");
                //LocationDevice tmpDevice = (LocationDevice) on_save_data.getSerializable("on_save_data");
                Message message = Message.obtain();
                message.what = ON_LOGOUT;
                //message.obj = tmpDevice;
                LogUtil.i(TAG, " 消息标志位 " + message.what);
                mHandler.sendMessage(message);
            }


        }
    }
}
