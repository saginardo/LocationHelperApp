package com.notinglife.android.LocationHelper.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.notinglife.android.LocationHelper.R;
import com.notinglife.android.LocationHelper.utils.SPUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * ${DESCRIPTION}
 *
 * @author saginardo
 * @version ${VERSION}
 *          date 2017-06-27 21:00
 */

public class ChoiceDialog extends Dialog {

    @BindView(R.id.tv_title)
    TextView mTvTitle;

    @BindView(R.id.radioButton1)
    RadioButton mRadioButton1;
    @BindView(R.id.radioButton2)
    RadioButton mRadioButton2;
    @BindView(R.id.radioButton3)
    RadioButton mRadioButton3;
    @BindView(R.id.radioButton4)
    RadioButton mRadioButton4;
    @BindView(R.id.bt_accept)
    Button mBtAccept;
    @BindView(R.id.bt_cancel)
    Button mBtCancel;


    private onPositiveOnclickListener positiveOnclickListener;//确定按钮被点击了的监听器
    private onNegativeOnclickListener negativeOnclickListener;//取消按钮被点击了的监听器

    private String dialogTitle;


    private static final String TAG = "ChoiceDialog";
    //修改设置的广播的标志位
    private final static int LOCATION_MODE = 40;
    private final static int LOCATION_TIME = 41;

    //SPUtil用的key
    private final static String LocationMode = "LocationMode";
    private final static String LocationTime = "LocationTime";

    private int mFlag;
    private String[] mChoices;
    private List<RadioButton> mRadioButtonList;
    private Unbinder mUnBinder;
    private String mCheckButtonName;


    public ChoiceDialog(Context context, String title, String... strings) {
        super(context, R.style.MyDialog);
        dialogTitle = title;
        mChoices = strings;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choice_dialog);

        //按空白处不能取消动画
        setCanceledOnTouchOutside(false);
        mUnBinder = ButterKnife.bind(this);

        //初始化界面数据
        initData();
        //初始化界面控件
        initView();
        //初始化界面控件的事件
        initEvent();

    }

    /**
     * 初始化界面控件的显示数据回显,需要先于initView执行
     */
    private void initData() {
        mRadioButtonList = new ArrayList<>();
        mRadioButtonList.add(mRadioButton1);
        mRadioButtonList.add(mRadioButton2);
        mRadioButtonList.add(mRadioButton3);
        mRadioButtonList.add(mRadioButton4);

    }

    private void initView() {
        if (!TextUtils.isEmpty(dialogTitle)) {
            mTvTitle.setText(dialogTitle);
        }
        if (mFlag == LOCATION_MODE) {
            //标志位为 LOCATION_MODE,说明是选择定位模式
            for (int i = 0; i < mChoices.length; i++) {
                //设置对应button的显示
                mRadioButtonList.get(i).setText(mChoices[i]);
                mRadioButtonList.get(i).setVisibility(View.VISIBLE);
            }
        }

        if (mFlag == LOCATION_TIME) {
            //标志位为 Locationtime，说明是选择定位时长
            //标志位为 LOCATION_MODE,说明是选择定位模式
            for (int i = 0; i < mChoices.length; i++) {
                //设置对应button的显示
                mRadioButtonList.get(i).setText(mChoices[i]);
                mRadioButtonList.get(i).setVisibility(View.VISIBLE);
            }
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


        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radio_group);
        if (mFlag == LOCATION_MODE) {
            switch (SPUtil.getString(getContext(), LocationMode, "混合模式")) {
                case "混合模式":
                    radioGroup.check(R.id.radioButton1);
                    break;
                case "仅使用GPS":
                    radioGroup.check(R.id.radioButton2);
                    break;
                case "仅使用网络":
                    radioGroup.check(R.id.radioButton3);
                    break;
                default:
                    break;
            }
        }
        if (mFlag == LOCATION_TIME) {
            switch (SPUtil.getString(getContext(), LocationTime,"60秒")) {
                case "60秒":
                    radioGroup.check(R.id.radioButton1);
                    break;
                case "120秒":
                    radioGroup.check(R.id.radioButton2);
                    break;
                case "300秒":
                    radioGroup.check(R.id.radioButton3);
                    break;
                case "无限制":
                    radioGroup.check(R.id.radioButton4);
                    break;
                default:
                    break;
            }
        }

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                RadioButton radioButton = (RadioButton) findViewById(checkedId);
                setCheckButtonName(radioButton.getText().toString());
            }
        });
    }

    /**
     * 设置确定按钮的显示内容和监听
     *
     * @param positiveOnclickListener
     */
    public void setPositiveOnclickListener(onPositiveOnclickListener positiveOnclickListener) {
        this.positiveOnclickListener = positiveOnclickListener;
    }

    /**
     * 设置取消按钮的显示内容和监听
     *
     * @param negativeOnclickListener
     */
    public void setNegativeOnclickListener(onNegativeOnclickListener negativeOnclickListener) {
        this.negativeOnclickListener = negativeOnclickListener;
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


    public void setCheckButtonName(String checkButtonName) {
        mCheckButtonName = checkButtonName;
    }

    public String getCheckButtonName() {
        //当用户什么都不选择，直接按确定，此时 mCheckButtonName为空
        if(mCheckButtonName==null){
            if(mFlag==LOCATION_TIME){
               return SPUtil.getString(getContext(), LocationTime,"60秒");
            }
            if(mFlag==LOCATION_MODE){
                return SPUtil.getString(getContext(), LocationMode, "混合模式");
            }
        }
        return mCheckButtonName;
    }


}
