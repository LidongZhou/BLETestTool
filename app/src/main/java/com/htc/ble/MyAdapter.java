package com.htc.ble;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import android.widget.TextView;


import com.neurosky.chrisblecentral.R;

import java.util.List;


public class MyAdapter extends BaseAdapter {
	private List<BluetoothDevice> arrays = null;
	private Context mContext;

	public MyAdapter(Context mContext, List<BluetoothDevice> arrays) {
		this.mContext = mContext;
		this.arrays = arrays;
	}

	public void setList(List<BluetoothDevice> arrays){

		this.arrays = arrays;
	}

	@Override
	public int getCount() {
		return this.arrays.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View view, ViewGroup arg2) {
		ViewHolder viewHolder = null;
		if (view == null) {
			viewHolder = new ViewHolder();
			view = LayoutInflater.from(mContext).inflate(R.layout.device_list_item, null);

			viewHolder.deviceName = (TextView) view.findViewById(R.id.device_name);
			viewHolder.deviceAddress = (TextView) view.findViewById(R.id.device_address);

			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}



		viewHolder.deviceName.setText(arrays.get(position).getName());
		viewHolder.deviceAddress.setText(arrays.get(position).getAddress()
				.toString());

		return view;
	}

	final static class ViewHolder {
		TextView deviceName;
		TextView deviceAddress;
	}
}