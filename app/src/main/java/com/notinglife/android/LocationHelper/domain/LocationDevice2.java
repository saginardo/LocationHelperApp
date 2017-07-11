package com.notinglife.android.LocationHelper.domain;

import java.io.Serializable;
import java.util.Date;

public class LocationDevice2 implements Serializable {
    public Integer id;
    public String deviceId;
    public Integer userId;
    public String macAddress;
    public String latitude;
    public String longitude;

    public Boolean online;
    public Date createTime;
    public Date updateTime;

    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LocationDevice2 that = (LocationDevice2) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (deviceId != null ? !deviceId.equals(that.deviceId) : that.deviceId != null)
            return false;
        if (userId != null ? !userId.equals(that.userId) : that.userId != null) return false;
        if (macAddress != null ? !macAddress.equals(that.macAddress) : that.macAddress != null)
            return false;
        if (latitude != null ? !latitude.equals(that.latitude) : that.latitude != null)
            return false;
        if (longitude != null ? !longitude.equals(that.longitude) : that.longitude != null)
            return false;
        if (online != null ? !online.equals(that.online) : that.online != null) return false;
        if (createTime != null ? !createTime.equals(that.createTime) : that.createTime != null)
            return false;
        return updateTime != null ? updateTime.equals(that.updateTime) : that.updateTime == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (deviceId != null ? deviceId.hashCode() : 0);
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        result = 31 * result + (macAddress != null ? macAddress.hashCode() : 0);
        result = 31 * result + (latitude != null ? latitude.hashCode() : 0);
        result = 31 * result + (longitude != null ? longitude.hashCode() : 0);
        result = 31 * result + (online != null ? online.hashCode() : 0);
        result = 31 * result + (createTime != null ? createTime.hashCode() : 0);
        result = 31 * result + (updateTime != null ? updateTime.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "LocationDevice2{" +
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