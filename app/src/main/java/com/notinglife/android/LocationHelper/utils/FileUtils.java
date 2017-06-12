package com.notinglife.android.LocationHelper.utils;

import android.content.Context;

import com.notinglife.android.LocationHelper.domain.LocationDevice;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
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

public class FileUtils {

    public static boolean saveToFile(Context context, LocationDevice locationDevice) {

        StringBuffer sb = new StringBuffer();
        sb.append(locationDevice.mDeivceId).append("#")
                .append(locationDevice.mMacAddress.toUpperCase()).append("#")
                .append(locationDevice.mLatitude.toUpperCase()).append("#")
                .append(locationDevice.mLongitude.toUpperCase()).append("\n");

        try {
            String path = context.getExternalFilesDir("data").getPath();
            if (path != null && path.length() > 0) {
                File file = new File(path, "data.txt");
                FileOutputStream out = new FileOutputStream(file, true);
                //FileOutputStream out = context.openFileOutput("data",Context.MODE_APPEND);
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out));
                bw.write(sb.toString());
                bw.flush();
                out.flush();
                out.close();
                bw.close();
                return true;
            } else {
                LogUtil.i("写入文件失败");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean saveListToFile(Context context, List<LocationDevice> list) {

        String path = context.getExternalFilesDir("data").getPath();
        File file = new File(path, "data.txt");
        if (file.exists()) {
            boolean delete = file.delete();
        }

        for (LocationDevice ld : list) {

            StringBuffer sb = new StringBuffer();
            sb.append(ld.mDeivceId).append("#")
                    .append(ld.mMacAddress.toUpperCase()).append("#")
                    .append(ld.mLatitude.toUpperCase()).append("#")
                    .append(ld.mLongitude.toUpperCase()).append("\n");

            try {
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter( new FileOutputStream(file, true)));
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
            if (path != null && path.length() > 0) {
                File file = new File(path, "data.txt");
                FileInputStream in = new FileInputStream(file);
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                String line = null;
                while ((line = br.readLine()) != null) {
                    String[] words = line.split("#");
                    locationDevice = new LocationDevice();
                    locationDevice.mDeivceId = words[0];
                    locationDevice.mMacAddress = words[1];
                    locationDevice.mLatitude = words[2];
                    locationDevice.mLongitude = words[3];
                    list.add(locationDevice);
                }
                br.close();
                return list;
            }

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
}
