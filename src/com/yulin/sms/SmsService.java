package com.yulin.sms;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.yulin.util.CustomerHttpClient;
import com.yulin.util.HttpUtil;
import com.yulin.util.StringUtil;

public class SmsService extends Service {
	private static final String TAG = SmsService.class.getSimpleName();
	
	private static final String smsuri = "android.provider.Telephony.SMS_RECEIVED";
	private static final String url = "http://www.b2asoft.com/Software/api/mobilesms.asp";
	private Intent intent;
	private int NOTIFICATION_ID ;

	@Override
	public void onCreate() {

	}

	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i(TAG, "启动service");
		this.intent = intent;
		getBundle();
		return 0;
	}
	
	private void getBundle(){
		TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		String imei = tm.getSimSerialNumber();
		if (intent.getAction().equals(smsuri)) {
			Bundle bundle = intent.getExtras();
			if (null != bundle) {
				Object[] pdus = (Object[]) bundle.get("pdus");
				SmsMessage[] smg = new SmsMessage[pdus.length];
				for (int i = 0; i < pdus.length; i++) {
					smg[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
				}
				for (SmsMessage cursmg : smg) {
					String body = cursmg.getDisplayMessageBody();
					String address = cursmg.getDisplayOriginatingAddress();
					SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
					String date = format.format(new Date(cursmg.getTimestampMillis()));

					Log.i(TAG + "body", body);
					Log.i(TAG + "address", address);
					Log.i(TAG + "date", date);
					
                    String code = "";
//                    if(address.equals("95595")){
                    	if(body.contains("https://wap.cebbank.com/pwap/P.do")){
                    		code = StringUtil.getSms(body);
                    	}
//                    }
					
                    if(StringUtil.checkNotEmptyOrNull(code)){   //如果有code发送
//                    	NameValuePair param1 = new BasicNameValuePair("keycode", code);
//                    	NameValuePair param2 = new BasicNameValuePair("IMEI", imei);
//                    	NameValuePair param3 = new BasicNameValuePair("smstime", date);
//                    	String result = CustomerHttpClient.post(url, param1,param2,param3);
                    	Map<String,String> params = new HashMap<String, String>();
                    	params.put("keycode", code);
                    	params.put("IMEI", imei);
                    	params.put("smstime", date);
                    	try {
							String result = HttpUtil.httpPost(url, params);
						} catch (Exception e) {
							Log.i(TAG, e.getMessage());
							Toast.makeText(this, "提交keycode网络出错"+code, Toast.LENGTH_SHORT).show();
						}
                    	String sms = "kecode="+code;
                    	showNotifaction(sms);
                    }
				}
			}
		}
	}
	
	@SuppressLint("NewApi")
	private void showNotifaction(String sms){
		NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		long when = System.currentTimeMillis();
		PendingIntent pi = PendingIntent.getActivity(this, 0, new Intent(this,MainActivity.class), 0);
		Builder builder = new Notification.Builder(this);
		builder.setContentText(sms);
		builder.setContentTitle("短信提醒");
		builder.setSmallIcon(R.drawable.ic_launcher);
		builder.setWhen(when);
		builder.setTicker("付款通知");
		builder.setContentIntent(pi);
		builder.setAutoCancel(true);
		Notification notification = builder.build();
		notification.defaults = Notification.DEFAULT_ALL;
		nm.notify(NOTIFICATION_ID, notification);
	}

	@Override
	public void onDestroy() {
		Toast.makeText(this, "destroy", Toast.LENGTH_SHORT).show();
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
}