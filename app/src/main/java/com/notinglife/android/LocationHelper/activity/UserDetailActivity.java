package com.notinglife.android.LocationHelper.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.RequestEmailVerifyCallback;
import com.avos.avoscloud.RequestPasswordResetCallback;
import com.avos.avoscloud.SaveCallback;
import com.notinglife.android.LocationHelper.R;
import com.notinglife.android.LocationHelper.domain.User;
import com.notinglife.android.LocationHelper.utils.LogUtil;
import com.notinglife.android.LocationHelper.utils.ToastUtil;
import com.notinglife.android.LocationHelper.utils.UIRefreshUtil;
import com.notinglife.android.LocationHelper.utils.UIUtil;

import java.lang.ref.WeakReference;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

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
    private Activity mActivity;
    private MyHandler mHandler;


    private final static int ON_EDIT_USER_EMAIL = 7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);
        ButterKnife.bind(this);
        mActivity = this;
        mHandler = new MyHandler(this);


        setSupportActionBar(mMineToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (AVUser.getCurrentUser() != null) {

            final AVUser currentUser = AVUser.getCurrentUser();
            final String username = currentUser.getUsername();
            final String email = currentUser.getEmail();
            Date createdAt = currentUser.getCreatedAt();
            //查出来的云对象保存为本地对象
            final User user = new User();
            user.mUsername =username;
            user.mEmail = email;

            LogUtil.i(TAG,createdAt.toString());

            mTvUsername.setText(username);
            mTvEmailAddress.setText(email);

            mMineRequestChangePassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODOED: 2017/6/21 修改用户密码的逻辑
                    // FIXME: 2017/6/21 增加确认对话框
                    LogUtil.i(TAG,"修改密码被点击了");
                    AVUser.requestPasswordResetInBackground(email, new RequestPasswordResetCallback() {
                        @Override
                        public void done(AVException e) {
                            if (e == null) {
                                ToastUtil.showShortToast(getApplicationContext(), "密码重置链接已发送到注册邮箱");
                                UIRefreshUtil.onLogout(getApplicationContext());
                                finish();
                            } else {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            });


            mMineRequestChangeEmail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO: 2017/6/20 修改用户注册邮箱
                    LogUtil.i(TAG, "修改邮箱被点击了");
                    UIUtil.showEmailEditDialog(mActivity, mHandler, "修改用户邮箱", "请确定修改如下用户邮箱", user);
                }
            });
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
                    User user = (User) msg.obj;
                    final String email = user.mEmail;

                    AVUser.getCurrentUser().saveInBackground(new SaveCallback() {
                        @Override
                        public void done(AVException e) {
                            AVUser.getCurrentUser().put("email", email);
                            AVUser.getCurrentUser().saveInBackground(new SaveCallback() {
                                @Override
                                public void done(AVException e) {
                                    if(e==null){
                                        ToastUtil.showShortToast(activity,"设置成功,请查收邮以重新激活");
                                        AVUser.requestEmailVerifyInBackground(email, new RequestEmailVerifyCallback() {
                                            @Override
                                            public void done(AVException e) {
                                                //do nothing
                                            }
                                        });
                                    }
                                }
                            });


                        }
                    });
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
}
