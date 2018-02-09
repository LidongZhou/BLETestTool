package com.htc.ble;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import java.util.Timer;


public class IndexActivity extends Activity {

//	private int index = 0;
//	private Timer timer;
	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
		//	timer.cancel();
			Intent intent = new Intent(IndexActivity.this,
					BLEMainActivity.class);
			startActivity(intent);
			finish();
			super.handleMessage(msg);
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestReadExternalPermission();
		// setContentView(R.layout.index_main);

		//  ImageView myImage = (ImageView) findViewById(R.id.index_animation);
		/*myImage.setBackgroundResource(R.anim.index_anim);
		AnimationDrawable frameAnimation = (AnimationDrawable) myImage.getBackground();
		frameAnimation.start();*/

		//      myImage.setBackgroundResource(R.drawable.index_icon);
/*            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    index = index+1;
                    handler.sendEmptyMessage(0);
                }
            },2000,1000);
        }*/


	}


	@SuppressLint("NewApi")
	public void requestReadExternalPermission() {
		if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
				!= PackageManager.PERMISSION_GRANTED) {

			if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)) {

			} else {
				// 0 是自己定义的请求coude
				requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
			}
		} else {
			handler.sendEmptyMessage(0);
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

		switch (requestCode) {
			case 0: {

				if (grantResults.length > 0
						&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {

					// permission was granted
					// request successfully, handle you transactions
					handler.sendEmptyMessage(0);
				} else {

					// permission denied
					// request failed
				}

				return;
			}
			default:
				break;

		}
	}

}
