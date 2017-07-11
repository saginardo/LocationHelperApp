package com.notinglife.android.LocationHelper.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.notinglife.android.LocationHelper.R;
import com.notinglife.android.LocationHelper.domain.MsgData;
import com.notinglife.android.LocationHelper.domain.User;
import com.notinglife.android.LocationHelper.utils.GlobalConstant;
import com.notinglife.android.LocationHelper.utils.LogUtil;
import com.notinglife.android.LocationHelper.utils.NetUtil;
import com.notinglife.android.LocationHelper.utils.OkHttpUtil;
import com.notinglife.android.LocationHelper.utils.RegexValidator;
import com.notinglife.android.LocationHelper.utils.ToastUtil;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

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
    private Unbinder mUnBinder;
    private Context mContext;
    private MyHandler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mUnBinder = ButterKnife.bind(this);
        mContext = this;
        mHandler = new MyHandler(this);

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


    private static class MyHandler extends Handler {
        WeakReference<RegisterActivity> mActivity;

        MyHandler(RegisterActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int flag = msg.what;
            final RegisterActivity activity = mActivity.get();
            if (activity != null) {
                if (flag == GlobalConstant.MSG_REGISTER_SUCCESS) {
                    activity.showProgress(false);
                    ToastUtil.showShortToast(activity.mContext, "注册成功，请登录");
                    activity.startActivity(new Intent(activity, LoginActivity.class));
                    activity.finish();
                }
                if (flag == GlobalConstant.MSG_REGISTER_NAMEREPEAT) {
                    activity.showProgress(false);
                    ToastUtil.showShortToast(activity.mContext, "注册失败，用户名已存在");

                }
                if (flag == GlobalConstant.MSG_REGISTER_EMAILREPEAT) {
                    activity.showProgress(false);
                    ToastUtil.showShortToast(activity.mContext, "注册失败，邮箱已存在");
                }

            }
        }
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
            mEmailAddress2.setError(getString(R.string.prompt_email_hint));
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

            if (NetUtil.getNetState(mContext) != NetUtil.NetState.NET_NO) {
                showProgress(true);

                User user = new User();
                user.setUsername(username);// 设置用户名
                user.setPassword(password);// 设置密码
                user.setSalt(username); //将用户名设置为salt
                user.setEmailAddress(emailAddress);// 设置Email
                user.setEmailVerified(false);
                user.setCreateTime(new Date());
                user.setUpdateTime(new Date());
                String userJson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create().toJson(user);

                LogUtil.i(TAG, userJson);

                OkHttpUtil.doPostJSON(mContext, GlobalConstant.REGISTER_URL, userJson, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.code() == 200) {
                            MsgData msgData = new Gson().fromJson(response.body().string(), MsgData.class);
                            if (msgData.code == GlobalConstant.MSG_REGISTER_SUCCESS) {
                                //注册成功
                                Message message = Message.obtain();
                                message.what = GlobalConstant.MSG_REGISTER_SUCCESS;
                                mHandler.sendMessage(message);
                            }
                            if (msgData.code == GlobalConstant.MSG_REGISTER_NAMEREPEAT) {
                                //用户名重复
                                Message message = Message.obtain();
                                message.what = GlobalConstant.MSG_REGISTER_NAMEREPEAT;
                                mHandler.sendMessage(message);

                            }
                            if (msgData.code == GlobalConstant.MSG_REGISTER_EMAILREPEAT) {
                                //邮箱重复
                                Message message = Message.obtain();
                                message.what = GlobalConstant.MSG_REGISTER_EMAILREPEAT;
                                mHandler.sendMessage(message);

                            }
                        }
                    }
                });
            }


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
    protected void onDestroy() {
        super.onDestroy();
        mUnBinder.unbind();
    }
}
