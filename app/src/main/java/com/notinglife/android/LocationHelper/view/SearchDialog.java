package com.notinglife.android.LocationHelper.view;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.notinglife.android.LocationHelper.R;
import com.notinglife.android.LocationHelper.activity.DeviceDetailActivity;
import com.notinglife.android.LocationHelper.dao.DeviceRawDao;
import com.notinglife.android.LocationHelper.domain.LocationDevice;
import com.notinglife.android.LocationHelper.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * ${DESCRIPTION}
 *
 * @author saginardo
 * @version ${VERSION}
 *          date 2017-06-12 14:39
 */

public class SearchDialog extends Dialog {


    private static final String LOCATIONDEVICE = "LOCATIONDEVICE";
    private static final String TAG = "SearchDialog";

    private Context context;
    private View customView;
    private List<String> mDeviceIds;
    private ArrayAdapter<String> mArrayAdapter;
    private LocationDevice mLocationDevice;
    private Unbinder mUnBinder;

    @BindView(R.id.tv_backspace)
    TextView mTextView;
    @BindView(R.id.listView)
    ListView mListView;
    @BindView(R.id.emptyview)
    View mEmptyView;
    @BindView(R.id.sv_searchView)
    SearchView mSearchView;

    public SearchDialog(Context context) {
        super(context, R.style.MyDialog);
        this.context = context;

    }

    public SearchDialog(Context context, int theme){
        super(context, theme);
        this.context = context;
        customView = LayoutInflater.from(context).inflate(theme,null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(customView);
        //按空白处不能取消动画
        setCanceledOnTouchOutside(true);
        mUnBinder = ButterKnife.bind(this);

        //初始化界面控件
        initView();
        //初始化界面数据
        initData();
        //初始化界面控件的事件
        initEvent();

    }


    @Override
    public View findViewById(int id) {
        return super.findViewById(id);
    }

    public View getCustomView() {
        return customView;
    }

    /**
     * 初始化界面控件的显示数据回显
     */
    private void initData() {

    }

    private void initView() {
        mSearchView.onActionViewExpanded();
        //mSearchView.setBackgroundColor(Color.parseColor("#303F9F"));
        mSearchView.setIconifiedByDefault(false);
        mSearchView.setSubmitButtonEnabled(true);

        SpannableString spanText = new SpannableString("请输入设备号");
        spanText.setSpan(new AbsoluteSizeSpan(16, true), 0, spanText.length(),
                Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        spanText.setSpan(new ForegroundColorSpan(Color.BLACK), 0,
                spanText.length(),Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

        mSearchView.setQueryHint(spanText);

        mDeviceIds = new ArrayList<>();
        mArrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, mDeviceIds);
        mListView.setAdapter(mArrayAdapter);
        //mListView.setTextFilterEnabled(true);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                String deviceId = mDeviceIds.get(position);
                LogUtil.i(TAG," 传递的设备ID：" +deviceId);
                Bundle bundle = new Bundle();
                bundle.putSerializable(LOCATIONDEVICE,mLocationDevice);
                intent.putExtra(LOCATIONDEVICE,bundle);
                intent.setClass(context,DeviceDetailActivity.class);
                context.startActivity(intent);
                // FIXME: 2017/6/16 用listener关闭搜索框
            }
        });
    }
    /**
     * 初始化界面的确定和取消监听器
     */
    private void initEvent() {

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            // 当点击搜索按钮时触发该方法
            @Override
            public boolean onQueryTextSubmit(String query) {
                //ToastUtil.showShortToast(getApplicationContext(),"点击了开始搜索");
                DeviceRawDao mDao = new DeviceRawDao(context);
                if(!TextUtils.isEmpty(query)){
                    mLocationDevice = mDao.queryById(query);
                    if(mLocationDevice !=null){
                        mEmptyView.setVisibility(View.GONE);
                        String result = mLocationDevice.mDeviceID;
                        //LogUtil.i("查询结果"+ result);
                        mDeviceIds.clear();
                        mDeviceIds.add("设备号："+ result);
                        mArrayAdapter.notifyDataSetChanged();
                    }else {
                        //mDeviceIds.clear();
                        //mArrayAdapter.notifyDataSetChanged();
                        mEmptyView.setVisibility(View.VISIBLE);
                    }
                }
                return false;
            }

            // 当搜索内容改变时触发该方法
            @Override
            public boolean onQueryTextChange(String newText) {
                if (!TextUtils.isEmpty(newText)){
                    //mListView.setFilterText(newText);
                }else{
                    //mListView.clearTextFilter();
                }
                return false;
            }
        });
    }


}
