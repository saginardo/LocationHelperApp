package com.notinglife.android.LocationHelper;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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

import com.notinglife.android.LocationHelper.adapter.DeviceRecyclerAdapter;
import com.notinglife.android.LocationHelper.dao.DeviceRawDao;
import com.notinglife.android.LocationHelper.domain.LocationDevice;
import com.notinglife.android.LocationHelper.ui.EditDialog;
import com.notinglife.android.LocationHelper.utils.LogUtil;
import com.notinglife.android.LocationHelper.utils.ToastUtil;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.notinglife.android.LocationHelper.utils.FileUtils.saveListToFile;

public class DeviceDetailActivity extends AppCompatActivity implements View.OnClickListener {

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


    private DeviceRecyclerAdapter mDeviceRecyclerAdapter;
    private List<LocationDevice> mList;
    private DeviceRawDao mDao;
    private AlertDialog.Builder builder;
    private EditDialog mEditDialog;
    private LinearLayoutManager mLayoutManager;

    //showdialog标志位
    private final static int DELETEBYID = 0;
    private final static int DELETEALL = 1;
    private final static int UPDATEDEVICE = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_detail);

        ButterKnife.bind(this);
        mQueryById.setOnClickListener(this);
        mQueryAll.setOnClickListener(this);
        mDeleteById.setOnClickListener(this);
        mDeleteAll.setOnClickListener(this);
        mSaveToFile.setOnClickListener(this);
        mSendData.setOnClickListener(this);
        mUpdateById.setOnClickListener(this);

        mDao = new DeviceRawDao(getApplicationContext());
        mList = mDao.queryAll();

        if (mList == null) {
            mScrollView.setVisibility(View.GONE);
            mTextView.setVisibility(View.VISIBLE);
        } else {

            mScrollView.setVisibility(View.VISIBLE);
            mTextView.setVisibility(View.GONE);
            mLayoutManager = new LinearLayoutManager(this);
            mRecyclerView.setLayoutManager(mLayoutManager);
            mDeviceRecyclerAdapter = new DeviceRecyclerAdapter(this, mList);
            mRecyclerView.setAdapter(mDeviceRecyclerAdapter);
            mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), mRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    // TODO: 2017/6/12 单击事件的处理
                }

                @Override
                public void onItemLongClick(View view, int position) {
                    //showEditDeviceDialog(view, mList.get(position), position);
                    showDialog(view,"修改设备信息",null,mList.get(position),position,UPDATEDEVICE);
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
                    //LogUtil.i("删除设备：" + device.mDeivceId);
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

    }

    @Override
    public void onClick(View v) {

        List<LocationDevice> locationDevices = null;
        LocationDevice deviceToDo = null; //查询出来的临时对象
        int position = 0;
        switch (v.getId()) {
            case R.id.bt_query_by_id:
                String queryString = mQueryId.getText().toString();
                if (TextUtils.isEmpty(queryString)) {
                    ToastUtil.showShortToast(getApplicationContext(), "请输入有效的设备的ID");
                    return;
                }
                deviceToDo = mDao.queryById(queryString);
                //// FIXMD: 2017/6/13 对象为空，就没有必要判断对象的数据；对象不为空，就还需要判断对象数据不为空
                if (deviceToDo == null) {
                    ToastUtil.showShortToast(getApplicationContext(), "没有查询到设备，请检查输入的设备ID");
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
                    ToastUtil.showShortToast(getApplicationContext(), "没有查询到设备信息");
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
                    ToastUtil.showShortToast(getApplicationContext(), "请输入有效的设备的ID");
                    return;
                }

                //// FIXME: 2017/6/13 rework
                deviceToDo = mDao.queryById(deleteString);
                if (deviceToDo == null) {
                    ToastUtil.showShortToast(getApplicationContext(), "没有查询到需要删除的设备");
                    return;
                }
                position = mList.indexOf(deviceToDo);
                showDialog(v, "删除当前设备", null, deviceToDo, position, DELETEBYID);

                // showDeleteByIdDialog(v, deleteString);
                break;

            case R.id.bt_delete_all:
                if (mList == null || mList.size() == 0) {
                    ToastUtil.showShortToast(getApplicationContext(), "当前没有设备数据");
                    return;
                }

                // FIXME: 2017/6/13 rework
                //showDeleteAllDialog(v);
                showDialog(v,"删除所有设备信息","请确认是否删除所有设备信息",null,-1,DELETEALL);
                break;

            case R.id.bt_send_data:

                locationDevices = mDao.queryAll();
                if (locationDevices == null || locationDevices.size() == 0) {
                    ToastUtil.showShortToast(getApplicationContext(), "当前没有数据，无法发送");
                    return;
                }

                saveListToFile(getApplicationContext(), mDao.queryAll());

                Intent intentShareFile = new Intent(Intent.ACTION_SEND);
                String myFilePath = DeviceDetailActivity.this.getExternalFilesDir("data").getPath();
                File file = new File(myFilePath, "data.txt");

                if (file.exists()) {
                    intentShareFile.setType("application/pdf");
                    intentShareFile.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + file));

                    intentShareFile.putExtra(Intent.EXTRA_SUBJECT,
                            "Sharing File...");
                    intentShareFile.putExtra(Intent.EXTRA_TEXT, "Sharing File...");

                    startActivity(Intent.createChooser(intentShareFile, "Share File"));
                } else {
                    ToastUtil.showShortToast(getApplicationContext(), "没有数据文件");
                }
                break;

            case R.id.bt_update_by_id:
                String deviceId = mUpdateId.getText().toString();

                deviceToDo = mDao.queryById(deviceId);
                if (deviceToDo == null) {
                    ToastUtil.showShortToast(getApplicationContext(), "查询不到该设备，请检查输入ID");
                    return;
                }

                LogUtil.i(deviceToDo.toString() + "\n");
                // FIXED: 2017/6/13 如何获得对象的位置
                // 通过重写 对象的 hashCode和equals方法，判断查询出来的对象是同一个对象，从而获取在mList中的位置
                position = mList.indexOf(deviceToDo);

                LogUtil.i("当前查询到的设备位置: " + position);
                showDialog(v,"修改设备信息",null,deviceToDo,position,UPDATEDEVICE);
               // showEditDeviceDialog(v, deviceToDo, position);
            default:
                break;
        }
    }

    //Handler 往往持有一个隐式对象，通常为Activity，如果这个handler伴随着一个耗时的后台线程
    //如果突然退出Activity时，线程还未执行完毕，这时候GC就不会回收Activity，导致内存泄露
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            LocationDevice tmpDevice = (LocationDevice) msg.obj;
            int position = msg.what;
            int flag = msg.arg1;

            //删除所有设备
            if(flag==DELETEALL){
                mDao.deleteAll();
                mList.clear();
                mDeviceRecyclerAdapter.notifyDataSetChanged();
                ToastUtil.showShortToast(getApplicationContext(), "成功清除所有设备信息");
            }

            if (tmpDevice != null && tmpDevice.mDeivceId != null) {
                if (flag == UPDATEDEVICE) {
                    int update = mDao.update(tmpDevice);
                    if (update == 1) {
                        ToastUtil.showShortToast(getApplicationContext(), "成功修改设备信息");
                        mList.set(position, tmpDevice);
                        mDeviceRecyclerAdapter.notifyItemChanged(position);
                    }else {
                        //这里应该判断不到，在修改之前已经查询输入的ID是否合法，考虑是否删除？
                        ToastUtil.showShortToast(getApplicationContext(),"修改失败，请检查ID是否输入错误");
                    }
                }

                if (flag == DELETEBYID) {
                    mDao.deleteById(tmpDevice.mId);
                    mList.remove(position);
                    mDeviceRecyclerAdapter.notifyItemRemoved(position);
                }
            }


        }
    };

    //删除单条数据
    // FIXED: 2017/6/12 用自定义dialog重构
    private void showDeleteByIdDialog(View view, final String deleteString) {
        builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.alert);
        builder.setTitle("删除设备信息");
        builder.setMessage("请确认是否当前设备信息？");

        //监听下方button点击事件
        builder.setPositiveButton(R.string.postive_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                LogUtil.i("确定按钮按下");
                int num1 = mDao.deleteById(deleteString);
                // FIXME: 2017/6/13 笨办法 清空后重新查询
                mList.clear();
                List<LocationDevice> tmpList = mDao.queryAll();
                if (tmpList != null && tmpList.size() != 0) {
                    mList.addAll(mDao.queryAll());
                }
                // mList.remove(2);
                mDeviceRecyclerAdapter.notifyDataSetChanged();

                if (num1 < 1) {
                    ToastUtil.showShortToast(getApplicationContext(), "删除失败，请检查设备ID是否正确");
                } else {
                    ToastUtil.showShortToast(getApplicationContext(), "成功删除" + num1 + "条设备信息");
                }
                if (mList.size() == 0) {
                    mScrollView.setVisibility(View.GONE);
                    mTextView.setVisibility(View.VISIBLE);
                }
            }
        });
        builder.setNegativeButton(R.string.negative_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                LogUtil.i("取消按钮按下");
            }
        });

        //设置对话框是可取消的
        builder.setCancelable(true);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //删除多条数据
    // FIXED: 2017/6/12 用自定义dialog重构
    private void showDeleteAllDialog(View view) {

        builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.alert);
        builder.setTitle("删除设备信息");
        builder.setMessage("请确认是否删除所有设备信息？");

        //监听下方button点击事件
        builder.setPositiveButton(R.string.postive_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                LogUtil.i("确定按钮按下");
                int num2 = mDao.deleteAll();
                mList.clear();
                mDeviceRecyclerAdapter.notifyDataSetChanged();
                ToastUtil.showShortToast(getApplicationContext(), "成功删除" + num2 + "行数据");
                mScrollView.setVisibility(View.GONE);
                mTextView.setVisibility(View.VISIBLE);
            }
        });
        builder.setNegativeButton(R.string.negative_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                LogUtil.i("取消按钮按下");
            }
        });

        //设置对话框是可取消的
        builder.setCancelable(true);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showEditDeviceDialog(View view, LocationDevice locationDevice, final int position) {

        final LocationDevice[] tmpDevice = {new LocationDevice()};
        mEditDialog = new EditDialog(DeviceDetailActivity.this, "修改设备信息1", null);

        //将需要修改的对象传递给dialog
        mEditDialog.setDeviceInfo(locationDevice);
        //响应确定键的点击事件
        mEditDialog.setPositiveOnclickListener(new EditDialog.onPositiveOnclickListener() {
            @Override
            public void onPositiveClick() {

                tmpDevice[0] = mEditDialog.getDeviceInfo();
                Message msg = Message.obtain();
                msg.obj = tmpDevice[0];
                msg.what = position;
                mHandler.sendMessage(msg);

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

    //还是通过 equals方法获取在mList中的位置
    private void showDialog(View view, String title, String message, LocationDevice locationDevice, final int position, final int flag) {

        mEditDialog = new EditDialog(DeviceDetailActivity.this, title, message);
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
        //响应确定键的点击事件
        mEditDialog.setPositiveOnclickListener(new EditDialog.onPositiveOnclickListener() {
            @Override
            public void onPositiveClick() {
                Message msg = Message.obtain();
                if (flag == DELETEALL) {
                    msg.obj = null;
                    msg.what = -1;
                    msg.arg1 = flag;
                    mHandler.sendMessage(msg);
                } else {
                    tmpDevice[0] = mEditDialog.getDeviceInfo();
                    msg.obj = tmpDevice[0];
                    msg.what = position;
                    msg.arg1 = flag;
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
