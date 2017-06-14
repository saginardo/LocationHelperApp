package com.notinglife.android.LocationHelper.fragment;

import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.widget.Button;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.notinglife.android.LocationHelper.R;
import com.notinglife.android.LocationHelper.dao.DeviceRawDao;
import com.notinglife.android.LocationHelper.domain.LocationDevice;
import com.notinglife.android.LocationHelper.utils.MySqliteOpenHelper;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * ${DESCRIPTION}
 *
 * @author saginardo
 * @version ${VERSION}
 *          date 2017-06-13 19:30
 */

public class MapMarkFragment extends BaseFragment {

    @BindView(R.id.mapview)
    MapView mMapView;
    @BindView(R.id.bt_mark_map)
    Button mMarkMap;


    private DeviceRawDao mDao;
    private LocationDevice mLocationDevice;
    private BaiduMap mBaiduMap;

    @Override
    public View initView() {
        View view = View.inflate(mActivity, R.layout.fragment_map_mark, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void initData() {

        //创建一个帮助类对象
        MySqliteOpenHelper mySqliteOpenHelper = new MySqliteOpenHelper(mActivity);
        //调用getReadableDatabase方法,来初始化数据库的创建
        SQLiteDatabase db = mySqliteOpenHelper.getReadableDatabase();
        mDao = new DeviceRawDao(mActivity);
        mLocationDevice = new LocationDevice();

        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }

    // TODO: 2017/6/14 考虑用sharedprefrences存储退出时所在的位置信息，下一次进入app时候，先读取信息然后再判读是否需要移动到上一次的位置

}
