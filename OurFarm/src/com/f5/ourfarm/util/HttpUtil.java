package com.f5.ourfarm.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

/**
 * http请求工具类
 * @author lify
 *
 */
public class HttpUtil {

    /** 
     * Get方式请求，默认返回的数据为UTF-8编码 
     *  
     * @param url 请求地址 
     * @param method  提交方式 
     * @return 返回内容
     * @throws IOException 
     */  
    public static String getUrl(String url) throws IOException {
        HttpGet request = new HttpGet(url);
        HttpClient httpClient = new DefaultHttpClient();
        HttpResponse response = httpClient.execute(request);
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            return EntityUtils.toString(response.getEntity());
        }
        
        return null;
    }
    
    /** 
     * Post方式请求 
     *  
     * @param url 提交地址 
     * @param params 参数 
     * 
     * @return 返回内容
     * @throws ClientProtocolException 
     * @throws IOException 
     */  
    public static String postUrl(String url, List<NameValuePair> params)
            throws ClientProtocolException, IOException {
        return postUrl(url, params, "UTF-8");
    }
    
    /** 
     * post提交数据 
     *  
     * @param url 提交地址 
     * @param params 参数 
     * @param encoding 参数编码 
     * @return 返回内容
     * @throws ClientProtocolException 
     * @throws IOException 
     */  
    public static String postUrl(String url, List<NameValuePair> params,
            String encoding) throws ClientProtocolException, IOException {
  
        HttpPost request = new HttpPost(url);
        request.setEntity(new UrlEncodedFormEntity(params, encoding));
  
        HttpClient client = new DefaultHttpClient();
        client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000); // 设置请求超时时间
        client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000); // 读取超时
  
        HttpResponse response = client.execute(request);
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            StringBuffer sb = new StringBuffer();
            InputStream  is = response.getEntity().getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is,"utf-8"));  
            String tempBf = null; 
            while((tempBf=reader.readLine()) != null){  
                sb.append(tempBf) ;
            }  
            reader.close();
            is.close();
            return sb.toString();
        }
        
        return null;
    }
}
