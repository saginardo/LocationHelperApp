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
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.RequestEmailVerifyCallback;
import com.avos.avoscloud.RequestPasswordResetCallback;
import com.avos.avoscloud.SaveCallback;
import com.notinglife.android.LocationHelper.R;
import com.notinglife.android.LocationHelper.domain.User;
import com.notinglife.android.LocationHelper.utils.DialogUtil;
import com.notinglife.android.LocationHelper.utils.LogUtil;
import com.notinglife.android.LocationHelper.utils.SPUtil;
import com.notinglife.android.LocationHelper.utils.ToastUtil;
import com.notinglife.android.LocationHelper.utils.UIRefreshUtil;

import java.lang.ref.WeakReference;
import java.util.Date;
import java.util.List;

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

            final AVUser currentUser = AVUser.getCurrentUser();
            final String mObjectID = currentUser.getObjectId();
            final String username = currentUser.getUsername();
            final String email = currentUser.getEmail();
            Date createdAt = currentUser.getCreatedAt();
            //查出来的云对象保存为本地对象
            final User user = new User();
            user.mObjectId = mObjectID;
            user.mUsername = username;
            user.mEmail = email;

            LogUtil.i(TAG, createdAt.toString());

            mTvUsername.setText(username);
            mTvEmailAddress.setText(email);

            mMineRequestChangePassword.setOnClickListener(new View.OnClickListener() {
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
                    final String mObjectId = user.mObjectId;
                    AVUser.getCurrentUser().saveInBackground(new SaveCallback() {
                        @Override
                        public void done(AVException e) {
                            AVUser.getCurrentUser().put("email", email);
                            AVUser.getCurrentUser().saveInBackground(new SaveCallback() {
                                @Override
                                public void done(AVException e) {
                                    if (e == null) {
                                        ToastUtil.showLongToast(activity, "请查收邮件激活后，重新登录");
                                        AVUser.requestEmailVerifyInBackground(email, new RequestEmailVerifyCallback() {
                                            @Override
                                            public void done(AVException e) {
                                                //do nothing
                                            }
                                        });
                                        // Pointer 查询, 更新对应用户指向的Pointer的邮箱
                                        AVQuery<AVObject> pointerQuery = new AVQuery<>("UserEmail");
                                        pointerQuery.whereEqualTo("targetUserID", AVObject.createWithoutData("targetUserID", mObjectId));
                                        pointerQuery.findInBackground(new FindCallback<AVObject>() {
                                            @Override
                                            public void done(List<AVObject> list, AVException e) {
                                                if (list != null && list.size() > 0) {
                                                    AVObject avObject = list.get(0);
                                                    avObject.put("emailAddress", email);
                                                    avObject.saveInBackground(new SaveCallback() {
                                                        @Override
                                                        public void done(AVException e) {
                                                            if (e == null) {
                                                                //UserEmail表保存成功，
                                                                UIRefreshUtil.onLogout(activity);
                                                                activity.finish();
                                                            }
                                                        }
                                                    });
                                                }
                                            }
                                        });

                                    }
                                }
                            });
                        }
                    });
                }
                if (flag == ON_CHANGE_USER_PASSWORD) {
                    User user = (User) msg.obj;
                    final String email = user.mEmail;
                    LogUtil.i(TAG, "用户当前邮箱为：" + email);
                    AVUser.requestPasswordResetInBackground(email, new RequestPasswordResetCallback() {
                        @Override
                        public void done(AVException e) {
                            if (e == null) {
                                ToastUtil.showShortToast(activity, "密码重置链接已发送到注册邮箱");
                                UIRefreshUtil.onLogout(activity);
                                activity.finish();
                            } else {
                                e.printStackTrace();
                            }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnBinder.unbind();
    }
}
