package com.notinglife.android.LocationHelper.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.notinglife.android.LocationHelper.MainActivity;
import com.notinglife.android.LocationHelper.R;
import com.notinglife.android.LocationHelper.adapter.DeviceRecyclerAdapter;
import com.notinglife.android.LocationHelper.dao.DeviceRawDao;
import com.notinglife.android.LocationHelper.domain.LocationDevice;
import com.notinglife.android.LocationHelper.ui.EditDialog;
import com.notinglife.android.LocationHelper.utils.FileUtil;
import com.notinglife.android.LocationHelper.utils.LogUtil;
import com.notinglife.android.LocationHelper.utils.ToastUtil;
import com.notinglife.android.LocationHelper.utils.UIUtil;

import java.io.File;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * ${DESCRIPTION}
 *
 * @author saginardo
 * @version ${VERSION}
 *          date 2017-06-13 19:31
 */

public class ListDeviceFragment extends BaseFragment implements View.OnClickListener {

    @BindView(R.id.id_recyclerview)
    RecyclerView mRecyclerView;

    @BindView(R.id.bt_save_to_file)
    Button mSaveToFile;
    @BindView(R.id.bt_send_data)
    Button mSendData;
    @BindView(R.id.bt_delete_all)
    Button mDeleteAll;
    @BindView(R.id.bt_delete_by_id)
    Button mDeleteById;
    @BindView(R.id.bt_query_by_id)
    Button mQueryById;
    @BindView(R.id.bt_query_all)
    Button mQueryAll;
    @BindView(R.id.bt_update_by_id)
    Button mUpdateById;

    @BindView(R.id.et_delete_id)
    EditText mDeleteId;
    @BindView(R.id.et_query_id)
    EditText mQueryId;
    @BindView(R.id.et_update_id)
    EditText mUpdateId;

    @BindView(R.id.sv_devices)
    ScrollView mScrollView;
    @BindView(R.id.tv_no_data)
    TextView mTextView;

    private static final String TAG_ACQ_DATA = "TAG_ACQ_DATA";
    private static final String TAG_MAP_MARK = "TAG_MAP_MARK";
    private static final String TAG_LIST_DEVICE = "TAG_LIST_DEVICE";

    //showdialog标志位
    private final static int DELETE_BY_ID = 0;
    private final static int DELETE_ALL = 1;
    private final static int UPDATE_DEVICE = 2;
    private final static int ON_SAVE_DATA = 3;  //接收到ContentFragment的消息的标志位

    private DeviceRecyclerAdapter mDeviceRecyclerAdapter;
    private List<LocationDevice> mList;
    private DeviceRawDao mDao;
    private LinearLayoutManager mLayoutManager;
    private LocalBroadcastManager broadcastManager;
    private MyHandler mHandler;


