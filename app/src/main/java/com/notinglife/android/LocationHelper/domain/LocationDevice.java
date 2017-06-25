package com.notinglife.android.LocationHelper.domain;

import java.io.Serializable;
import java.util.Objects;

/**
 * ${DESCRIPTION}
 *
 * @author saginardo
 * @version ${VERSION}
 *          date 2017-06-08 19:46
 */

public class LocationDevice implements Serializable {

    private static final long serialVersionUID = -2304670905038616692L;

    public Integer mID;
    public String mDeviceID;
    public String mMacAddress;
    public String mLatitude;
    public String mLongitude;
    public String mRadius;
    public Integer mLocMode;

    @Override
    public String toString() {
        return "LocationDevice{" +
                "mDeiviceId='" + mDeviceID + '\'' +
                ", mMacAddress='" + mMacAddress + '\'' +
                ", mLatitude='" + mLatitude + '\'' +
                ", mLongitude='" + mLongitude + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(obj==null) return false;
        if(getClass() != obj.getClass()) return false;
        LocationDevice other = (LocationDevice)obj;
        return  Objects.equals(mDeviceID,other.mDeviceID)
                && Objects.equals(mMacAddress,other.mMacAddress)
                && Objects.equals(mLatitude,other.mLatitude)
                && Objects.equals(mLongitude,other.mLongitude);
                //比较对象相同，不需要其精度信息
                //&& Objects.equals(mRadius,other.mRadius);
    }

    @Override
    public int hashCode() {
        //return super.hashCode();
        return Objects.hash(mDeviceID,mMacAddress,mLatitude,mLongitude);
    }
}
