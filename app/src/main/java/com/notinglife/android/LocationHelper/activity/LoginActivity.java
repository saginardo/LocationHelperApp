package com.notinglife.android.LocationHelper.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.LogInCallback;
import com.avos.avoscloud.RequestEmailVerifyCallback;
import com.notinglife.android.LocationHelper.R;
import com.notinglife.android.LocationHelper.utils.LogUtil;
import com.notinglife.android.LocationHelper.utils.RegexValidator;
import com.notinglife.android.LocationHelper.utils.ToastUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

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
    private Context mContext;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        mContext = getApplicationContext();

        if (AVUser.getCurrentUser() != null) {
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

        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                //LoginActivity.this.finish();//如果取消注册还需要回到登录界面，所以不弹出LoginActivity
            }
        });


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
    protected void onPause() {
        super.onPause();
        AVAnalytics.onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AVAnalytics.onResume(this);
    }

}
