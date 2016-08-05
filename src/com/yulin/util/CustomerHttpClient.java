package com.yulin.util;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.util.Log;

/**
 * HttpClient工具类
 */
public class CustomerHttpClient {

	/**
	 * 处理客户端发起的HTTP请求，调用方式
	 * final String url = "http://yourdomain/context/adduser";
	 * NameValuePair param1 = new BasicNameValuePair("username", "张三");
　　   * NameValuePair param2 = new BasicNameValuePair("password", "123456");
	 * 使用工具类直接发出POST请求,服务器返回json数据，比如"{userid:12}"
　　   * String response = CustomerHttpClient.post(url, param1, param2);
　　   * JSONObject root = new JSONObject(response);
　　   * resultId = Integer.parseInt(root.getString("userid"));
	 */
	private static final String TAG = "CustomerHttpClient";
//	private static final String CHARSET = HTTP.UTF_8;
	private static final String CHARSET = "gbk";
    private static HttpClient customerHttpClient;

    private CustomerHttpClient() {
    }

    public static synchronized HttpClient getHttpClient() {
        if (null == customerHttpClient) {
            HttpParams params = new BasicHttpParams();
            // 设置一些基本参数
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params,
                    CHARSET);
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
            customerHttpClient = new DefaultHttpClient(conMgr, params);
        }
        return customerHttpClient;
    }
    
    public static String get(String url){
   
         // 打印请求信息   
         try {   
        	 // 核心应用类   
        	 HttpClient httpClient = getHttpClient();
        	 
        	 // HTTP请求   
        	 HttpUriRequest request =   new HttpGet(url);   
             // 发送请求，返回响应   
             HttpResponse response = httpClient.execute(request);   
   
             // 打印响应信息   
             if(response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                 throw new RuntimeException("请求失败");
             }
             HttpEntity resEntity =  response.getEntity();
             Log.i(TAG, EntityUtils.toString(resEntity, CHARSET));
             return (resEntity == null) ? null : EntityUtils.toString(resEntity, CHARSET);
         } catch (ClientProtocolException e) {
             Log.w(TAG, e.getMessage());
             return null;
         } catch (IOException e) {
             throw new RuntimeException("连接失败", e);
         }
    }

    /**
     * request-line方式是指在请求行上通过URI直接提供参数。
     * @param url
     * @param params
     * @return
     */
    public static String post(String url, NameValuePair... params) {
        try {
            // 编码参数
            List<NameValuePair> formparams = new ArrayList<NameValuePair>(); // 请求参数
            for (NameValuePair p : params) {
                formparams.add(p);
            }
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams,
                    CHARSET);
            // 创建POST请求
            HttpPost request = new HttpPost(url);
            request.setEntity(entity);
            // 发送请求
            HttpClient client = getHttpClient();
            Log.i(TAG, EntityUtils.toString(entity));
            HttpResponse response = client.execute(request);
            if(response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                throw new RuntimeException("请求失败");
            }
            HttpEntity resEntity =  response.getEntity();
//            Log.i(TAG, EntityUtils.toString(resEntity, CHARSET));
            return (resEntity == null) ? null : EntityUtils.toString(resEntity, CHARSET);
        } catch (UnsupportedEncodingException e) {
            Log.w(TAG, e.getMessage());
            return null;
        } catch (ClientProtocolException e) {
            Log.w(TAG, e.getMessage());
            return null;
        } catch (IOException e) {
            throw new RuntimeException("连接失败", e);
        }
    }
    
    /**
     * request-body
		与request-line方式不同，request-body方式是在request-body中提供参数，
		此方式只能用于POST请求。在HttpClient程序包中有两个类可以完成此项工作，
		它们分别是UrlEncodedFormEntity类与MultipartEntity类。这两个类均实现了HttpEntity接口。
     * @param host
     * @param path
     * @param params
     * @return
     */
    public static String post(String host,String path, List<NameValuePair> params) {
        try {
            // 编码参数
        	String formparams = URLEncodedUtils.format(params, CHARSET);
        	URI uri = URIUtils.createURI("http", host, 80,path, formparams, null);
            // 创建POST请求
            HttpPost request = new HttpPost(uri);
            // 发送请求
            HttpClient client = getHttpClient();
            HttpResponse response = client.execute(request);
            if(response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                throw new RuntimeException("请求失败");
            }
            HttpEntity resEntity =  response.getEntity();
            Log.i(TAG, EntityUtils.toString(resEntity, CHARSET));
            return (resEntity == null) ? null : EntityUtils.toString(resEntity, CHARSET);
        } catch (UnsupportedEncodingException e) {
            Log.w(TAG, e.getMessage());
            return null;
        } catch (ClientProtocolException e) {
            Log.w(TAG, e.getMessage());
            return null;
        } catch (IOException e) {
            throw new RuntimeException("连接失败", e);
        } catch (URISyntaxException e) {
        	Log.w(TAG, e.getMessage());
            return null;
		}
    }
    
    /*
     * 上传文件至Server的方法 
     * newName ="image.jpg";
     * uploadFile ="/sdcard/image.JPG";
     * actionUrl ="http://192.168.0.71:8086/HelloWord/myForm";
     */
    public static String uploadFile(String actionUrl, String newName, String uploadFile)
    {
      String end ="\r\n";
      String twoHyphens ="--";
      String boundary ="*****";
      String result = null;
      try
      {
        URL url =new URL(actionUrl);
        HttpURLConnection con=(HttpURLConnection)url.openConnection();
        /* 允许Input、Output，不使用Cache */
        con.setDoInput(true);
        con.setDoOutput(true);
        con.setUseCaches(false);
        /* 设置传送的method=POST */
        con.setRequestMethod("POST");
        /* setRequestProperty */
        con.setRequestProperty("Connection", "Keep-Alive");
        con.setRequestProperty("Charset", "UTF-8");
        con.setRequestProperty("Content-Type",
                           "multipart/form-data;boundary="+boundary);
        /* 设置DataOutputStream */
        DataOutputStream ds =
          new DataOutputStream(con.getOutputStream());
        ds.writeBytes(twoHyphens + boundary + end);
        ds.writeBytes("Content-Disposition: form-data; "+
                      "name=\"file1\";filename=\""+
                      newName +"\""+ end);
        ds.writeBytes(end);  
        /* 取得文件的FileInputStream */
        FileInputStream fStream =new FileInputStream(uploadFile);
        /* 设置每次写入1024bytes */
        int bufferSize =1024;
        byte[] buffer =new byte[bufferSize];
        int length =-1;
        /* 从文件读取数据至缓冲区 */
        while((length = fStream.read(buffer)) !=-1)
        {
          /* 将资料写入DataOutputStream中 */
          ds.write(buffer, 0, length);
        }
        ds.writeBytes(end);
        ds.writeBytes(twoHyphens + boundary + twoHyphens + end);
        /* close streams */
        fStream.close();
        ds.flush();
        /* 取得Response内容 */
        InputStream is = con.getInputStream();
        int ch;
        StringBuffer b =new StringBuffer();
        while( ( ch = is.read() ) !=-1 )
        {
          b.append( (char)ch );
        }
        result = b.toString().trim();
        /* 关闭DataOutputStream */
        ds.close();
      }
      catch(Exception e)
      {
    	  e.printStackTrace();
      }
      return result;
    }

}
