package com.notinglife.android.LocationHelper.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.LogInCallback;
import com.avos.avoscloud.RequestEmailVerifyCallback;
import com.avos.avoscloud.RequestPasswordResetCallback;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.notinglife.android.LocationHelper.R;
import com.notinglife.android.LocationHelper.domain.MsgData;
import com.notinglife.android.LocationHelper.utils.DialogUtil;
import com.notinglife.android.LocationHelper.utils.GlobalConstant;
import com.notinglife.android.LocationHelper.utils.LogUtil;
import com.notinglife.android.LocationHelper.utils.NetUtil;
import com.notinglife.android.LocationHelper.utils.OkHttpUtil;
import com.notinglife.android.LocationHelper.utils.RegexValidator;
import com.notinglife.android.LocationHelper.utils.SPUtil;
import com.notinglife.android.LocationHelper.utils.ToastUtil;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.avos.avoscloud.AVException.EMAIL_NOT_FOUND;
import static com.avos.avoscloud.AVException.USERNAME_PASSWORD_MISMATCH;
import static com.avos.avoscloud.AVException.USER_DOESNOT_EXIST;

/**
 * ${DESCRIPTION}
 *
 * @author saginardo
 * @version ${VERSION}
 *          date 2017-06-19 8:06
 */


public class LoginActivity extends AppCompatActivity {


    private static final String TAG = "LoginActivity";

    @BindView(R.id.login_toolBar)
    Toolbar mLoginToolBar;
    @BindView(R.id.login_progress)
    ProgressBar mLoginProgress;
    @BindView(R.id.username)
    AutoCompleteTextView mUsernameView;
    @BindView(R.id.password)
    EditText mPasswordView;
    @BindView(R.id.username_login_button)
    Button mLoginButton;
    @BindView(R.id.username_register_button)
    Button mRegisterButton;

    @BindView(R.id.email_resend_button)
    Button mEmailResendButton;
    @BindView(R.id.reset_password_button)
    Button mResetPasswordButton;
    @BindView(R.id.email_login_form)
    LinearLayout mEmailLoginForm;
    private Context mContext;
    private Unbinder mUnBinder;
    private Handler mHandler;

    private final static int ON_RESET_PASSWORD = 32;
    private final static int ON_LOGIN_SUCCESS = 33;
    private final static int ON_LOGIN_FAILED = 34;
    private final static int ON_LOGIN_ERROR = 35;
    private final static int ON_LOGIN_TIMEOUT = 36;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mUnBinder = ButterKnife.bind(this);

        mContext = getApplicationContext();
        mHandler = new MyHandler(this);

        String username = SPUtil.getString(mContext, "username", null);

