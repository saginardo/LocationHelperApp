package com.notinglife.android.LocationHelper.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;

import com.notinglife.android.LocationHelper.R;
import com.notinglife.android.LocationHelper.adapter.DeviceRecyclerAdapter;
import com.notinglife.android.LocationHelper.dao.DeviceRawDao;
import com.notinglife.android.LocationHelper.domain.LocationDevice;
import com.notinglife.android.LocationHelper.utils.DialogUtil;
import com.notinglife.android.LocationHelper.utils.MyLocalBroadcastManager;
import com.notinglife.android.LocationHelper.view.EmptyRecyclerView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class RepairDevicesActivity extends AppCompatActivity {


    private static final String TAG = "RepairDevicesActivity";
    public static final String LOCATIONDEVICE = "LOCATIONDEVICE";
    public static final String DEVICEPOSITION = "DEVICEPOSITION";
    //showdialog标志位
    private final static int DELETE_BY_ID = 0;
    private final static int DELETE_ALL = 1;
    private final static int UPDATE_DEVICE = 2;
    private final static int ON_SAVE_DATA = 3;  //接收到ContentFragment的消息的标志位
    private final static int UNDO_SAVE = 4;
    private final static int ON_RECEIVE_LOCATION_DATA = 5;
    private final static int ON_RECEIVE_SCAN_RESULT = 6;
    private final static int ON_EDIT_DEVICE = 7;
    private final static int ON_DELETE_ALL_DATA = 8;

    //设备维护标志位
    private final static int DEVICE_REPAIR = 50;
    private final static int DEVICE_REPAIR_DOWN = 51;


    @BindView(R.id.repair_device_list_toolBar)
    Toolbar mRepairDeviceListToolBar;
    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;
    @BindView(R.id.repair_device_recyclerview)
    EmptyRecyclerView mRepairDeviceRecyclerView;
    @BindView(R.id.repair_device_empty_view)
    View mEmptyView;


    private Unbinder mUnBinder;
    private Activity mActivity;
    private List<LocationDevice> mList;
    private DeviceRecyclerAdapter mDeviceRecyclerAdapter;
    private MyHandler mHandler;
    private LocalBroadcastManager broadcastManager;
    private MyReceiver mReceiver;
    private DeviceRawDao mDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repair_devices);
        mUnBinder = ButterKnife.bind(this);
        mActivity = this;
        mDao = new DeviceRawDao(this);

        setSupportActionBar(mRepairDeviceListToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mList = new ArrayList<>();
        if (mDao.queryByStatus("OffLine") != null) {
            mList.addAll(mDao.queryByStatus("OffLine"));
        }
        mHandler = new MyHandler(RepairDevicesActivity.this);

        //动态注册本地广播，以便接收数据更改，及时更新待维护设备列表
        broadcastManager = LocalBroadcastManager.getInstance(mActivity);
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.notinglife.android.action.DATA_CHANGED"); //DeviceDetailActivity 发送的修改设备信息的广播
        mReceiver = new MyReceiver();
        broadcastManager.registerReceiver(mReceiver, filter);


        //设置recyclerview
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        mRepairDeviceRecyclerView.setLayoutManager(layoutManager);
        mDeviceRecyclerAdapter = new DeviceRecyclerAdapter(mActivity, mList);
        mRepairDeviceRecyclerView.setAdapter(mDeviceRecyclerAdapter);
        mRepairDeviceRecyclerView.setEmptyView(mEmptyView);

        mRepairDeviceRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(mActivity, mRepairDeviceRecyclerView
                , new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                // FIXED: 2017/6/12 单击事件的处理，预计实现点击进入详情页修改
                Intent intent = new Intent();
                LocationDevice locationDevice = mList.get(position);
                Bundle bundle = new Bundle();
                bundle.putSerializable(LOCATIONDEVICE, locationDevice);
                intent.putExtra(DEVICEPOSITION, position);
                intent.putExtra(LOCATIONDEVICE, bundle);//传入mList中的position信息，便于回传修改
                intent.setClass(mActivity, DeviceDetailActivity.class);
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {
                //DialogUtil.showDeviceEditDialog(mActivity, mHandler, "删除设备信息", null, mList.get(position), position, DELETE_BY_ID);
                DialogUtil.showDeviceEditDialog(mActivity, mHandler, "请确认设备是否维护完毕", null, mList.get(position), position, DEVICE_REPAIR_DOWN);
            }
        }));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnBinder.unbind();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private static class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {

        public interface OnItemClickListener {
            void onItemClick(View view, int position);

            void onItemLongClick(View view, int position);
        }

        private OnItemClickListener mListener;
        private GestureDetector mGestureDetector;

        public RecyclerItemClickListener(Context context, final RecyclerView recyclerView, OnItemClickListener listener) {
            mListener = listener;
            mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View childView = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (childView != null && mListener != null) {
                        mListener.onItemLongClick(childView, recyclerView.getChildAdapterPosition(childView));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
            View childView = view.findChildViewUnder(e.getX(), e.getY());
            if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
                mListener.onItemClick(childView, view.getChildAdapterPosition(childView));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView view, MotionEvent motionEvent) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        }
    }

    private static class MyHandler extends Handler {
        WeakReference<RepairDevicesActivity> mActivity;
        MyHandler(RepairDevicesActivity activity) {
            mActivity = new WeakReference<>(activity);
        }
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int flag = msg.what;
            int position = msg.arg1;
            LocationDevice tmpDevice = (LocationDevice) msg.obj;

            RepairDevicesActivity activity = mActivity.get();
            if (activity != null) {
                if (flag == DEVICE_REPAIR_DOWN) {
                    //说明选中设备维护完毕，更新数据库和UI

                    activity.mDao.updateDeviceStatus(tmpDevice.mDeviceID, "Normal");
                    tmpDevice.mStatus = "Normal";// 维护完成，改变其状态
                    activity.mList.remove(position);
                    activity.mDeviceRecyclerAdapter.notifyItemRemoved(position);
                    //通知 DeviceListFragment 设备状态更新,positon不一致
                    MyLocalBroadcastManager.sendLocalBroadcast(activity, "DATA_CHANGED", DEVICE_REPAIR_DOWN
                            , DEVICEPOSITION, -1, "DEVICE_DATA", tmpDevice);
                }


                if (flag == ON_EDIT_DEVICE) {
                    //如果设备的 status改变成 Normal，那么从待维护列表中删除该设备
                    if (tmpDevice != null && tmpDevice.mStatus.equals("Normal")) {
                        //ToastUtil.showShortToast(activity.mActivity, "成功修改设备信息");
                        position = activity.mList.indexOf(tmpDevice);
                        activity.mList.remove(position);
                        activity.mDeviceRecyclerAdapter.notifyItemRemoved(position);
                    } else if (tmpDevice != null && tmpDevice.mStatus.equals("OffLine")) {
                        //如果设备的 status改变成 Normal，那么从待维护列表中添加该设备，
                        position = activity.mList.indexOf(tmpDevice);
                        if (position >= 0) {
                            //如果待维护设备有该设备，那么更新该设备的状态
                            activity.mList.set(position, tmpDevice);
                            activity.mDeviceRecyclerAdapter.notifyItemChanged(position);
                        } else {
                            //如果没有，直接添加
                            activity.mList.add(tmpDevice);
                            activity.mDeviceRecyclerAdapter.notifyDataSetChanged();
                        }
                    }
                }

            }
        }
    }

    //
    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            int flag = intent.getIntExtra("flag", -1);
            int position = intent.getIntExtra(DEVICEPOSITION, -1);
            Message message = Message.obtain();
            message.what = flag;
            message.obj = intent.getBundleExtra("DEVICE_DATA").getSerializable("DEVICE_DATA");
            message.arg1 = position;
            mHandler.sendMessage(message);
        }
    }
}
