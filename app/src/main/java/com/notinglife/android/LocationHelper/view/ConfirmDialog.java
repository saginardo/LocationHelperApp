package com.notinglife.android.LocationHelper.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.notinglife.android.LocationHelper.R;
import com.notinglife.android.LocationHelper.domain.LocationDevice;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * ${DESCRIPTION}
 *
 * @author saginardo
 * @version ${VERSION}
 *          date 2017-06-12 14:39
 */

public class ConfirmDialog extends Dialog {

    @BindView(R.id.tv_confirm_title)
    TextView mDialogTitle;
    @BindView(R.id.tv_confirm_message)
    TextView mMessage;
    @BindView(R.id.bt_accept)
    Button mPositiveButton;//确定按钮
    @BindView(R.id.bt_cancel)
    Button mNegativeButton;//取消按钮

    private onPositiveOnclickListener positiveOnclickListener;//确定按钮被点击了的监听器
    private onNegativeOnclickListener negativeOnclickListener;//取消按钮被点击了的监听器

    private String dialogTitle;
    private String message;
    private LocationDevice tmpDevice;
    private Unbinder mUnBinder;
    private int mFlag;

    public ConfirmDialog(Context context, String title, String msg) {
        super(context, R.style.MyDialog);
        dialogTitle = title;
        message = msg;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_confirm);
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

        if (dialogTitle != null && !dialogTitle.equals("")) {
            mDialogTitle.setText(dialogTitle);
        }
        if (message != null && !message.equals("")) {
            mMessage.setVisibility(View.VISIBLE);
            mMessage.setText(message);
        }
    }

    private void initView() {

    }

    /**
     * 初始化界面的确定和取消监听器
     */
    private void initEvent() {
        //设置确定按钮被点击后，向外界提供监听
        mPositiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (positiveOnclickListener != null) {
                    positiveOnclickListener.onPositiveClick();
                }
            }
        });
        //设置取消按钮被点击后，向外界提供监听
        mNegativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (negativeOnclickListener != null) {
                    negativeOnclickListener.onNegativeClick();
                }
            }
        });
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
