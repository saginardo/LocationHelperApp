package com.notinglife.android.LocationHelper.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.notinglife.android.LocationHelper.domain.LocationDevice;
import com.notinglife.android.LocationHelper.utils.MySqliteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * ${DESCRIPTION}
 *
 * @author saginardo
 * @version ${VERSION}
 *          date 2017-06-11 8:17
 */

public class DeviceRawDao {

    private MySqliteOpenHelper mySqliteOpenHelper;
    private static final String TAG = "DeviceRawDao";
    public DeviceRawDao(Context context) {
        //创建一个帮助类对象
        mySqliteOpenHelper = new MySqliteOpenHelper(context);
    }

    //返回值：代表添加这个新行的Id ，-1代表添加失败
    public boolean add(LocationDevice bean) {

        //执行sql语句需要sqliteDatabase对象
        //调用getReadableDatabase方法,来初始化数据库的创建
        SQLiteDatabase db = mySqliteOpenHelper.getReadableDatabase();

        ContentValues values = new ContentValues();//是用map封装的对象，用来存放值
        values.put("deviceId", bean.mDeviceID);
        values.put("macAddress", bean.mMacAddress);
        values.put("latitude", bean.mLatitude);
        values.put("longitude", bean.mLongitude);

        //table: 表名, nullColumnHack：可以为空，标示添加一个空行,
        //       values:数据一行的值
        //       返回值：代表添加这个新行的Id ，-1代表添加失败
        long result = db.insert("devices", null, values);//底层是在拼装sql语句

        //关闭数据库对象
        db.close();

        return result != -1;//-1代表添加失败

    }

    //按照设备编号删除
    public int deleteById(String deviceId) {

        //执行sql语句需要sqliteDatabase对象
        //调用getReadableDatabase方法,来初始化数据库的创建
        SQLiteDatabase db = mySqliteOpenHelper.getReadableDatabase();
        //table ：表名, whereClause: 删除条件, whereArgs：条件的占位符的参数 ; 返回值：成功删除多少行
        int result = db.delete("devices", "deviceId = ?", new String[]{deviceId});
        //关闭数据库对象
        db.close();
        return result;
    }

    //按照设备自增序号删除,该方法是为了获得recyclerview中的position
    public int deleteById(int mId) {

        //执行sql语句需要sqliteDatabase对象
        //调用getReadableDatabase方法,来初始化数据库的创建
        SQLiteDatabase db = mySqliteOpenHelper.getReadableDatabase();
        //table ：表名, whereClause: 删除条件, whereArgs：条件的占位符的参数 ; 返回值：成功删除多少行
        int result = db.delete("devices", "_id = ?", new String[]{mId + ""});
        //关闭数据库对象
        db.close();
        return result;

    }


    public int deleteAll() {
        //执行sql语句需要sqliteDatabase对象
        //调用getReadableDatabase方法,来初始化数据库的创建
        SQLiteDatabase db = mySqliteOpenHelper.getReadableDatabase();
        //table ：表名, whereClause: 删除条件, whereArgs：条件的占位符的参数 ; 返回值：成功删除多少行
        int result = db.delete("devices", null, null);
        //关闭数据库对象
        //LogUtil.i(TAG,"成功删除" + result + "行");
        db.close();
        return result;

    }

    //返回值：成功修改多少行
    public int update(LocationDevice bean) {

        //执行sql语句需要sqliteDatabase对象
        //调用getReadableDatabase方法,来初始化数据库的创建
        SQLiteDatabase db = mySqliteOpenHelper.getReadableDatabase();

        ContentValues values = new ContentValues();//是用map封装的对象，用来存放值
        values.put("deviceId", bean.mDeviceID);
        values.put("macAddress", bean.mMacAddress);
        values.put("latitude", bean.mLatitude);
        values.put("longitude", bean.mLongitude);

        //table:表名, values：更新的值, whereClause:更新的条件, whereArgs：更新条件的占位符的值,
        // 返回值：成功修改多少行
        int result = db.update("devices", values, "_id = ?", new String[]{bean.mID + ""});
        //关闭数据库对象
        db.close();
        return result;

    }

    public LocationDevice queryById(String deviceId) {

        LocationDevice bean = new LocationDevice();
        SQLiteDatabase db = mySqliteOpenHelper.getReadableDatabase();

        //table:表名,
        // columns：查询的列名,如果null代表查询所有列；
        // selection:查询条件, selectionArgs：条件占位符的参数值,
        // groupBy:按什么字段分组, having:分组的条件, orderBy:按什么字段排序
        Cursor cursor = db.query("devices", null,
                "deviceId = ?", new String[]{deviceId}, null, null, null);

        //解析Cursor中的数据
        if (cursor != null && cursor.getCount() > 0) {//判断cursor中是否存在数据

            while (cursor.moveToNext()) {//条件，游标能否定位到下一行
                //获取数据
                bean.mID = cursor.getInt(0);
                bean.mDeviceID = cursor.getString(1);
                bean.mMacAddress = cursor.getString(2);
                bean.mLatitude = cursor.getString(3);
                bean.mLongitude = cursor.getString(4);
                //LogUtil.i("查询到的设备：" + bean.mDeiviceId + " " + bean.mMacAddress + "" + bean.mLatitude + " " + bean.mLongitude);
            }
            cursor.close();//关闭结果集
            //关闭数据库对象
            db.close();
            return bean;
        }
        //关闭数据库对象
        db.close();
        return null;
    }