    @Override
    public View initView() {
        View view = View.inflate(mActivity, R.layout.fragment_list_device, null);
        ButterKnife.bind(this, view);

        mQueryById.setOnClickListener(this);
        mQueryAll.setOnClickListener(this);
        mDeleteById.setOnClickListener(this);
        mDeleteAll.setOnClickListener(this);
        mSaveToFile.setOnClickListener(this);
        mSendData.setOnClickListener(this);
        mUpdateById.setOnClickListener(this);

        mDao = new DeviceRawDao(mActivity);
        mList = mDao.queryAll();

        if (mList == null) {
            mScrollView.setVisibility(View.GONE);
            mTextView.setVisibility(View.VISIBLE);
        } else {

            mScrollView.setVisibility(View.VISIBLE);
            mTextView.setVisibility(View.GONE);
            mLayoutManager = new LinearLayoutManager(mActivity);
            mRecyclerView.setLayoutManager(mLayoutManager);
            mDeviceRecyclerAdapter = new DeviceRecyclerAdapter(mActivity, mList);
            mRecyclerView.setAdapter(mDeviceRecyclerAdapter);

            mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(mActivity, mRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    // TODO: 2017/6/12 单击事件的处理
                }

                @Override
                public void onItemLongClick(View view, int position) {
                    showDialog(view, "修改设备信息", null, mList.get(position), position, UPDATE_DEVICE);
                }
            }));

            ItemTouchHelper.SimpleCallback mCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                    ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
                @Override
                public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                    return false;
                }

                @Override
                public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                    int position = viewHolder.getAdapterPosition();
                    LocationDevice device = mList.remove(position);
                    mDao.deleteById(device.mId);
                    mDeviceRecyclerAdapter.notifyItemRemoved(position);
                }

                @Override
                public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                    if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                        //左右滑动时改变Item的透明度
                        final float alpha = 1 - Math.abs(dX) / (float) viewHolder.itemView.getWidth();
                        viewHolder.itemView.setAlpha(alpha);
                        viewHolder.itemView.setTranslationX(dX);
                    }
                }
            };
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(mCallback);
            itemTouchHelper.attachToRecyclerView(mRecyclerView);
        }
        return view;
    }

    @Override
    public void initData() {

/*      //动态注册广播，以便通知设备的添加和删除，可以及时更新recyclerview
        broadcastManager = LocalBroadcastManager.getInstance(mActivity);
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.notinglife.android.action.DATA_CHANGED");
        mReceiver = new MyReceiver();
        broadcastManager.registerReceiver(mReceiver, filter);*/
        // mHandler = new MyHandler(ListDeviceFragment.this);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        MainActivity mainActivity = (MainActivity) context;
        FragmentManager fm = mainActivity.getSupportFragmentManager();
        ContentFragment contentFragment = (ContentFragment) fm.findFragmentByTag(TAG_ACQ_DATA);
        // 因为onAttach生命周期最靠前，它需要一个handler来设置给外界，所以handler放在了这里初始化
        mHandler = new MyHandler(ListDeviceFragment.this);
        contentFragment.setHandler(mHandler);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // broadcastManager.unregisterReceiver(mReceiver);
    }


    private static class MyHandler extends Handler {

        WeakReference<ListDeviceFragment> mFragment;

        MyHandler(ListDeviceFragment fragment) {
            mFragment = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            int flag = msg.what;
            int position = msg.arg1;

            LogUtil.i("---接收到消息标志位---"+flag  );

            ListDeviceFragment fragment = mFragment.get();
            if (fragment != null) {
                //删除所有设备
                if (flag == DELETE_ALL) {
                    fragment.mDao.deleteAll();
                    fragment.mList.clear();
                    fragment.mDeviceRecyclerAdapter.notifyDataSetChanged();
                    ToastUtil.showShortToast(fragment.getActivity(), "成功清除所有设备信息");
                }

                if (flag == UPDATE_DEVICE) {

                    LocationDevice tmpDevice = (LocationDevice) msg.obj;
                    if (tmpDevice != null && tmpDevice.mDeivceId != null && !tmpDevice.mDeivceId.equals("")) {
                        int update = fragment.mDao.update(tmpDevice);
                        if (update == 1) {
                            ToastUtil.showShortToast(fragment.getActivity(), "成功修改设备信息");
                            fragment.mList.set(position, tmpDevice);
                            fragment.mDeviceRecyclerAdapter.notifyItemChanged(position);
                        } else {
                            //这里应该判断不到，在修改之前已经查询输入的ID是否合法，考虑是否删除？
                            ToastUtil.showShortToast(fragment.getActivity(), "修改失败，请检查ID是否输入错误");
                        }
                    }
                }
                if (flag == DELETE_BY_ID) {
                    LocationDevice tmpDevice = (LocationDevice) msg.obj;
                    if (tmpDevice != null && tmpDevice.mDeivceId != null && !tmpDevice.mDeivceId.equals("")) {
                        int delete = fragment.mDao.deleteById(tmpDevice.mId);

                        if (delete == 1) {
                            ToastUtil.showShortToast(fragment.getActivity(), "成功删除设备信息");
                            fragment.mList.remove(position);
                            fragment.mDeviceRecyclerAdapter.notifyItemRemoved(position);
                        } else {
                            //这里应该判断不到，在修改之前已经查询输入的ID是否合法，考虑是否删除？
                            ToastUtil.showShortToast(fragment.getActivity(), "删除失败，请检查ID是否输入错误");
                        }
                    }
                }

                if (flag == ON_SAVE_DATA) {
                    LogUtil.i(msg.obj.toString());
                    Serializable locationDevice = msg.getData().getSerializable("locationDevice");
                    LogUtil.i("Bundle传递的对象"+locationDevice);
                    //收到contentFragment消息后更新 UI
                    List<LocationDevice> locationDevices = fragment.mDao.queryAll();
                    fragment.mList.clear();
                    fragment.mList.addAll(locationDevices);
                    fragment.mDeviceRecyclerAdapter.notifyDataSetChanged();
                }

            }
        }
    }

    @Override
    public void onClick(View v) {
        List<LocationDevice> locationDevices = null;
        LocationDevice deviceToDo = null; //查询出来的临时对象
        int position;
        switch (v.getId()) {
            case R.id.bt_query_by_id:
                String queryString = mQueryId.getText().toString();
                if (TextUtils.isEmpty(queryString)) {
                    ToastUtil.showShortToast(mActivity, "请输入有效的设备的ID");
                    return;
                }
                deviceToDo = mDao.queryById(queryString);
                //// FIXMD: 2017/6/13 对象为空，就没有必要判断对象的数据；对象不为空，就还需要判断对象数据不为空
                if (deviceToDo == null) {
                    ToastUtil.showShortToast(mActivity, "没有查询到设备，请检查输入的设备ID");
                    return;
                } else {
                    mList.clear();
                    mList.add(deviceToDo);
                    mDeviceRecyclerAdapter.notifyDataSetChanged();
                }
                break;

            case R.id.bt_query_all:
                locationDevices = mDao.queryAll();
                if (locationDevices == null || locationDevices.size() == 0) {
                    ToastUtil.showShortToast(mActivity, "没有查询到设备信息");
                    return;
                } else {
                    mList.clear();
                    mList.addAll(locationDevices);
                    mDeviceRecyclerAdapter.notifyDataSetChanged();
                }
                break;

            case R.id.bt_delete_by_id:

                String deleteString = mDeleteId.getText().toString();
                if (TextUtils.isEmpty(deleteString)) {
                    ToastUtil.showShortToast(mActivity, "请输入有效的设备的ID");
                    return;
                }
                deviceToDo = mDao.queryById(deleteString);
                if (deviceToDo == null) {
                    ToastUtil.showShortToast(mActivity, "没有查询到需要删除的设备");
                    return;
                }
                position = mList.indexOf(deviceToDo);
                //// FIXED: 2017/6/13 rework
                showDialog(v, "删除当前设备", null, deviceToDo, position, DELETE_BY_ID);
                break;

            case R.id.bt_delete_all:
                if (mList == null || mList.size() == 0) {
                    ToastUtil.showShortToast(mActivity, "当前没有设备数据");
                    return;
                }
                // FIXED: 2017/6/13 rework
                showDialog(v, "删除所有设备信息", "请确认是否删除所有设备信息", null, -1, DELETE_ALL);
                break;

            case R.id.bt_send_data:
                locationDevices = mDao.queryAll();
                if (locationDevices == null || locationDevices.size() == 0) {
                    ToastUtil.showShortToast(mActivity, "当前没有数据，无法发送");
                    return;
                }
                FileUtil.saveListToFile(mActivity, mDao.queryAll());

                Intent intentShareFile = new Intent(Intent.ACTION_SEND);
                String myFilePath = mActivity.getExternalFilesDir("data").getPath();
                File file = new File(myFilePath, "data.txt");
                if (file.exists()) {
                    intentShareFile.setType("application/pdf");
                    intentShareFile.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + file));
                    intentShareFile.putExtra(Intent.EXTRA_SUBJECT,
                            "Sharing File...");
                    intentShareFile.putExtra(Intent.EXTRA_TEXT, "Sharing File...");
                    startActivity(Intent.createChooser(intentShareFile, "Share File"));
                } else {
                    ToastUtil.showShortToast(mActivity, "没有数据文件");
                }
                break;

            case R.id.bt_update_by_id:
                String deviceId = mUpdateId.getText().toString();

                deviceToDo = mDao.queryById(deviceId);
                if (deviceToDo == null) {
                    ToastUtil.showShortToast(mActivity, "查询不到该设备，请检查输入ID");
                    return;
                }
                // FIXED: 2017/6/13 如何获得对象的位置
                // 通过重写 对象的 hashCode和equals方法，判断查询出来的对象是同一个对象，从而获取在mList中的位置
                position = mList.indexOf(deviceToDo);
                //showDialog(v, "修改设备信息", null, deviceToDo, position, UPDATE_DEVICE);
                UIUtil.showDialog(v,mActivity,mHandler, "修改设备信息", null, deviceToDo, position, UPDATE_DEVICE);
            default:
                break;
        }
    }

    /**
     * 展示统一风格的对话框，其中包含对话框UI中的数据更新和不同对话框UI的替换
     * @param view 传入的视图对象
     * @param title 对话框的标题
     * @param message 对话框的提示信息；其中只有清除所有和删除单个item有提示，其余都设置为GONE
     * @param locationDevice 传入需要显示在对话框上UI的设备信息
     * @param position LocationDevice在recyclerView中的位置，用来回传给fragment中的recyclerAdapter来更新视图
     * @param flag 标志位，判断弹出对话框的类型
     */
    private void showDialog(View view, String title, String message, LocationDevice locationDevice, final int position, final int flag) {

        final EditDialog mEditDialog = new EditDialog(mActivity, title, message);
        mEditDialog.setDeviceInfo(locationDevice);
        mEditDialog.setFlag(flag);

        //自定义窗体参数
        WindowManager.LayoutParams attributes = mEditDialog.getWindow().getAttributes();
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        attributes.width = (int) (metrics.widthPixels * 0.9);
        attributes.height = (int) (metrics.heightPixels * 0.9);
        attributes.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        attributes.dimAmount = 0.5f;
        mEditDialog.getWindow().setAttributes(attributes);

        final LocationDevice[] tmpDevice = {new LocationDevice()};
        //响应确定的点击事件
        mEditDialog.setPositiveOnclickListener(new EditDialog.onPositiveOnclickListener() {
            @Override
            public void onPositiveClick() {
                Message msg = Message.obtain();
                if (flag == DELETE_ALL) {
                    msg.what = flag;
                    msg.obj = null;
                    msg.arg1 = -1;
                    mHandler.sendMessage(msg);
                } else {
                    tmpDevice[0] = mEditDialog.getDeviceInfo();
                    msg.what = flag;
                    msg.obj = tmpDevice[0];
                    msg.arg1 = position;
                    mHandler.sendMessage(msg);
                }
                mEditDialog.dismiss();
            }
        });

        //响应取消键的点击事件
        mEditDialog.setNegativeOnclickListener(new EditDialog.onNegativeOnclickListener() {
            @Override
            public void onNegativeClick() {
                mEditDialog.dismiss();
            }
        });
        //展示对话框
        mEditDialog.show();
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

}
