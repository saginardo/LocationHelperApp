package com.notinglife.android.LocationHelper.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.notinglife.android.LocationHelper.R;
import com.notinglife.android.LocationHelper.domain.LocationDevice;

import butterknife.ButterKnife;


/**
 * ${DESCRIPTION}
 *
 * @author saginardo
 * @version ${VERSION}
 *          date 2017-06-13 19:30
 */

public class MineFragment extends Fragment {


    private LocationDevice mLocationDevice;
    public Activity mActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = View.inflate(mActivity, R.layout.fragment_mine, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
/*        //创建一个帮助类对象
        MySqliteOpenHelper mySqliteOpenHelper = new MySqliteOpenHelper(mActivity);
        SQLiteDatabase db = mySqliteOpenHelper.getReadableDatabase();
        mDao = new DeviceRawDao(mActivity);
        mLocationDevice = new LocationDevice();*/

    }


}
