package com.yulin.util;

import android.annotation.SuppressLint;

public class StringUtil {
	@SuppressLint("NewApi")
	public static boolean checkNotEmptyOrNull(String str){
		return str!=null && !str.isEmpty();
	}
	
	public static String getSms(String body){
		if(body.indexOf("=")>0 && body.indexOf("。")>0){
			return body.substring(body.indexOf("=")+1,body.indexOf("。"));
		}
		return "";
	}
	
}
