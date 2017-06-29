package com.notinglife.android.LocationHelper.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;
import com.avos.avoscloud.SignUpCallback;
import com.notinglife.android.LocationHelper.R;
import com.notinglife.android.LocationHelper.utils.LogUtil;
import com.notinglife.android.LocationHelper.utils.RegexValidator;
import com.notinglife.android.LocationHelper.utils.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.avos.avoscloud.AVException.EMAIL_TAKEN;
import static com.avos.avoscloud.AVException.USERNAME_TAKEN;

public class RegisterActivity extends AppCompatActivity {

    @BindView(R.id.toolBar)
    Toolbar mToolBar;
    @BindView(R.id.register_progress)
    ProgressBar mRegisterProgress;
    @BindView(R.id.username)
    AutoCompleteTextView mUsername;
    @BindView(R.id.password)
    EditText mPassword;
    @BindView(R.id.username_register_button)
    Button mUsernameRegisterButton;
    @BindView(R.id.email_register_form)
    LinearLayout mEmailRegisterForm;
    @BindView(R.id.emailaddress)
    EditText mEmailAddress;
    @BindView(R.id.password2)
    EditText mPassword2;
    @BindView(R.id.emailaddress2)
    EditText mEmailAddress2;

    private static final String TAG = "RegisterActivity";
    Unbinder mUnBinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mUnBinder = ButterKnife.bind(this);


        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.register || id == EditorInfo.IME_NULL) {
                    attemptRegister();
                    return true;
                }
                return false;
            }


        });

        mUsernameRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });
    }

    private void attemptRegister() {
        mUsername.setError(null);
        mPassword.setError(null);
        mEmailAddress.setError(null);

        final String username = mUsername.getText().toString();
        String password = mPassword.getText().toString();
        String password2 = mPassword2.getText().toString();
        final String emailAddress = mEmailAddress.getText().toString();
        String emailAddress2 = mEmailAddress2.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (!RegexValidator.isUserName(username)) {
            mUsername.setError(getString(R.string.username_hint));
            focusView = mUsername;
            cancel = true;
        }
        if (!RegexValidator.isPassword(password)) {
            mPassword.setError(getString(R.string.prompt_password_hint));
            focusView = mPassword;
            cancel = true;
        }
        if (!RegexValidator.isPassword(password2)) {
            mPassword2.setError(getString(R.string.prompt_password_hint));
            focusView = mPassword;
            cancel = true;
        }
        if (!password2.equals(password)) {
            mPassword2.setError(getString(R.string.password_repeat_fail));
            focusView = mPassword2;
            cancel = true;
        }

        if (!RegexValidator.isEmail(emailAddress)) {
            mEmailAddress.setError(getString(R.string.error_emil_invalid));
            focusView = mEmailAddress;
            cancel = true;
        }

        if (!RegexValidator.isEmail(emailAddress2)) {
            mEmailAddress2.setError(getString(R.string.prompt_password_hint));
            focusView = mEmailAddress2;
            cancel = true;
        }
        if (!emailAddress2.equals(emailAddress)) {
            mEmailAddress2.setError(getString(R.string.email_repeat_fail));
            focusView = mEmailAddress2;
            cancel = true;
        }


        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);

            final AVUser user = new AVUser();// 新建 AVUser 对象实例
            user.setUsername(username);// 设置用户名
            user.setPassword(password);// 设置密码
            user.setEmail(emailAddress);// 设置Email

            user.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(AVException e) {
                    if (e == null) {
                        // 建立_User表和 user_email表的一个 Pointer 映射关系
                        AVObject user_email = new AVObject("UserEmail");
                        user_email.put("emailAddress", emailAddress);
                        user_email.put("targetUserID", AVObject.createWithoutData("_User", user.getObjectId()));//Point指针类型
                        user_email.put("targetUserName", user.getUsername());
                        LogUtil.i(TAG, "保存附表中");
                        user_email.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(AVException e) {
                                if (e == null) {
                                    LogUtil.i(TAG, "保存附表成功");
                                    // 注册成功，把用户对象赋值给当前用户
                                    ToastUtil.showShortToast(getApplicationContext(), "注册成功，请激活邮箱");
                                    startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                    RegisterActivity.this.finish();
                                }
                            }
                        });

                    } else {
                        showProgress(false);
                        ToastUtil.showShortToast(RegisterActivity.this, e.getMessage());
                        switch (e.getCode()) {
                            case USERNAME_TAKEN:
                                ToastUtil.showShortToast(RegisterActivity.this, "用户名已存在，请重新设置");
                                break;
                            case EMAIL_TAKEN:
                                ToastUtil.showShortToast(RegisterActivity.this, "邮箱已存在，请重新设置");
                                break;
                            default:
                                break;
                        }
                    }
                }
            });
        }
    }


    private void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
        mRegisterProgress.setVisibility(show ? View.VISIBLE : View.GONE);
        mRegisterProgress.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mRegisterProgress.setVisibility(show ? View.VISIBLE : View.GONE);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnBinder.unbind();
    }
}
