package com.notinglife.android.LocationHelper.receiver;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.google.gson.Gson;
import com.notinglife.android.LocationHelper.R;
import com.notinglife.android.LocationHelper.activity.RepairDevicesActivity;
import com.notinglife.android.LocationHelper.dao.DeviceRawDao;
import com.notinglife.android.LocationHelper.domain.RepairDevices;
import com.notinglife.android.LocationHelper.utils.LogUtil;

import java.util.List;


/**
 * 接收到通知，并修改本地数据库中对应设备的状态
 *
 * @author saginardo
 * @version ${VERSION}
 *          date 2017-07-01 9:58
 */

public class RepairDeviceReceiver extends BroadcastReceiver {
    private static final String TAG = "RepairDeviceReceiver";
    private DeviceRawDao mDao;
    private Context mContext;
    private String alert;
    private String title;


    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        //获取消息内容
        String action = intent.getAction();
        String channel = intent.getExtras().getString("com.avos.avoscloud.Channel");
        String IntentData = intent.getExtras().getString("com.avos.avoscloud.Data");
        LogUtil.i(TAG, "收到广播 " + action + "，频道是 " + channel);

        Gson gson = new Gson();
        RepairDevices repairDevices = gson.fromJson(IntentData, RepairDevices.class);
        alert = repairDevices.data.alert;
        title = repairDevices.data.title;
        List<String> devicesID = repairDevices.data.devices;

        //只有收到 "Repair"的广播才做出修改
        if(channel!=null && channel.equals("Repair")){
            updateDatabase(context, devicesID);
            sendNotification();
        }
    }

    //收到消息后，修改数据库
    private void updateDatabase(Context context, List<String> devicesID) {
        mDao = new DeviceRawDao(context);
        int i = mDao.updateDevicesStatus(devicesID,"OffLine");
        LogUtil.i(TAG, "修改了" + i + "个设备状态");
    }
    //收到消息后，发送通知
    private void sendNotification() {
        //获取NotificationManager实例
        NotificationManager notifyManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        //获取PendingIntent，用于设置打开的action
        Intent mainIntent = new Intent(mContext, RepairDevicesActivity.class);
        PendingIntent mainPendingIntent = PendingIntent.getActivity(mContext, 0, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        //创建 Notification.Builder 对象
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext)
                .setSmallIcon(R.drawable.launcher)
                //点击通知后自动清除
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentText(alert)
                .setContentIntent(mainPendingIntent);
        //发送通知
        notifyManager.notify(1, builder.build());
    }

    //RESTAPI
    //需要设置 频道为 Repair才会接受到该消息
    /*
    {
    "data":{
        "alert":      "收到设备故障通知",
        "title":      "定位助手",
        "action":     "com.notinglife.android.LocationHelper.REPAIR_DEVICE",
        "devices": [
            "00002",
            "00012",
            "00023",
            "00058"]
        }
    }
     */
}
