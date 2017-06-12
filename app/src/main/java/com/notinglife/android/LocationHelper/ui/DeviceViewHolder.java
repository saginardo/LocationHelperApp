package com.notinglife.android.LocationHelper.ui;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.notinglife.android.LocationHelper.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * ${DESCRIPTION}
 *
 * @author saginardo
 * @version ${VERSION}
 *          date 2017-06-11 19:58
 */

public class DeviceViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.tv_device_number)
    public TextView mDeviceNumber;
    @BindView(R.id.tv_device_lat_info)
    public TextView mDeviceLatitude;
    @BindView(R.id.tv_device_lng_info)
    public TextView mDeviceLongitude;
    @BindView(R.id.tv_device_macAddress)
    public TextView mMacAddress;

    @BindView(R.id.cv_item)
    public CardView cv_item;
    @BindView(R.id.item_delete_txt)
    public TextView mDeleteItem;
    @BindView(R.id.ll_item_main)
    LinearLayout mLinearLayout;

    public DeviceViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);

    }
}
