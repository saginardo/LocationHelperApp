package com.notinglife.android.LocationHelper.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.notinglife.android.LocationHelper.R;
import com.notinglife.android.LocationHelper.utils.LogUtil;
import com.notinglife.android.LocationHelper.utils.SPUtil;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class UserDetailActivity extends AppCompatActivity {

    @BindView(R.id.mine_toolBar)
    Toolbar mMineToolBar;
    @BindView(R.id.iv_user_image)
    ImageView mIvUserImage;
    @BindView(R.id.tv_username)
    TextView mTvUsername;
    @BindView(R.id.tv_emailAddress)
    TextView mTvEmailAddress;
    @BindView(R.id.rl_user_info)
    RelativeLayout mRlUserInfo;
    @BindView(R.id.mine_request_change_password)
    TextView mMineRequestChangePassword;
    @BindView(R.id.mine_request_change_email)
    TextView mMineRequestChangeEmail;
    @BindView(R.id.mine_setting)
    Button mMineSetting;
    @BindView(R.id.mine_logout)
    Button mMineLogout;
    @BindView(R.id.mine_about)
    Button mMineAbout;

    private static final String TAG = "UserDetailActivity";
    private final static int ON_EDIT_USER_EMAIL = 30;
    private final static int ON_CHANGE_USER_PASSWORD = 31;

    private Activity mActivity;
    private MyHandler mHandler;
    private Unbinder mUnBinder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);
        mUnBinder = ButterKnife.bind(this);
        mActivity = this;
        mHandler = new MyHandler(this);


        setSupportActionBar(mMineToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (SPUtil.getString(mActivity,"username",null) != null) {

            String username = SPUtil.getString(mActivity,"username",null);
            mTvUsername.setText(username);


           /* mMineRequestChangePassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODOED: 2017/6/21 修改用户密码的逻辑
                    // FIXME: 2017/6/21 增加确认对话框
                    DialogUtil.showUserEditDialog(mActivity, mHandler, "修改用户密码", "请确定是否重置密码，重置链接将发送到注册邮箱", user, ON_CHANGE_USER_PASSWORD);

                }
            });


            mMineRequestChangeEmail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO: 2017/6/20 修改用户注册邮箱
                    DialogUtil.showUserEditDialog(mActivity, mHandler, "修改用户邮箱", "请确定修改如下用户邮箱", user, ON_EDIT_USER_EMAIL);
                }
            });*/
        }

    }


    private static class MyHandler extends Handler {

        WeakReference<UserDetailActivity> mActivity;

        MyHandler(UserDetailActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            final UserDetailActivity activity = mActivity.get();
            if (activity != null) {// 先判断弱引用是否为空，为空则不更新UI

                int flag = msg.what;

                if (flag == ON_EDIT_USER_EMAIL) {//接收修改邮箱的本地广播

                }
                if (flag == ON_CHANGE_USER_PASSWORD) {//接收修改密码的本地广播

                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnBinder.unbind();
    }
}
