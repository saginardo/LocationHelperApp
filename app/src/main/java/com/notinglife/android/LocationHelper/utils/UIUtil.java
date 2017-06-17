package com.notinglife.android.LocationHelper.utils;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

import com.notinglife.android.LocationHelper.domain.LocationDevice;
import com.notinglife.android.LocationHelper.view.EditDialog;

/**
 * ${DESCRIPTION}
 *
 * @author saginardo
 * @version ${VERSION}
 *          date 2017-06-14 22:13
 */

public class UIUtil {

    //flag标志位
    private final static int DELETE_BY_ID = 0;
    private final static int DELETE_ALL = 1;
    private final static int UPDATE_DEVICE = 2;
    //private final static int ON_SAVE_DATA = 3; //触发保存数据的标志位
    private final static int UNDO_SAVE = 4;
    //private final static int ON_RECEIVE_LOCATION_DATA = 5;

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

        final EditDialog mEditDialog = new EditDialog(activity, title, message);
        mEditDialog.setDeviceInfo(locationDevice);
        mEditDialog.setFlag(flag);

        //自定义窗体参数
        WindowManager.LayoutParams attributes = mEditDialog.getWindow().getAttributes();
        DisplayMetrics metrics = activity.getResources().getDisplayMetrics();
        attributes.width = (int) (metrics.widthPixels * 0.9);
        attributes.height = (int) (metrics.heightPixels * 0.9);
        attributes.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        attributes.dimAmount = 0.5f;
        mEditDialog.getWindow().setAttributes(attributes);

        //响应确定键的点击事件
        mEditDialog.setPositiveOnclickListener(new EditDialog.onPositiveOnclickListener() {
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
                    msg.obj =  mEditDialog.getDeviceInfo();
                    msg.arg1 = position;
                    handler.sendMessage(msg);
                }

                if(flag == DELETE_BY_ID || flag == UNDO_SAVE){
                    msg.what = flag;
                    msg.obj =  locationDevice;
                    msg.arg1 = position;
                    handler.sendMessage(msg);
                }
                mEditDialog.dismiss();
            }
        });

        //响应取消键的点击事件
        mEditDialog.setNegativeOnclickListener(new EditDialog.onNegativeOnclickListener() {
            @Override
            public void onNegativeClick() {
                mEditDialog.dismiss();
            }
        });
        //展示对话框
        mEditDialog.show();
    }
}
