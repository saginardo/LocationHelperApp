package com.notinglife.android.LocationHelper.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.notinglife.android.LocationHelper.domain.LocationDevice;
import com.notinglife.android.LocationHelper.utils.LogUtil;
import com.notinglife.android.LocationHelper.utils.MySqliteOpenHelper;

/**
 * ${DESCRIPTION}
 *
 * @author saginardo
 * @version ${VERSION}
 *          date 2017-06-11 8:17
 */

public class DeviceSQLDao {

    private MySqliteOpenHelper mySqliteOpenHelper;

    public DeviceSQLDao(Context context) {
        //创建一个帮助类对象
        mySqliteOpenHelper = new MySqliteOpenHelper(context);
    }

    public void add(LocationDevice bean) {

        //执行sql语句需要sqliteDatabase对象
        //调用getReadableDatabase方法,来初始化数据库的创建
        SQLiteDatabase db = mySqliteOpenHelper.getReadableDatabase();
        //sql:sql语句，  bindArgs：sql语句中占位符的值
        //insert into devices values('00002','71:22:63:44:55:06','29.001200','106.055000');
        db.execSQL("insert into devices(deviceId,macAddress,latitude,longitude) values(?,?,?,?);",
                new Object[]{bean.mDeivceId, bean.mMacAddress, bean.mLatitude, bean.mLongitude});
        //关闭数据库对象
        db.close();
    }

    public void delete(String DeviceId) {

        //执行sql语句需要sqliteDatabase对象
        //调用getReadableDatabase方法,来初始化数据库的创建
        SQLiteDatabase db = mySqliteOpenHelper.getReadableDatabase();
        //sql:sql语句，  bindArgs：sql语句中占位符的值
        db.execSQL("delete from devices where deviceId=?;", new Object[]{DeviceId});
        //关闭数据库对象
        db.close();

    }

    public void update(LocationDevice bean) {

        //执行sql语句需要sqliteDatabase对象
        //调用getReadableDatabase方法,来初始化数据库的创建
        SQLiteDatabase db = mySqliteOpenHelper.getReadableDatabase();
        //sql:sql语句，  bindArgs：sql语句中占位符的值
        //update devices set macAddress='11:11:11:33:44:55', latitude='30.111111', longitude='111.222222' where _id='00003'
        db.execSQL("update devices set deviceId=?,macAddress=?, latitude=?, longitude=? where deviceId=?;",
                new Object[]{ bean.mDeivceId, bean.mMacAddress, bean.mLatitude, bean.mLongitude, bean.mDeivceId});
        //关闭数据库对象
        db.close();

    }

    public LocationDevice queryById(String DeviceId) {

        LocationDevice bean = new LocationDevice();

        //执行sql语句需要sqliteDatabase对象
        //调用getReadableDatabase方法,来初始化数据库的创建
        SQLiteDatabase db = mySqliteOpenHelper.getReadableDatabase();
        //sql:sql语句，  selectionArgs:查询条件占位符的值,返回一个cursor对象
        Cursor cursor = db.rawQuery("select deviceId,macAddress,latitude,longitude from devices where deviceId = ?;", new String[]{DeviceId});
        //解析Cursor中的数据
        if (cursor != null && cursor.getCount() > 0) {//判断cursor中是否存在数据

            //循环遍历结果集，获取每一行的内容
            while (cursor.moveToNext()) {//条件，游标能否定位到下一行
                //获取数据
                bean.mDeivceId = cursor.getString(0);
                bean.mMacAddress = cursor.getString(1);
                bean.mLatitude = cursor.getString(2);
                bean.mLongitude = cursor.getString(2);

                LogUtil.i("查询到的设备："+ bean.mDeivceId+" "+bean.mMacAddress+""+bean.mLatitude+" "+ bean.mLongitude);
            }
            cursor.close();//关闭结果集

        }
        //关闭数据库对象
        db.close();
        return bean;
    }

    public LocationDevice queryAll() {

        LocationDevice bean = new LocationDevice();

        //执行sql语句需要sqliteDatabase对象
        //调用getReadableDatabase方法,来初始化数据库的创建
        SQLiteDatabase db = mySqliteOpenHelper.getReadableDatabase();
        //sql:sql语句，  selectionArgs:查询条件占位符的值,返回一个cursor对象
        Cursor cursor = db.rawQuery("select deviceId,macAddress,latitude,longitude from devices ;", new String[]{});
        //解析Cursor中的数据
        if (cursor != null && cursor.getCount() > 0) {//判断cursor中是否存在数据

            //循环遍历结果集，获取每一行的内容
            while (cursor.moveToNext()) {//条件，游标能否定位到下一行
                //获取数据
                bean.mDeivceId = cursor.getString(0);
                bean.mMacAddress = cursor.getString(1);
                bean.mLatitude = cursor.getString(2);
                bean.mLongitude = cursor.getString(2);

                LogUtil.i("查询到的设备："+ bean.mDeivceId+" "+bean.mMacAddress+""+bean.mLatitude+" "+ bean.mLongitude);
            }
            cursor.close();//关闭结果集
        }
        //关闭数据库对象
        db.close();
        return bean;
    }

}
