package com.notinglife.android.LocationHelper.utils;

import android.app.Activity;
import android.app.Dialog;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.notinglife.android.LocationHelper.R;
import com.notinglife.android.LocationHelper.domain.LocationDevice;
import com.notinglife.android.LocationHelper.domain.User;
import com.notinglife.android.LocationHelper.view.ChoiceDialog;
import com.notinglife.android.LocationHelper.view.ConfirmDialog;
import com.notinglife.android.LocationHelper.view.EditDeviceDialog;
import com.notinglife.android.LocationHelper.view.EditUserDialog;
import com.notinglife.android.LocationHelper.view.SearchDialog;

/**
 * ${DESCRIPTION}
 *
 * @author saginardo
 * @version ${VERSION}
 *          date 2017-06-14 22:13
 */

public class DialogUtil {

    private static final String TAG = "DialogUtil";

    //flag标志位
    private final static int DELETE_BY_ID = 0;
    private final static int DELETE_ALL = 1;
    private final static int UPDATE_DEVICE = 2;
    private final static int ON_SAVE_DATA = 3; //触发保存数据的标志位
    private final static int UNDO_SAVE = 4;
    private final static int ON_RECEIVE_LOCATION_DATA = 5;
    private final static int ON_RECEIVE_SCAN_RESULT = 6;
    private final static int ON_EDIT_DEVICE = 7;

    private final static int ON_CONFIRM_LOGOUT = 21;

    private final static int ON_EDIT_USER_EMAIL = 30;
    private final static int ON_CHANGE_USER_PASSWORD = 31;

    //修改设置选项的标志位
    private final static int LOCATION_MODE = 40;
    private final static int LOCATION_TIME = 41;
    private final static int LOCATION_DEVICE_STATUS = 42;

    //设备维护标志位
    private final static int DEVICE_REPAIR = 50;
    private final static int DEVICE_REPAIR_DOWN = 51;

    //SPUtil用的key
    private final static String LocationMode = "LocationMode";
    private final static String LocationTime = "LocationTime";


    /**
     * 修改设备信息的对话框
     *
     * @param activity       对话框所在的Activity
     * @param title          对话框的标题
     * @param message        对话框的提示信息；其中只有清除所有和删除单个item有提示，其余都设置为GONE
     * @param locationDevice 传入需要显示在对话框上UI的设备信息
     * @param position       LocationDevice在recyclerView中的位置，用来回传给fragment中的recyclerAdapter来更新视图
     * @param flag           标志位，判断弹出对话框的类型
     */
    public static void showDeviceEditDialog(Activity activity, final Handler handler, String title
            , String message, final LocationDevice locationDevice, final int position, final int flag) {

        final EditDeviceDialog mEditDeviceDialog = new EditDeviceDialog(activity, title, message);
        mEditDeviceDialog.setDeviceInfo(locationDevice);
        mEditDeviceDialog.setFlag(flag);

        setDialogParameter(activity, mEditDeviceDialog, Gravity.CENTER, 0.9, 0.9, WindowManager.LayoutParams.FLAG_DIM_BEHIND, 0.5f);
        //响应确定键的点击事件
        mEditDeviceDialog.setPositiveOnclickListener(new EditDeviceDialog.onPositiveOnclickListener() {
            @Override
            public void onPositiveClick() {
                Message msg = Message.obtain();
                msg.what = flag;
                msg.obj = locationDevice;
                msg.arg1 = position;
                handler.sendMessage(msg);
                mEditDeviceDialog.dismiss();
            }
        });

        //响应取消键的点击事件
        mEditDeviceDialog.setNegativeOnclickListener(new EditDeviceDialog.onNegativeOnclickListener() {
            @Override
            public void onNegativeClick() {
                mEditDeviceDialog.dismiss();
            }
        });
        //展示对话框
        mEditDeviceDialog.show();
    }


    /**
     * 修改用户信息的对话框
     *
     * @param activity 对话框所在的activity
     * @param handler  用于更新的handler
     * @param title    设置对话框标题
     * @param message  设置对话框显示信息
     * @param user     待修改的用户类
     * @param flag     修改用户信息的标志位
     */
    public static void showUserEditDialog(final Activity activity, final Handler handler, String title, String message, final User user, final int flag) {

        final EditUserDialog mEditUserDialog = new EditUserDialog(activity, title, message);
        mEditUserDialog.setUserInfo(user);
        mEditUserDialog.setFlag(flag);

        setDialogParameter(activity, mEditUserDialog, Gravity.CENTER, 0.9, 0.9, WindowManager.LayoutParams.FLAG_DIM_BEHIND, 0.5f);

        //响应确定键的点击事件
        mEditUserDialog.setPositiveOnclickListener(new EditUserDialog.onPositiveOnclickListener() {
            @Override
            public void onPositiveClick() {
                Message msg = Message.obtain();
                msg.what = flag;

                if (flag == ON_EDIT_USER_EMAIL) {
                    msg.obj = mEditUserDialog.getUserInfo();//重新获取的新的输入框数据
                    if (msg.obj == null) {
                        ToastUtil.showShortToast(activity, "请检查两次输入邮箱是否一致");
                    } else {
                        //两次输入邮箱一致，则发送msg
                        handler.sendMessage(msg);
                        mEditUserDialog.dismiss();
                    }
                }
                if (flag == ON_CHANGE_USER_PASSWORD) {
                    msg.obj = user;
                    handler.sendMessage(msg);
                    mEditUserDialog.dismiss();
                }

            }
        });

        //响应取消键的点击事件
        mEditUserDialog.setNegativeOnclickListener(new EditUserDialog.onNegativeOnclickListener() {
            @Override
            public void onNegativeClick() {
                mEditUserDialog.dismiss();
            }
        });
        //展示对话框
        mEditUserDialog.show();
    }


