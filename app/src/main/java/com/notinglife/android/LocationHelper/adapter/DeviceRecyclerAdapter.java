package com.notinglife.android.LocationHelper.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.notinglife.android.LocationHelper.R;
import com.notinglife.android.LocationHelper.domain.LocationDevice;
import com.notinglife.android.LocationHelper.view.DeviceViewHolder;

import java.util.List;

/**
 * ${DESCRIPTION}
 *
 * @author saginardo
 * @version ${VERSION}
 *          date 2017-06-08 19:45
 */

public class DeviceRecyclerAdapter extends RecyclerView.Adapter<DeviceViewHolder> {

    private Context mContext;
    private List<LocationDevice> mList;

    public DeviceRecyclerAdapter(Context context, List<LocationDevice> list) {
        this.mContext = context;
        this.mList = list;
    }

    @Override
    public DeviceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.device_list_item, parent, false);
        return new DeviceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DeviceViewHolder holder, int position) {
        LocationDevice locationDevice = mList.get(position);
        holder.mDeviceNumber.setText(locationDevice.mDeviceID);
        holder.mMacAddress.setText(locationDevice.mMacAddress);
        holder.mDeviceLatitude.setText(locationDevice.mLatitude);
        holder.mDeviceLongitude.setText(locationDevice.mLongitude);

    }

    @Override
    public int getItemCount() {
        if(mList==null){
            return 0;
        }
        return mList.size();
    }


}
