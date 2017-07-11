package com.notinglife.android.LocationHelper.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.notinglife.android.LocationHelper.R;
import com.notinglife.android.LocationHelper.domain.User;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.notinglife.android.LocationHelper.R.id.tv_user_email_info_repeat;

/**
 * ${DESCRIPTION}
 *
 * @author saginardo
 * @version ${VERSION}
 *          date 2017-06-12 14:39
 */

public class EditUserDialog extends Dialog {


    @BindView(R.id.tv_edit_user_title)
    TextView mTvEditUserTitle;
    @BindView(R.id.tv_message)
    TextView mTvMessage;
    @BindView(R.id.tv_username)
    TextView mTvUsername;
    @BindView(R.id.tv_user_name_info)
    EditText mTvUserNameInfo;
    @BindView(R.id.rl_user_info)
    RelativeLayout mRlUserInfo;
    @BindView(R.id.tv_user_email)
    TextView mTvUserEmail;
    @BindView(R.id.tv_user_email_info)
    EditText mTvUserEmailInfo;
    @BindView(R.id.rl_user_email)
    RelativeLayout mRlUserEmail;
    @BindView(R.id.bt_accept)
    Button mBtAccept;
    @BindView(R.id.bt_cancel)
    Button mBtCancel;
    @BindView(R.id.tv_user_email_repeat)
    TextView mTvUserEmailRepeat;
    @BindView(tv_user_email_info_repeat)
    EditText mTvUserEmailInfoRepeat;
    @BindView(R.id.rl_user_email_repeat)
    RelativeLayout mRlUserEmailRepeat;

    private onPositiveOnclickListener positiveOnclickListener;//确定按钮被点击了的监听器
    private onNegativeOnclickListener negativeOnclickListener;//取消按钮被点击了的监听器

    private String dialogTitle;
    private String message;
    private Unbinder mUnBinder;
    //用于支持显示用户修改页面的对话框
    private User mUser;

    //showdialog标志位
    private int mFlag;
    private final static int DELETE_BY_ID = 0;
    private final static int DELETE_ALL = 1;
    private final static int UPDATE_DEVICE = 2;
    private final static int ON_SAVE_DATA = 3; //触发保存数据的标志位
    private final static int UNDO_SAVE = 4;
    private final static int ON_RECEIVE_LOCATION_DATA = 5;


    private final static int ON_EDIT_USER_EMAIL = 30;
    private final static int ON_CHANGE_USER_PASSWORD = 31;
    private final static int ON_RESET_PASSWORD = 32;


    private static final String TAG = "EditUserDialog";

    public EditUserDialog(Context context, String title, String msg) {
        super(context, R.style.MyDialog);
        dialogTitle = title;
        message = msg;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_edit_user);
        //按空白处不能取消动画
        setCanceledOnTouchOutside(false);
        mUnBinder = ButterKnife.bind(this);

        //初始化界面控件
        initView();
        //初始化界面数据
        initData();
        //初始化界面控件的事件
        initEvent();

    }

    /**
     * 初始化界面控件的显示数据回显
     */
    private void initData() {

    }


    private void initView() {

        if(mFlag==ON_RESET_PASSWORD){
            mTvEditUserTitle.setText(dialogTitle);
            mTvUserNameInfo.setEnabled(true);

            mTvUserEmail.setVisibility(View.GONE);
            mTvUserEmailRepeat.setVisibility(View.GONE);

            mTvUserEmailInfo.setVisibility(View.GONE);
            mTvUserEmailInfoRepeat.setVisibility(View.GONE);
        }

        if(mFlag==ON_EDIT_USER_EMAIL){
            mTvEditUserTitle.setText(dialogTitle);

            mTvUserNameInfo.setEnabled(false);//不让修改用户名
            if(mUser!=null){ //初始回显页面
             /*   mTvUserNameInfo.setText(mUser.mUsername);
                mTvUserEmailInfo.setHint(mUser.mEmail);
                mTvUserEmailInfoRepeat.setHint(mUser.mEmail);*/
            }
        }
        if(mFlag==ON_CHANGE_USER_PASSWORD){
            mTvEditUserTitle.setText(dialogTitle);
            mTvMessage.setVisibility(View.VISIBLE);
            mTvMessage.setText(message);

            mTvUsername.setVisibility(View.GONE);
            mTvUserEmail.setVisibility(View.GONE);
            mTvUserEmailRepeat.setVisibility(View.GONE);

            mTvUserNameInfo.setVisibility(View.GONE);
            mTvUserEmailInfo.setVisibility(View.GONE);
            mTvUserEmailInfoRepeat.setVisibility(View.GONE);

        }

    }

    /**
     * 初始化界面的确定和取消监听器
     */
    private void initEvent() {
        //设置确定按钮被点击后，向外界提供监听
        mBtAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (positiveOnclickListener != null) {
                    positiveOnclickListener.onPositiveClick();
                }
            }
        });
        //设置取消按钮被点击后，向外界提供监听
        mBtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (negativeOnclickListener != null) {
                    negativeOnclickListener.onNegativeClick();
                }
            }
        });
    }

    public void setUserInfo(User user) {
        mUser = new User();
        mUser = user;
    }

    public User getUserInfo() {
        /*mUser.mUsername = mTvUserNameInfo.getText().toString();
        String s1 = mTvUserEmailInfo.getText().toString();
        String s2 = mTvUserEmailInfoRepeat.getText().toString();
        if(!s2.equals(s1)){
            return null;
        }else {
            mUser.mEmail = s2;
        }*/
        return mUser;
    }

    public String getUserName(){
        return mTvUserNameInfo.getText().toString().trim();
    }


    /**
     * 设置确定按钮的显示内容和监听
     *
     * @param onYesOnclickListener
     */
    public void setPositiveOnclickListener(onPositiveOnclickListener onYesOnclickListener) {
        this.positiveOnclickListener = onYesOnclickListener;
    }

    /**
     * 设置取消按钮的显示内容和监听
     *
     * @param onNoOnclickListener
     */
    public void setNegativeOnclickListener(onNegativeOnclickListener onNoOnclickListener) {
        this.negativeOnclickListener = onNoOnclickListener;
    }

    public void setFlag(int flag) {
        mFlag = flag;
    }


    /**
     * 设置确定按钮和取消被点击的接口
     */
    public interface onPositiveOnclickListener {
        void onPositiveClick();
    }

    public interface onNegativeOnclickListener {
        void onNegativeClick();
    }
}
