package com.notinglife.android.LocationHelper.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.ProgressBar;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetDataCallback;
import com.avos.avoscloud.ProgressCallback;
import com.avos.avoscloud.SaveCallback;
import com.notinglife.android.LocationHelper.domain.LocationDevice;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * ${DESCRIPTION}
 *
 * @author saginardo
 * @version ${VERSION}
 *          date 2017-06-09 8:48
 */

public class FileUtil {
    private static final String TAG = "FileUtil";

    public static boolean saveListToFile(Context context, List<LocationDevice> list) {
        String path = context.getExternalFilesDir("data").getPath();
        File file = new File(path, "data.txt");
        if (file.exists()) {
            boolean delete = file.delete();
        }
        for (LocationDevice ld : list) {
            StringBuffer sb = new StringBuffer();
            sb.append(ld.mDeviceID).append("#")
                    .append(ld.mMacAddress.toUpperCase()).append("#")
                    .append(ld.mLatitude.toUpperCase()).append("#")
                    .append(ld.mLongitude.toUpperCase()).append("\n");
            try {
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true)));
                bw.write(sb.toString());
                bw.flush();
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    public static List<LocationDevice> readFromFile(Context context) {
        List<LocationDevice> list = new ArrayList<>();
        LocationDevice locationDevice = null;
        try {
            String path = context.getExternalFilesDir("data").getPath();
            File file = new File(path, "data.txt");
            FileInputStream in = new FileInputStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line = null;
            while ((line = br.readLine()) != null) {
                String[] words = line.split("#");
                locationDevice = new LocationDevice();
                locationDevice.mDeviceID = words[0];
                locationDevice.mMacAddress = words[1];
                locationDevice.mLatitude = words[2];
                locationDevice.mLongitude = words[3];
                list.add(locationDevice);
            }
            br.close();
            return list;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void removeFile(Context context) {
        String path = context.getExternalFilesDir("data").getPath();
        File file = new File(path, "data.txt");
        if (file.exists()) {
            file.delete();
        }
    }

    //以下为ManageDataActivity用到的工具
    /**
     * 调用系统接口发送文件
     * @param activity 上下文
     * @param fileName 要发送的文件名
     */
    public static void sendFile(Activity activity, List<LocationDevice> list,String fileName) {
        if (list == null || list.size() == 0) {
            ToastUtil.showShortToast(activity, "当前没有数据，无法发送");
        } else {
            FileUtil.saveListToFile(activity, list);
            Intent intentShareFile = new Intent(Intent.ACTION_SEND);
            String myFilePath = activity.getExternalFilesDir("data").getPath();
            File file = new File(myFilePath, fileName);
            if (file.exists()) {
                intentShareFile.setType("text/plain");
                intentShareFile.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + file));
                intentShareFile.putExtra(Intent.EXTRA_SUBJECT,
                        "Sharing File...");
                intentShareFile.putExtra(Intent.EXTRA_TEXT, "Sharing File...");
                activity.startActivity(Intent.createChooser(intentShareFile, "Share File"));
            } else {
                ToastUtil.showShortToast(activity, "没有数据文件");
            }
        }
    }

    /**
     * 备份到云端
     * @param activity 上下文对象
     * @param fileName 需要备份的文件名
     */
    public static void backupViaCloud(final Activity activity, List<LocationDevice> list, String fileName, final ProgressBar mProgressBar) {
        //存为文件 _File表

        if (list == null || list.size() == 0) {
            ToastUtil.showShortToast(activity, "当前没有数据，无法发送");
        } else {
            FileUtil.saveListToFile(activity, list);
            String myFilePath = activity.getExternalFilesDir("data").getPath();
            mProgressBar.setVisibility(View.VISIBLE);
            try {
                final AVFile file = AVFile.withAbsoluteLocalPath("DeviceData.txt", myFilePath + "/"+fileName);
                file.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(AVException e) {
                        if (e == null) {
                            LogUtil.i(TAG, file.getUrl());//保存成功后，返回一个唯一的 Url 地址
                            mProgressBar.setVisibility(View.GONE);
                            ToastUtil.showShortToast(activity, "云备份文件成功");

                            AVObject locationDevices = new AVObject("LocationDevices");
                            locationDevices.put("owner", AVUser.getCurrentUser());
                            locationDevices.put("dataFile",file);
                            locationDevices.saveInBackground();
                        }
                    }
                }, new ProgressCallback() {
                    @Override
                    public void done(Integer integer) {
                        // 上传进度数据，integer 介于 0 和 100。
                        mProgressBar.setProgress(integer);
                    }
                });

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 下载云端文件
     * @param activity 上下文对象
     * @param fileName 另存为文件名
     */
    public static void downloadFromCloud(final Activity activity, final String fileName, final ProgressBar mProgressBar) {
        //查询当前用对应的 dataFile文件
        AVQuery<AVObject> avQuery = new AVQuery<>("LocationDevices");
        avQuery.whereEqualTo("owner", AVUser.getCurrentUser());
        avQuery.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    mProgressBar.setVisibility(View.VISIBLE);
                    if (list.size() > 0) {
                        AVObject avObject = list.get(list.size() - 1); //获取最新的
                        AVFile dataFile = avObject.getAVFile("dataFile");
                        dataFile.getDataInBackground(new GetDataCallback() {
                            @Override
                            public void done(byte[] bytes, AVException e) {
                                File file = new File(activity.getExternalFilesDir("data").getPath() + "/" + fileName);
                                BufferedOutputStream bo = null;
                                try {
                                    bo = new BufferedOutputStream(new FileOutputStream(file));
                                    bo.write(bytes);
                                    bo.flush();
                                    ToastUtil.showShortToast(activity, "下载成功");
                                } catch (IOException e1) {
                                    e1.printStackTrace();
                                } finally {
                                    mProgressBar.setVisibility(View.GONE);
                                    if (bo != null) {
                                        try {
                                            bo.close();
                                        } catch (IOException e1) {
                                            e1.printStackTrace();
                                        }
                                    }
                                }
                            }
                        }, new ProgressCallback() {
                            @Override
                            public void done(Integer integer) {
                                // 下载进度数据，integer 介于 0 和 100。
                                mProgressBar.setProgress(integer);
                            }
                        });
                    } else {
                        mProgressBar.setVisibility(View.GONE);
                        ToastUtil.showShortToast(activity, "云端没有数据");
                    }
                }else {
                    e.printStackTrace();
                }
            }
        });

    }
    public static void deleteFromCloud(final Activity activity) {
        //查询当前用对应的 dataFile文件
        AVQuery<AVObject> avQuery = new AVQuery<>("LocationDevices");
        avQuery.whereEqualTo("owner", AVUser.getCurrentUser());
        avQuery.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    for (AVObject avObject: list) {
                        AVFile dataFile = avObject.getAVFile("dataFile");
                        dataFile.deleteInBackground();
                        avObject.deleteInBackground();
                    }
                    ToastUtil.showShortToast(activity,"清除完成");
                }
            }
        });

    }

}
