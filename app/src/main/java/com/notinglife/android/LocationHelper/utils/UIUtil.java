package com.notinglife.android.LocationHelper.utils;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

import com.notinglife.android.LocationHelper.domain.LocationDevice;
import com.notinglife.android.LocationHelper.domain.User;
import com.notinglife.android.LocationHelper.view.EditDeviceDialog;
import com.notinglife.android.LocationHelper.view.EditUserDialog;

/**
 * ${DESCRIPTION}
 *
 * @author saginardo
 * @version ${VERSION}
 *          date 2017-06-14 22:13
 */

public class UIUtil {

    private static final String TAG = "UIUtil";

    //flag标志位
    private final static int DELETE_BY_ID = 0;
    private final static int DELETE_ALL = 1;
    private final static int UPDATE_DEVICE = 2;
    //private final static int ON_SAVE_DATA = 3; //触发保存数据的标志位
    private final static int UNDO_SAVE = 4;
    //private final static int ON_RECEIVE_LOCATION_DATA = 5;
    private final static int ON_CONFIRM_LOGOUT = 6;
    private final static int ON_EDIT_USER_EMAIL = 7;
    /**
     * 展示统一风格的对话框，其中包含对话框UI中的数据更新和不同对话框UI的替换
     *
     * @param view           传入的视图对象
     * @param title          对话框的标题
     * @param message        对话框的提示信息；其中只有清除所有和删除单个item有提示，其余都设置为GONE
     * @param locationDevice 传入需要显示在对话框上UI的设备信息
     * @param position       LocationDevice在recyclerView中的位置，用来回传给fragment中的recyclerAdapter来更新视图
     * @param flag           标志位，判断弹出对话框的类型
     */
    public static void showEditDialog(View view, Activity activity, final Handler handler, String title, String message, final LocationDevice locationDevice, final int position, final int flag) {

        final EditDeviceDialog mEditDeviceDialog = new EditDeviceDialog(activity, title, message);
        mEditDeviceDialog.setDeviceInfo(locationDevice);
        mEditDeviceDialog.setFlag(flag);

        //自定义窗体参数
        WindowManager.LayoutParams attributes = mEditDeviceDialog.getWindow().getAttributes();
        DisplayMetrics metrics = activity.getResources().getDisplayMetrics();
        attributes.width = (int) (metrics.widthPixels * 0.9);
        attributes.height = (int) (metrics.heightPixels * 0.9);
        attributes.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        attributes.dimAmount = 0.5f;
        mEditDeviceDialog.getWindow().setAttributes(attributes);

        //响应确定键的点击事件
        mEditDeviceDialog.setPositiveOnclickListener(new EditDeviceDialog.onPositiveOnclickListener() {
            @Override
            public void onPositiveClick() {
                Message msg = Message.obtain();
                if (flag == DELETE_ALL) {
                    msg.what = flag;
                    msg.obj = null;
                    msg.arg1 = -1;
                    handler.sendMessage(msg);

                }
                if (flag ==UPDATE_DEVICE) {
                    msg.what = flag;
                    msg.obj =  mEditDeviceDialog.getDeviceInfo();
                    msg.arg1 = position;
                    handler.sendMessage(msg);
                }

                if(flag == DELETE_BY_ID || flag == UNDO_SAVE){
                    msg.what = flag;
                    msg.obj =  locationDevice;
                    msg.arg1 = position;
                    handler.sendMessage(msg);
                }
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

    public static void showConfirmDialog(Activity activity, final Handler handler, String title, String message) {

        final EditDeviceDialog mEditDeviceDialog = new EditDeviceDialog(activity, title, message);

        //自定义窗体参数
        WindowManager.LayoutParams attributes = mEditDeviceDialog.getWindow().getAttributes();
        DisplayMetrics metrics = activity.getResources().getDisplayMetrics();
        attributes.width = (int) (metrics.widthPixels * 0.9);
        attributes.height = (int) (metrics.heightPixels * 0.9);
        attributes.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        attributes.dimAmount = 0.5f;
        mEditDeviceDialog.getWindow().setAttributes(attributes);

        //响应确定键的点击事件
        mEditDeviceDialog.setPositiveOnclickListener(new EditDeviceDialog.onPositiveOnclickListener() {
            @Override
            public void onPositiveClick() {
                Message msg = Message.obtain();
                msg.what = ON_CONFIRM_LOGOUT;
                msg.obj = true;
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

    public static void showEmailEditDialog(final Activity activity, final Handler handler, String title, String message, User user) {

        final EditUserDialog mEditUserDialog = new EditUserDialog(activity, title, message);
        mEditUserDialog.setUserInfo(user);

        //自定义窗体参数
        WindowManager.LayoutParams attributes = mEditUserDialog.getWindow().getAttributes();
        DisplayMetrics metrics = activity.getResources().getDisplayMetrics();
        attributes.width = (int) (metrics.widthPixels * 0.9);
        attributes.height = (int) (metrics.heightPixels * 0.9);
        attributes.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        attributes.dimAmount = 0.5f;
        mEditUserDialog.getWindow().setAttributes(attributes);

        //响应确定键的点击事件
        mEditUserDialog.setPositiveOnclickListener(new EditUserDialog.onPositiveOnclickListener() {
            @Override
            public void onPositiveClick() {
                Message msg = Message.obtain();
                msg.what = ON_EDIT_USER_EMAIL;
                msg.obj = mEditUserDialog.getUserInfo();//重新获取的新的输入框数据
                if(msg.obj==null){
                    ToastUtil.showShortToast(activity,"请检查两次输入邮箱是否一致");
                }else {
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

}