    /**
     * 确认对话框
     *
     * @param activity 对话框所在的activity
     * @param handler  用于更新的handler
     * @param title    设置对话框标题
     * @param message  设置对话框显示信息
     * @param flag     对话框标志位
     */
    public static void showConfirmDialog(Activity activity, final Handler handler, String title, String message, final int flag) {
        final ConfirmDialog mConfirmDialog = new ConfirmDialog(activity, title, message);
        //自定义窗体参数
        setDialogParameter(activity, mConfirmDialog, Gravity.CENTER, 0.9, 0.9, WindowManager.LayoutParams.FLAG_DIM_BEHIND, 0.5f);

        //响应确定键的点击事件
        mConfirmDialog.setPositiveOnclickListener(new ConfirmDialog.onPositiveOnclickListener() {
            @Override
            public void onPositiveClick() {
                Message msg = Message.obtain();
                msg.what = flag;
                msg.obj = true;
                handler.sendMessage(msg);
                mConfirmDialog.dismiss();
            }
        });

        //响应取消键的点击事件
        mConfirmDialog.setNegativeOnclickListener(new ConfirmDialog.onNegativeOnclickListener() {
            @Override
            public void onNegativeClick() {
                mConfirmDialog.dismiss();
            }
        });
        //展示对话框
        mConfirmDialog.show();
    }

    /**
     * 搜索对话框
     *
     * @param activity
     */
    public static void showSearchDialog(Activity activity) {

        final SearchDialog mSearchDialog = new SearchDialog(activity, R.layout.dialog_search_device);
        setDialogParameter(activity, mSearchDialog, Gravity.TOP, 0.95, 0.6, WindowManager.LayoutParams.FLAG_DIM_BEHIND, 0.5f);

        //设置回退按键的功能 让搜索框不显示
        mSearchDialog.setBackspaceOnclickListener(new SearchDialog.onBackspaceOnclickListener() {
            @Override
            public void onBackspaceClick() {
                mSearchDialog.dismiss();
            }
        });

        //展示对话框
        mSearchDialog.show();
    }

    /**
     * 选项对话框
     *
     * @param activity 对话框所在的activity
     * @param handler  用于更新的handler
     * @param title    设置对话框标题
     * @param flag     对话标志位
     * @param strings  待选项，可选参数，最多设置了4个备选按钮
     */
    public static void showChoiceDialog(final Activity activity, final Handler handler, String title, String defaultCheck,final int flag, String... strings) {

        final ChoiceDialog mChoiceDialog = new ChoiceDialog(activity, title, strings);
        mChoiceDialog.setFlag(flag);
        mChoiceDialog.setDefaultCheck(defaultCheck);
        setDialogParameter(activity, mChoiceDialog, Gravity.CENTER, 0.9, 0.9, WindowManager.LayoutParams.FLAG_DIM_BEHIND, 0.5f);

        //响应确定键的点击事件
        mChoiceDialog.setPositiveOnclickListener(new ChoiceDialog.onPositiveOnclickListener() {
            @Override
            public void onPositiveClick() {

                String checkButton = mChoiceDialog.getCheckButtonName();
                //LogUtil.i(TAG,"选择的按钮是："+checkButton.getText());
                Message message = Message.obtain();
                message.what = flag;
                message.obj = checkButton;
                //确认选项后，才写入SP中
                switch (flag) {
                    case LOCATION_MODE:
                        SPUtil.setString(activity.getApplicationContext(), LocationMode, checkButton);
                        break;
                    case LOCATION_TIME:
                        SPUtil.setString(activity.getApplicationContext(), LocationTime, checkButton);
                        break;
                }
                handler.sendMessage(message);

                mChoiceDialog.dismiss();
            }
        });

        //响应取消键的点击事件
        mChoiceDialog.setNegativeOnclickListener(new ChoiceDialog.onNegativeOnclickListener() {
            @Override
            public void onNegativeClick() {
                mChoiceDialog.dismiss();
            }
        });
        //展示对话框
        mChoiceDialog.show();
    }

    /**
     * @param activity    dialog所在的Activity
     * @param t           泛型，继承自Dialog的自定义Dialog
     * @param Gravity     dialog的布局方式(TOP,CENTER,...)
     * @param widthRadio  dialog占Activity的宽度比，小于等于1.0
     * @param heightRadio dialog占Activity的宽度比，小于等于1.0
     * @param flag        diglog的背景样式(背景模糊等)
     * @param dimAmount   dialog的背景模糊度度
     */
    private static <T extends Dialog> void setDialogParameter(Activity activity, T t, int Gravity
            , double widthRadio, double heightRadio, int flag, float dimAmount) {
        //自定义窗体参数
        Window dialogWindow = t.getWindow();
        dialogWindow.setGravity(Gravity);
        WindowManager.LayoutParams attributes = dialogWindow.getAttributes();
        DisplayMetrics metrics = activity.getResources().getDisplayMetrics();
        attributes.width = (int) (metrics.widthPixels * widthRadio);
        attributes.height = (int) (metrics.heightPixels * heightRadio);
        attributes.flags = flag;
        attributes.dimAmount = dimAmount;
        t.getWindow().setAttributes(attributes);
    }
}
