package com.notinglife.android.LocationHelper.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;
import com.avos.avoscloud.RequestEmailVerifyCallback;
import com.notinglife.android.LocationHelper.R;
import com.notinglife.android.LocationHelper.utils.RegexValidator;
import com.notinglife.android.LocationHelper.utils.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

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
    @BindView(R.id.sc_login_form)
    ScrollView mScLoginForm;
    @BindView(R.id.email_resend_button)
    Button mEmailResendButton;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

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
                LoginActivity.this.finish();
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
                        LoginActivity.this.finish();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    } else {
                        showProgress(false);
                        switch (e.getCode()) {
                            case 216:
                                ToastUtil.showShortToast(getApplicationContext(), "请验证邮件，若邮件遗失，请点击下方按钮重发");
                                mEmailResendButton.setVisibility(View.VISIBLE);
                                mEmailResendButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        //邮箱还没有验证，点击后重新请求邮箱验证码
                                        AVUser.requestEmailVerifyInBackground("abc@xyz.com", new RequestEmailVerifyCallback() {
                                            @Override
                                            public void done(AVException e) {
                                                if (e == null) {
                                                    // 求重发验证邮件成功
                                                    ToastUtil.showShortToast(getApplicationContext(), "验证邮件已经重新发送，请检查注册邮箱");
                                                }
                                            }
                                        });
                                    }
                                });

                                break;
                            case 205:
                                ToastUtil.showShortToast(getApplicationContext(),"该用户还未设置邮箱");
                                break;
                            case 211:
                                ToastUtil.showShortToast(getApplicationContext(),"该用户名尚未注册，请检查是否填写错误");

                            default:
                                break;
                        }

                        Log.e("LOGIN", e.getMessage());
                        //Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean isShow) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mScLoginForm.setVisibility(isShow ? View.GONE : View.VISIBLE);
            mScLoginForm.animate().setDuration(shortAnimTime).alpha(
                    isShow ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mScLoginForm.setVisibility(isShow ? View.GONE : View.VISIBLE);
                }
            });

            mLoginProgress.setVisibility(isShow ? View.VISIBLE : View.GONE);
            mLoginProgress.animate().setDuration(shortAnimTime).alpha(
                    isShow ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginProgress.setVisibility(isShow ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mLoginProgress.setVisibility(isShow ? View.VISIBLE : View.GONE);
            mScLoginForm.setVisibility(isShow ? View.GONE : View.VISIBLE);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
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
