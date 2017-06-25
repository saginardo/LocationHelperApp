package com.notinglife.android.LocationHelper.domain;

import java.util.Date;

/**
 * ${DESCRIPTION}
 *
 * @author saginardo
 * @version ${VERSION}
 *          date 2017-06-21 16:33
 */

public class User {
    public String mObjectId;
    public String mUsername;
    public String mPassword;// 这个字段应该不用，用户密码不应该存储在本地使用
    public String mEmail;
    public Date mCreateDate;
    public Date mUpdateDate;

    @Override
    public String toString() {
        return "User{" +
                "mUsername='" + mUsername + '\'' +
                ", mEmail='" + mEmail + '\'' +
                '}';
    }
}
