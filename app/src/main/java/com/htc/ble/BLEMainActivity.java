package com.htc.ble;

import java.util.ArrayList;
import java.util.List;


import android.app.Activity;
import android.bluetooth.*;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.neurosky.chrisblecentral.R;

public class BLEMainActivity extends Activity implements
		BluetoothAdapter.LeScanCallback {

	private BluetoothAdapter btAdapter;
	private BluetoothGatt gatt;
	private List<BluetoothGattService> serviceList;
	private List<BluetoothGattCharacteristic> characterList;
	private TextView textView;
	private Handler h;
	private ListView mDeviceList;
	private List<BluetoothDevice> listDevice=  new ArrayList<BluetoothDevice>();
	private MyAdapter mMyAdapter;
	private long ConnectingCount = 0;
	private  GattServerActivity mGattServerActivity;
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		this.setContentView(R.layout.ble_main);
		this.getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title);
		onInit();
	}

	void  onInit(){
		final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		btAdapter = bluetoothManager.getAdapter();
		textView =  findViewById(R.id.text);

		mDeviceList = findViewById(R.id.htc_device_list);
		mDeviceList.setBackgroundColor(Color.WHITE);
		mMyAdapter = new MyAdapter(BLEMainActivity.this, listDevice);
		mDeviceList.setAdapter(mMyAdapter);
		mDeviceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
		   	 	btAdapter.stopLeScan(BLEMainActivity.this);
				textView.setText("Stop Scan ...\n");
				BluetoothDevice mBluetoothDevice = listDevice.get(i);
				textView.append("Do  connect --->"+mBluetoothDevice.getName()+"\n");
				Message msg = new Message();
				msg.what = BluetoothProfile.STATE_CONNECTING;
				h.sendMessage(msg);
			gatt = mBluetoothDevice.connectGatt(BLEMainActivity.this, false, gattCallback);
			mDeviceList.setVisibility(View.INVISIBLE);

		}
		});

		h = new Handler() {

			public void handleMessage(Message msg) {
				// call update gui method.

				switch (msg.what) {
					case BluetoothProfile.STATE_CONNECTED:
						msg = new Message();
						msg.what = BluetoothProfile.STATE_CONNECTING;
						h.removeMessages(msg.what );
						textView.append("\n");
						textView.append("STATE_CONNECTED\n");
						break;
					case BluetoothProfile.STATE_DISCONNECTED:
						textView.append("STATE_DISCONNECTED\n");
						break;
					case BluetoothProfile.STATE_CONNECTING:
						ConnectingCount++;
						textView.append("-");
						if(ConnectingCount%(textView.getWidth()/textView.getPaint().measureText("-"))== 0)
							textView.append("\n");
						msg = new Message();
						msg.what = BluetoothProfile.STATE_CONNECTING;
						h.sendMessageDelayed(msg, 100);
						break;
					case BluetoothProfile.STATE_DISCONNECTING:
						textView.append("STATE_DISCONNECTING\n");
						break;
				}
			}
		};

		mGattServerActivity = new GattServerActivity();
		mGattServerActivity.initGattServer(bluetoothManager, h);
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	protected void onStop() {
		btAdapter.stopLeScan(this);
		super.onStop();
	}

	public void onScanButtonClicked(View v) {
		Log.d("Chris", "onButtonClicked");
		onStopAdver();
		Button mButton = findViewById(R.id.ble_scan);
		mDeviceList.setVisibility(View.VISIBLE);
		if(mButton.getText().equals("Start Scan")) {
			clearScanState();
			textView.setText("Client Mode:\n\n" );
			textView.append("start scan ---\n\n" );
			btAdapter.startLeScan(this);
			mDeviceList.setVisibility(View.VISIBLE);
			mButton.setText(R.string.ble_stop_scan);
		}else{
			textView.setText("Client Mode:\n\n" );
			textView.append("stop scan ---\n\n" );
			btAdapter.stopLeScan(this);
			mButton.setText(R.string.ble_start_scan);
		}

	}

	public  void onGattServerButtonClicked(View v){

		Log.d("Chris", "onGattServerButtonClicked");
		Button mButton = findViewById(R.id.ble_advertising);
		onStopScan();
		clearScanState();
		if(mButton.getText().equals("Start Advertising")) {
			mGattServerActivity.startAdvertising();
			mGattServerActivity.startServer();
			textView.setText("Server Mode:\n\n" );
			textView.append("Start Advertising\n");
			textView.append("Advertising Name: "+btAdapter.getName()+"\n"
						+"Advertising Address: "+btAdapter.getAddress()+"\n");
			mButton.setText(R.string.ble_stop_gatt_server);
		}else{
			mGattServerActivity.stopServer();
			mGattServerActivity.stopAdvertising();
			mButton.setText(R.string.ble_start_gatt_server);
			textView.setText("Server Mode:\n\n" );
			textView.append("Stop Advertising\n");
		}
	}

	private void clearScanState() {
		//Scan
		ConnectingCount = 0;
		btAdapter.stopLeScan(this);
		listDevice.clear();
		mMyAdapter.setList(listDevice);
		mMyAdapter.notifyDataSetChanged();
		mDeviceList.setVisibility(View.INVISIBLE);
		textView.setText("");

	}

	private  void onStopScan(){

		Button mButton = findViewById(R.id.ble_scan);
		if(!mButton.getText().equals("Start Scan")) {
			btAdapter.stopLeScan(this);
			mButton.setText(R.string.ble_start_scan);
		}
	}

	private  void onStopAdver(){
		//Advertising
		Button mButton = findViewById(R.id.ble_advertising);
		if(!mButton.getText().equals("Start Advertising")) {
			mGattServerActivity.stopServer();
			mGattServerActivity.stopAdvertising();
			mButton.setText(R.string.ble_start_gatt_server);
		//	textView.setText("Stop Advertising\n");
		}
		//textView.setText("");
	}

	@Override
	public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {

		if(!listDevice.contains(device)){

			listDevice.add(device);
			mMyAdapter.setList(listDevice);
			mMyAdapter.notifyDataSetChanged();
		}
		Log.d("Chris", "Device Name:" + device.getName());

	}

	private BluetoothGattCallback gattCallback = new BluetoothGattCallback() {

		@Override
		public void onConnectionStateChange(BluetoothGatt gatt, int status,
				int newState) {
			Log.d("Chris", "onConnectionStateChange");
			Message msg = new Message();
			msg.what = newState;
			h.sendMessage(msg);
			switch (newState) {
			case BluetoothProfile.STATE_CONNECTED:
				Log.d("Chris", "STATE_CONNECTED");
				gatt.discoverServices();

				break;
			case BluetoothProfile.STATE_DISCONNECTED:
				Log.d("Chris", "STATE_DISCONNECTED");
				gatt.disconnect();
				break;
			case BluetoothProfile.STATE_CONNECTING:
				Log.d("Chris", "STATE_CONNECTING");
				break;
			case BluetoothProfile.STATE_DISCONNECTING:
				Log.d("Chris", "STATE_DISCONNECTING");
				break;
			}
			super.onConnectionStateChange(gatt, status, newState);
		}

		@Override
		public void onServicesDiscovered(BluetoothGatt gatt, int status) {
			Log.d("Chris", "onServicesDiscovered");
			if (status == BluetoothGatt.GATT_SUCCESS) {
				serviceList = gatt.getServices();
				for (int i = 0; i < serviceList.size(); i++) {
					BluetoothGattService theService = serviceList.get(i);
					Log.d("Chris", "ServiceName:" + theService.getUuid());

					characterList = theService.getCharacteristics();
					for (int j = 0; j < characterList.size(); j++) {
						Log.d("Chris",
								"---CharacterName:"
										+ characterList.get(j).getUuid());
					}
				}
			}
			super.onServicesDiscovered(gatt, status);
		}

		@Override
		public void onCharacteristicRead(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic, int status) {
			Log.d("Chris", "onCharacteristicRead");
			super.onCharacteristicRead(gatt, characteristic, status);
		}

		@Override
		public void onCharacteristicWrite(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic, int status) {
			Log.d("Chris", "onCharacteristicWrite");
			super.onCharacteristicWrite(gatt, characteristic, status);
		}

		@Override
		public void onCharacteristicChanged(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic) {
			Log.d("Chris", "onCharacteristicChanged");
			super.onCharacteristicChanged(gatt, characteristic);
		}

		@Override
		public void onDescriptorRead(BluetoothGatt gatt,
				BluetoothGattDescriptor descriptor, int status) {
			Log.d("Chris", "onDescriptorRead");
			super.onDescriptorRead(gatt, descriptor, status);
		}

		@Override
		public void onDescriptorWrite(BluetoothGatt gatt,
				BluetoothGattDescriptor descriptor, int status) {
			Log.d("Chris", "onDescriptorWrite");
			super.onDescriptorWrite(gatt, descriptor, status);
		}

		@Override
		public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
			Log.d("Chris", "onReliableWriteCompleted");
			super.onReliableWriteCompleted(gatt, status);
		}

		@Override
		public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
			Log.d("Chris", "onReadRemoteRssi");
			super.onReadRemoteRssi(gatt, rssi, status);
		}

	};

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

}