        if (username != null && !username.equals("")) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            LoginActivity.this.finish();
        }

        setSupportActionBar(mLoginToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
/*
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                //LoginActivity.this.finish();//如果取消注册还需要回到登录界面，所以不弹出LoginActivity
            }
        });

        mResetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });*/
    }

    private static class MyHandler extends Handler {
        WeakReference<LoginActivity> mActivity;

        MyHandler(LoginActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int flag = msg.what;
            final LoginActivity activity = mActivity.get();
            if (activity != null) {
                if (flag == ON_RESET_PASSWORD) {
                    String username = (String) msg.obj;
                    //LogUtil.i(TAG, "收到" + username + "修改密码的请求");
                    final String[] userEmail = new String[1];
                    AVQuery<AVObject> userEmailQuery = new AVQuery<>("UserEmail");
                    userEmailQuery.whereEqualTo("targetUserName", username);
                    userEmailQuery.findInBackground(new FindCallback<AVObject>() {
                        @Override
                        public void done(List<AVObject> list, AVException e) {
                            if (list != null && list.size() > 0) {
                                AVObject avObject = list.get(0);
                                userEmail[0] = avObject.getString("emailAddress");
                                //LogUtil.i(TAG, userEmail[0]);
                                AVUser.requestPasswordResetInBackground(userEmail[0], new RequestPasswordResetCallback() {
                                    @Override
                                    public void done(AVException e) {
                                        if (e == null) {
                                            //求重发验证邮件成功
                                            ToastUtil.showShortToast(activity, "验证邮件已经重新发送，请检查注册邮箱");
                                        }
                                    }
                                });
                            } else {
                                ToastUtil.showShortToast(activity, "没有查询到该用户，请检查用户名是否输入错误");
                            }
                        }
                    });
                }
                if (flag == ON_LOGIN_SUCCESS) {
                    ToastUtil.showShortToast(activity.mContext, "登录成功");
                    SPUtil.setString(activity.mContext, "username", msg.obj.toString());
                    activity.startActivity(new Intent(activity, MainActivity.class));
                    activity.finish();
                }
                if (flag == ON_LOGIN_FAILED) {
                    ToastUtil.showShortToast(activity.mContext, "登录失败");
                }
                if (flag == ON_LOGIN_ERROR) {
                    activity.showProgress(false);
                    ToastUtil.showShortToast(activity.mContext, "登录错误，请联系管理员");
                }
                if (flag == ON_LOGIN_TIMEOUT) {
                    activity.showProgress(false);
                    ToastUtil.showShortToast(activity.mContext, "登录超时，请检查网络连接");
                }
            }
        }
    }

    private void resetPassword() {
        DialogUtil.showUserEditDialog(this, mHandler, "重置密码", null, null, ON_RESET_PASSWORD);
    }

    private void acCloudLogin() {
        mUsernameView.setError(null);
        mPasswordView.setError(null);

        final String username = mUsernameView.getText().toString();
        final String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;
        if (!RegexValidator.isUserName(username)) {
            mUsernameView.setError(getString(R.string.error_username_invalid));
            focusView = mUsernameView;
            cancel = true;
        }
        if (!RegexValidator.isPassword(password)) {
            mPasswordView.setError(getString(R.string.error_password_invalid));
            focusView = mPasswordView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
            AVUser.logInInBackground(username, password, new LogInCallback<AVUser>() {
                @Override
                public void done(AVUser avUser, AVException e) {
                    if (e == null) {
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        LoginActivity.this.finish();
                    } else {
                        showProgress(false);
                        switch (e.getCode()) {
                            case 216:
                                ToastUtil.showShortToast(getApplicationContext(), "请验证邮件，若邮件遗失，请点击下方按钮重发");
                                mEmailResendButton.setVisibility(View.VISIBLE);
                                mEmailResendButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        //邮箱还没有验证，点击后重新请求邮箱验证码，
                                        // FIXED: 2017/6/23
                                        final String[] userEmail = new String[1];
                                        AVQuery<AVObject> userEmailQuery = new AVQuery<>("UserEmail");
                                        userEmailQuery.whereEqualTo("targetUserName", username);
                                        userEmailQuery.findInBackground(new FindCallback<AVObject>() {
                                            @Override
                                            public void done(List<AVObject> list, AVException e) {
                                                if (list != null && list.size() > 0) {
                                                    AVObject avObject = list.get(0);
                                                    userEmail[0] = avObject.getString("emailAddress");
                                                    LogUtil.i(TAG, userEmail[0]);
                                                    if (!TextUtils.isEmpty(userEmail[0])) {
                                                        AVUser.requestEmailVerifyInBackground(userEmail[0], new RequestEmailVerifyCallback() {
                                                            @Override
                                                            public void done(AVException e) {
                                                                if (e == null) {
                                                                    //求重发验证邮件成功
                                                                    ToastUtil.showShortToast(mContext, "验证邮件已经重新发送，请检查注册邮箱");
                                                                }
                                                            }
                                                        });
                                                    }
                                                } else {
                                                    ToastUtil.showShortToast(getApplicationContext(), "没有查询到该用户，请检查用户名是否输入错误");
                                                }
                                            }
                                        });
                                    }
                                });

                                break;
                            case EMAIL_NOT_FOUND:
                                ToastUtil.showShortToast(mContext, "该用户还未设置邮箱");
                                break;
                            case USER_DOESNOT_EXIST:
                                ToastUtil.showShortToast(mContext, "该用户名尚未注册，请检查是否填写错误");
                            case USERNAME_PASSWORD_MISMATCH:
                                ToastUtil.showShortToast(mContext, "用户名或密码错误");
                            default:
                                break;
                        }
                        Log.e("LOGIN", e.getMessage());
                    }
                }
            });
        }
    }


    private void attemptLogin() {

        mUsernameView.setError(null);
        mPasswordView.setError(null);

        final String username = mUsernameView.getText().toString();
        final String password = mPasswordView.getText().toString();


        boolean cancel = false;
        View focusView = null;
        if (!RegexValidator.isUserName(username)) {
            mUsernameView.setError(getString(R.string.error_username_invalid));
            focusView = mUsernameView;
            cancel = true;
        }
        if (!RegexValidator.isPassword(password)) {
            mPasswordView.setError(getString(R.string.error_password_invalid));
            focusView = mPasswordView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            //判断网络状态
            NetUtil.NetState netState = NetUtil.getNetState(mContext);
            if (netState == NetUtil.NetState.NET_NO) {
                ToastUtil.showShortToast(mContext, "没有网络，请打开网络连接");
            } else {
                showProgress(true);
                HashMap<String, String> keyValue = new HashMap<>();
                keyValue.put("username", username);
                keyValue.put("password", password);
                OkHttpUtil.doPost(mContext, GlobalConstant.LOGIN_URL, keyValue, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                        Message msg = Message.obtain();
                        msg.what = ON_LOGIN_TIMEOUT;
                        mHandler.sendMessage(msg);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Message msg = Message.obtain();
                        try {
                            MsgData msgData = new Gson().fromJson(response.body().string(), MsgData.class);
                            LogUtil.i(TAG, msgData.code + "");
                            //100代表登录成功
                            if (msgData.code == 100) {
                                String[] split = msgData.message.split("#");

                                if (split.length == 2) {

                                    SPUtil.setString(mContext, "token1", split[0]);
                                    SPUtil.setString(mContext, "token2", split[1]);
                                    msg.what = ON_LOGIN_SUCCESS;
                                    msg.obj = username;
                                    mHandler.sendMessage(msg);
                                } else {
                                    msg.what = ON_LOGIN_FAILED;
                                    mHandler.sendMessage(msg);
                                }
                            }
                        } catch (JsonSyntaxException e) {
                            LogUtil.i(TAG, "json解析失败！");
                            msg.what = ON_LOGIN_ERROR;
                            mHandler.sendMessage(msg);
                        }
                    }
                });
            }
        }


    }


    private void showProgress(final boolean isShow) {

        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
        mLoginProgress.setVisibility(isShow ? View.VISIBLE : View.GONE);
        mLoginProgress.animate().setDuration(shortAnimTime).alpha(
                isShow ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mLoginProgress.setVisibility(isShow ? View.VISIBLE : View.GONE);
            }
        });

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
