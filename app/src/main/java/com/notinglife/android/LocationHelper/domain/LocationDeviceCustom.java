package com.notinglife.android.LocationHelper.domain;

import java.io.Serializable;

/**
 * ${DESCRIPTION}
 *
 * @author saginardo
 * @version ${VERSION}
 *          date 2017-07-08 21:59
 */

public class LocationDeviceCustom implements Serializable {
    private static final long serialVersionUID = 9062776421895984116L;
    public int id;
    public String deviceId;
    public int userId;
    public String macAddress;
    public String latitude;
    public String longitude;
    public boolean online;
    public long createTime;
    public long updateTime;

    @Override
    public String toString() {
        return "LocationDeviceCustom{" +
                "id=" + id +
                ", deviceId='" + deviceId + '\'' +
                ", userId=" + userId +
                ", macAddress='" + macAddress + '\'' +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                ", online=" + online +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
