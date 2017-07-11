package com.notinglife.android.LocationHelper.domain;

import java.util.Date;
import java.util.List;

/**
 * 服务器交互的消息类
 *
 * @author saginardo
 * @version ${VERSION}
 *          date 2017-07-09 9:13
 */

public class MsgData {

    /**
     * code : 110
     * message : 成功查询所有设备
     * devices : [{"id":3,"deviceId":"00012","userId":2,"macAddress":"00:22:25:21:32:58","latitude":"29.535883","longitude":"106.610361","online":true,"createTime":1498871748000,"updateTime":1498871748000},{"id":4,"deviceId":"00022","userId":1,"macAddress":"14:22:55:11:32:58","latitude":"29.539883","longitude":"106.615361","online":true,"createTime":1498130152000,"updateTime":1499436345000},{"id":6,"deviceId":"00013","userId":1,"macAddress":"16:11:32:17:12:18","latitude":"29.53681","longitude":"106.611995","online":false,"createTime":1497168954000,"updateTime":1497168954000},{"id":9,"deviceId":"00029","userId":1,"macAddress":"10:13:11:71:AC:B9","latitude":"29.531745","longitude":"106.613925","online":true,"createTime":1496898956000,"updateTime":1496898956000},{"id":10,"deviceId":"00031","userId":2,"macAddress":"30:23:41:11:BF:E9","latitude":"29.534883","longitude":"106.618661","online":false,"createTime":1496888157000,"updateTime":1496888157000},{"id":11,"deviceId":"00055","userId":1,"macAddress":"04:16:35:33:32:58","latitude":"29.533876","longitude":"106.614964","online":false,"createTime":1496736958000,"updateTime":1496736958000},{"id":12,"deviceId":"00058","userId":2,"macAddress":"25:FF:AC:EB:01:22","latitude":"29.531883","longitude":"106.611661","online":false,"createTime":1496805359000,"updateTime":1496805359000},{"id":13,"deviceId":"00062","userId":1,"macAddress":"14:16:55:AA:32:18","latitude":"29.537883","longitude":"106.614161","online":false,"createTime":1496650739000,"updateTime":1496650739000},{"id":14,"deviceId":"00065","userId":1,"macAddress":"54:22:55:11:32:58","latitude":"29.539183","longitude":"106.615261","online":false,"createTime":1496737140000,"updateTime":1496737140000},{"id":17,"deviceId":"00001","userId":2,"macAddress":"11:00:00:00:00:01","latitude":"29.536187","longitude":"106.491872","online":false,"createTime":1496650623000,"updateTime":1499502806000},{"id":18,"deviceId":"00033","userId":2,"macAddress":"01:11:32:17:12:BE","latitude":"29.536871","longitude":"106.681932","online":false,"createTime":1496650564000,"updateTime":1496650564000}]
     */

    public int code;
    public String message;
    public List<LocationDevice2> devices;


    public static class DevicesBean {
        /**
         * id : 3
         * deviceId : 00012
         * userId : 2
         * macAddress : 00:22:25:21:32:58
         * latitude : 29.535883
         * longitude : 106.610361
         * online : true
         * createTime : 1498871748000
         * updateTime : 1498871748000
         */

        public int id;
        public String deviceId;
        public int userId;
        public String macAddress;
        public String latitude;
        public String longitude;
        public boolean online;
        public Date createTime;
        public Date updateTime;
    }

    @Override
    public String toString() {
        return "MsgData{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", devices=" + devices +
                '}';
    }
}
