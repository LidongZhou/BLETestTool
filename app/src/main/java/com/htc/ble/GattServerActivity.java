package com.htc.ble;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;

import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import com.neurosky.chrisblecentral.R;

public class GattServerActivity extends Activity {
	private static final String TAG = GattServerActivity.class.getSimpleName();

	private BluetoothManager mBluetoothManager;
	private BluetoothGattServer mBluetoothGattServer;
	private BluetoothLeAdvertiser mBluetoothLeAdvertiser;
	private Handler h;
	/* Collection of notification subscribers */

	private TextView mTextView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	public void  initGattServer(BluetoothManager mBluetoothManager, Handler h){
		this.mBluetoothManager 	=mBluetoothManager;
		this.h = h;

	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	/**
	 * Begin advertising over Bluetooth that this device is connectable
	 * and supports the Current Time Service.
	 */
	public void startAdvertising() {
		BluetoothAdapter bluetoothAdapter = mBluetoothManager.getAdapter();
		mBluetoothLeAdvertiser = bluetoothAdapter.getBluetoothLeAdvertiser();
		if (mBluetoothLeAdvertiser == null) {
			Log.w(TAG, "Failed to create advertiser");
			return;
		}

		AdvertiseSettings settings = new AdvertiseSettings.Builder()
				.setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED)
				.setConnectable(true)
				.setTimeout(0)
				.setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM)
				.build();
		AdvertiseData data = new AdvertiseData.Builder()
				.setIncludeDeviceName(true)
				.setIncludeTxPowerLevel(false)
				.build();

		mBluetoothLeAdvertiser
				.startAdvertising(settings, data, mAdvertiseCallback);
	}

	/**
	 * Stop Bluetooth advertisements.
	 */
	public void stopAdvertising() {
		if (mBluetoothLeAdvertiser == null) return;

		mBluetoothLeAdvertiser.stopAdvertising(mAdvertiseCallback);
	}

	/**
	 * Initialize the GATT server instance with the services/characteristics
	 * from the Time Profile.
	 */
	public void startServer() {
		mBluetoothGattServer = mBluetoothManager.openGattServer(this, mGattServerCallback);
		if (mBluetoothGattServer == null) {
			Log.w(TAG, "Unable to create GATT server");
			return;
		}

	}

	/**
	 * Shut down the GATT server.
	 */
	public void stopServer() {
		if (mBluetoothGattServer == null) return;

		mBluetoothGattServer.close();
	}

	/**
	 * Callback to receive information about the advertisement process.
	 */
	private AdvertiseCallback mAdvertiseCallback = new AdvertiseCallback() {
		@Override
		public void onStartSuccess(AdvertiseSettings settingsInEffect) {
			Log.i(TAG, "LE Advertise Started.");
		}

		@Override
		public void onStartFailure(int errorCode) {
			Log.w(TAG, "LE Advertise Failed: "+errorCode);
		}
	};



	/**
	 * Callback to handle incoming requests to the GATT server.
	 * All read/write requests for characteristics and descriptors are handled here.
	 */
	private BluetoothGattServerCallback mGattServerCallback = new BluetoothGattServerCallback() {

		@Override
		public void onConnectionStateChange(BluetoothDevice device, int status, int newState) {
			if (newState == BluetoothProfile.STATE_CONNECTED) {
				Message msg = new Message();
				msg.what = BluetoothProfile.STATE_CONNECTED;
				if(h != null)
					h.sendMessage(msg);
				Log.i(TAG, "BluetoothDevice11 CONNECTED: " + device);

			} else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
				Message msg = new Message();
				msg.what = BluetoothProfile.STATE_DISCONNECTED;
				if(h != null)
					h.sendMessage(msg);
				Log.i(TAG, "BluetoothDevice11 DISCONNECTED: " + device);

			}
		}

		@Override
		public void onCharacteristicReadRequest(BluetoothDevice device, int requestId, int offset,
												BluetoothGattCharacteristic characteristic) {
		}

		@Override
		public void onDescriptorReadRequest(BluetoothDevice device, int requestId, int offset,
											BluetoothGattDescriptor descriptor) {

		}

		@Override
		public void onDescriptorWriteRequest(BluetoothDevice device, int requestId,
											 BluetoothGattDescriptor descriptor,
											 boolean preparedWrite, boolean responseNeeded,
											 int offset, byte[] value) {

		}
	};
}
