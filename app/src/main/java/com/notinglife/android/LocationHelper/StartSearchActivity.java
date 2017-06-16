package com.notinglife.android.LocationHelper;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.notinglife.android.LocationHelper.dao.DeviceRawDao;
import com.notinglife.android.LocationHelper.domain.LocationDevice;
import com.notinglife.android.LocationHelper.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;



public class StartSearchActivity extends AppCompatActivity {

    @BindView(R.id.searchView)
    SearchView mSearchView;
    @BindView(R.id.listView)
    ListView mListView;
    @BindView(R.id.emptyview)
    View mEmptyView;

    private List<String> mDeviceIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_search);

        ButterKnife.bind(this);



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
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mDeviceIds);
        mListView.setAdapter(arrayAdapter);
        mListView.setEmptyView(mEmptyView);
        //mListView.setTextFilterEnabled(true);
        //过时
/*        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                String deviceId = mDeviceIds.get(position);
                LogUtil.i("传递的设备ID：" +deviceId);
                intent.putExtra("deviceId",deviceId);
                intent.setClass(StartSearchActivity.this,DeviceDetailActivity.class);
                startActivity(intent);
            }
        });*/


        // 设置搜索文本监听
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            // 当点击搜索按钮时触发该方法
            @Override
            public boolean onQueryTextSubmit(String query) {
                //ToastUtil.showShortToast(getApplicationContext(),"点击了开始搜索");
                DeviceRawDao mDao = new DeviceRawDao(getApplicationContext());
                if(!TextUtils.isEmpty(query)){
                    LocationDevice locationDevice = mDao.queryById(query);
                    if(locationDevice!=null){
                        String result = locationDevice.mDeivceId;
                        LogUtil.i("查询结果"+ result);
                        mDeviceIds.clear();
                        mDeviceIds.add(result);
                        arrayAdapter.notifyDataSetChanged();
                    }else {
                        mDeviceIds.clear();
                        arrayAdapter.notifyDataSetChanged();
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
