package com.notinglife.android.LocationHelper.domain;

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
        return "设备信息：LocationDevice{" +
                "mDeivceId='" + mDeivceId + '\'' +
                ", mMacAddress='" + mMacAddress + '\'' +
                ", mLatitude='" + mLatitude + '\'' +
                ", mLongitude='" + mLongitude + '\'' +
                '}';
    }
}
