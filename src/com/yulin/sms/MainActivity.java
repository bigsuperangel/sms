package com.yulin.sms;

import com.yulin.util.NetUtil;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if(!NetUtil.toggleWiFi(this,true)){
			NetUtil.toggleMobileData(this,true);
		}
	}
}
