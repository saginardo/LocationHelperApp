package com.notinglife.android.LocationHelper.domain;

import java.util.List;

/**
 * ${DESCRIPTION}
 *
 * @author saginardo
 * @version ${VERSION}
 *          date 2017-07-01 14:58
 */

public class RepairDevices {


    /**
     * data : {"alert":"收到设备故障通知","title":"定位助手","devices":["00002","00012","00023","00058"]}
     */

    public DataBean data;

    public static class DataBean {
        /**
         * alert : 收到设备故障通知
         * title : 定位助手
         * devices : ["00002","00012","00023","00058"]
         */

        public String alert;
        public String title;
        public List<String> devices;
    }
}
