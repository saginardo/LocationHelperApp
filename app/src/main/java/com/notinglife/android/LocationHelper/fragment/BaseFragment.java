package com.notinglife.android.LocationHelper.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * ${DESCRIPTION}
 *
 * @author saginardo
 * @version ${VERSION}
 *          date 2017-06-13 19:22
 */

public abstract class BaseFragment extends Fragment {
    public Activity mActivity;//这个activity就是MainActivity

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();//获取activity对象
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return initView();
    }
    // fragment所依赖的activity的onCreate方法执行结束
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // 初始化数据
        initData();
    }

    // 初始化布局, 必须由子类实现
    public abstract View initView();

    // 初始化数据, 必须由子类实现
    public abstract void initData();

}
