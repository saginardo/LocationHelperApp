package com.notinglife.android.LocationHelper.domain;

import java.util.Objects;

/**
 * ${DESCRIPTION}
 *
 * @author saginardo
 * @version ${VERSION}
 *          date 2017-06-08 19:46
 */

public class LocationDevice {
    public Integer mId;
    public String mDeivceId;
    public String mMacAddress;
    public String mLatitude;
    public String mLongitude;

    @Override
    public String toString() {
        return "LocationDevice{" +
                "mDeivceId='" + mDeivceId + '\'' +
                ", mMacAddress='" + mMacAddress + '\'' +
                ", mLatitude='" + mLatitude + '\'' +
                ", mLongitude='" + mLongitude + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        //return super.equals(obj);
        if(this == obj) return true;
        if(obj==null) return false;
        if(getClass() != obj.getClass()) return false;
        LocationDevice other = (LocationDevice)obj;
        return Objects.equals(mId, other.mId)
                && Objects.equals(mDeivceId,other.mDeivceId)
                && Objects.equals(mMacAddress,other.mMacAddress)
                && Objects.equals(mLatitude,other.mLatitude)
                && Objects.equals(mLongitude,other.mLongitude);
    }

    @Override
    public int hashCode() {
        //return super.hashCode();
        return Objects.hash(mDeivceId,mMacAddress,mLatitude,mLongitude);
    }
}
