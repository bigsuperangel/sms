package com.yulin.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EntityUtils;

import android.util.Log;

public class HttpUtil {

	private static final String TAG = "HttpUtil";
	// private static final String CHARSET = HTTP.UTF_8;
	private static final String CHARSET = "gbk";
	private static HttpClient httpClient;
	
	static{
		HttpParams params = new BasicHttpParams();
		// 设置一些基本参数
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(params, CHARSET);
		HttpProtocolParams.setUseExpectContinue(params, true);
		HttpProtocolParams
				.setUserAgent(
						params,
						"Mozilla/5.0(Linux;U;Android 2.2.1;en-us;Nexus One Build.FRG83) "
								+ "AppleWebKit/553.1(KHTML,like Gecko) Version/4.0 Mobile Safari/533.1");
		// 超时设置
		/* 从连接池中取连接的超时时间 */
		ConnManagerParams.setTimeout(params, 30000);
		/* 连接超时 */
		HttpConnectionParams.setConnectionTimeout(params, 30000);
		/* 请求超时 */
		HttpConnectionParams.setSoTimeout(params, 30000);

		// 设置我们的HttpClient支持HTTP和HTTPS两种模式
		SchemeRegistry schReg = new SchemeRegistry();
		schReg.register(new Scheme("http", PlainSocketFactory
				.getSocketFactory(), 80));
		schReg.register(new Scheme("https", SSLSocketFactory
				.getSocketFactory(), 443));

		// 使用线程安全的连接管理来创建HttpClient
		ClientConnectionManager conMgr = new ThreadSafeClientConnManager(
				params, schReg);
		httpClient = new DefaultHttpClient(conMgr, params);
	}


	public static String httpGet(final String url) throws Exception {
		FutureTask<String> task = new FutureTask<String>(
				new Callable<String>() {

					@Override
					public String call() throws Exception {
						// HTTP请求
						HttpUriRequest request = new HttpGet(url);
						// 发送请求，返回响应
						HttpResponse response = httpClient.execute(request);

						// 打印响应信息
						if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
							throw new RuntimeException("请求失败");
						}
						HttpEntity resEntity = response.getEntity();
						Log.i(TAG, EntityUtils.toString(resEntity, CHARSET));
						return (resEntity == null) ? null : EntityUtils
								.toString(resEntity, CHARSET);
					}

				});
		new Thread(task).start();
		return task.get();
	}

	public static String httpPost(final String url,final Map<String, String> rowParams) throws Exception{
		FutureTask<String> task = new FutureTask<String>(
				new Callable<String>() {

					@Override
					public String call() throws Exception {
						List<NameValuePair> formparams = new ArrayList<NameValuePair>(); // 请求参数
						HttpPost request = new HttpPost(url);
						for (String key : rowParams.keySet()) {
							formparams.add(new BasicNameValuePair(key,
									rowParams.get(key)));
						}
						UrlEncodedFormEntity entity = new UrlEncodedFormEntity(
								formparams, CHARSET);
						// 创建POST请求
						request.setEntity(entity);
						// 发送请求
						Log.i(TAG, EntityUtils.toString(entity));
						HttpResponse response = httpClient.execute(request);
						if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
							throw new RuntimeException("请求失败");
						}
						HttpEntity resEntity = response.getEntity();
						return (resEntity == null) ? null : EntityUtils
								.toString(resEntity, CHARSET);
					}
				});
		new Thread(task).start();
		return task.get();
	}
}