    public LocationDevice queryByMac(String deviceMac) {

        LocationDevice bean = new LocationDevice();
        SQLiteDatabase db = mySqliteOpenHelper.getReadableDatabase();

        //table:表名,
        // columns：查询的列名,如果null代表查询所有列；
        // selection:查询条件, selectionArgs：条件占位符的参数值,
        // groupBy:按什么字段分组, having:分组的条件, orderBy:按什么字段排序
        Cursor cursor = db.query("devices", null,
                "macAddress = ?", new String[]{deviceMac}, null, null, null);

        //解析Cursor中的数据
        if (cursor != null && cursor.getCount() > 0) {//判断cursor中是否存在数据
            while (cursor.moveToNext()) {//条件，游标能否定位到下一行
                //获取数据
                bean.mID = cursor.getInt(0);
                bean.mDeviceID = cursor.getString(1);
                bean.mMacAddress = cursor.getString(2);
                bean.mLatitude = cursor.getString(3);
                bean.mLongitude = cursor.getString(4);
                //LogUtil.i(TAG,"查询到的设备：" + bean.mDeviceID + " " + bean.mMacAddress + "" + bean.mLatitude + " " + bean.mLongitude);
            }
            cursor.close();//关闭结果集
            //关闭数据库对象
            db.close();
            return bean;
        }
        //关闭数据库对象
        db.close();
        return null;
    }

    public List<LocationDevice> queryWithLimit(String limit) {

        List<LocationDevice> beans = new ArrayList<>();
        LocationDevice bean ;
        SQLiteDatabase db = mySqliteOpenHelper.getReadableDatabase();

        //table:表名,
        // columns：查询的列名,如果null代表查询所有列；
        // selection:查询条件, selectionArgs：条件占位符的参数值,
        // groupBy:按什么字段分组, having:分组的条件, orderBy:按什么字段排序
        Cursor cursor = db.query("devices", null,
                null, null, null, null, "deviceId asc", limit);

        //解析Cursor中的数据
        if (cursor != null && cursor.getCount() > 0) {//判断cursor中是否存在数据
            while (cursor.moveToNext()) {//条件，游标能否定位到下一行
                //获取数据
                bean = new LocationDevice();
                bean.mID = cursor.getInt(0);
                bean.mDeviceID = cursor.getString(1);
                bean.mMacAddress = cursor.getString(2);
                bean.mLatitude = cursor.getString(3);
                bean.mLongitude = cursor.getString(4);
                beans.add(bean);
                //LogUtil.i(TAG,"查询到的设备：" + bean.mDeviceID + " " + bean.mMacAddress + "" + bean.mLatitude + " " + bean.mLongitude);
            }
            cursor.close();//关闭结果集
            //关闭数据库对象
            db.close();
            return beans;
        }
        //关闭数据库对象
        db.close();
        return null;
    }



    public LocationDevice queryLastSave() {

        LocationDevice bean = new LocationDevice();
        SQLiteDatabase db = mySqliteOpenHelper.getReadableDatabase();

        //table:表名,
        // columns：查询的列名,如果null代表查询所有列；
        // selection:查询条件, selectionArgs：条件占位符的参数值,
        // groupBy:按什么字段分组, having:分组的条件, orderBy:按什么字段排序
        //Cursor cursor = db.query("devices", null,
        //        "_id = ?", new String[]{"(select max(_id) from  devices)"}, null, null, null);
        Cursor cursor = db.rawQuery("select * from devices where _id = (select max(_id) from  devices)", new String[]{});
        //解析Cursor中的数据
        if (cursor != null && cursor.getCount() > 0) {//判断cursor中是否存在数据
            while (cursor.moveToNext()) {//条件，游标能否定位到下一行
                //获取数据
                bean.mID = cursor.getInt(0);
                bean.mDeviceID = cursor.getString(1);
                bean.mMacAddress = cursor.getString(2);
                bean.mLatitude = cursor.getString(3);
                bean.mLongitude = cursor.getString(4);
            }
            cursor.close();//关闭结果集
            //关闭数据库对象
            db.close();
            return bean;
        }

        //关闭数据库对象
        db.close();
        return null;
    }

    public List<LocationDevice> queryAll() {
        List<LocationDevice> list = new ArrayList<>();
        SQLiteDatabase db = mySqliteOpenHelper.getReadableDatabase();

        //table:表名,
        // columns：查询的列名,如果null代表查询所有列；
        // selection:查询条件, selectionArgs：条件占位符的参数值,
        // groupBy:按什么字段分组, having:分组的条件, orderBy:按什么字段排序
//        Cursor cursor = db.query("devices", new String[]{"deviceId", "macAddress", "latitude", "longitude"},
//                null, null, null, null, "deviceId asc");
        Cursor cursor = db.query("devices", null,
                null, null, null, null, "deviceId asc");
        if (cursor != null && cursor.getCount() > 0) {//判断cursor中是否存在数据

            while (cursor.moveToNext()) {//条件，游标能否定位到下一行
                // FIXED: 2017/6/10  每一次都是新的bean，不然会串行,始终添加最新的数据
                LocationDevice bean = new LocationDevice();
                //获取数据
                bean.mID = cursor.getInt(0);
                bean.mDeviceID = cursor.getString(1);
                bean.mMacAddress = cursor.getString(2);
                bean.mLatitude = cursor.getString(3);
                bean.mLongitude = cursor.getString(4);
                list.add(bean);
            }
            cursor.close();//关闭结果集
            //关闭数据库对象
            db.close();
            return list;
        }

        //关闭数据库对象
        db.close();
        return null;
    }

}
