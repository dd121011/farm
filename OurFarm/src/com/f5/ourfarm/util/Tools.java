package com.f5.ourfarm.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;

import com.baidu.location.LocationClientOption;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

/**
 * 工具类
 * 
 * @author tianhao
 */
public class Tools {
    
    /**
     * 到Url去下載圖片回傳BITMAP回來
     * 
     * @param imgUrl
     * @return Bitmap
     */
    public static Bitmap getBitmapFromUrl(String imgUrl) {
        URL url;
        Bitmap bitmap = null;
        try {
            url = new URL(imgUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setReadTimeout(5*1000);
            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            bitmap = BitmapFactory.decodeStream(bis);
            bis.close();
        } catch(java.net.UnknownHostException e){
            Log.e("没有可用的网络", e.toString());
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("获取网络图片失败", "网络异常，无法打开"+imgUrl+e.toString());
        } catch (Exception e) {
        	Log.e("获取网络图片失败", "其他异常", e);
        }
        return bitmap;
    }
    
    /**
     * 显示提示(短时间)
     * 
     * @param activity 上下文环境
     * @param content 显示的内容
     */
    public static void showToastShort(final Activity activity,final String content) {
        activity.runOnUiThread(new Runnable() {
            
            @Override
            public void run() {
                Toast toast = Toast.makeText(activity, content, Toast.LENGTH_SHORT);
                toast.show();
            }
        });
       
    }
    
    /**
     * 显示提示(长时间)
     * 
     * @param activity 上下文环境
     * @param content 显示的内容
     */
    public static void showToastLong(final Activity activity,final String content) {
    	activity.runOnUiThread(new Runnable() {
    		@Override
    		public void run() {
    			Toast toast = Toast.makeText(activity, content, Toast.LENGTH_LONG);
    			toast.show();
    		}
    	});
    }
    
    /**
     * 显示提示(长时间)
     * 
     * @param activity 上下文环境
     * @param content 显示的内容
     * @param seconds 毫秒数
     */
    @SuppressLint("ParserError")
	public static void showToastCustom(final Activity activity,final String content, final int seconds) {
    	activity.runOnUiThread(new Runnable() {
    		
    		@Override
    		public void run() {
    			Toast toast = Toast.makeText(activity, content, seconds);
    			toast.show();
    		}
    	});
    }
    
    /**
     * 判断当前网络是否可用
     * 
     * @param context
     * @return true:可用; false:不可用
     */
    public static boolean isNetworkAvailable(Context context) {  
 	    ConnectivityManager connectivityManager = 
 		    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE); 
 	    if(connectivityManager == null) {
 	    	return false;
 	    }
 	    NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();  
 	    if (activeNetInfo == null || !activeNetInfo.isAvailable()){  
     	    return false;  
        }  
        return true;  
    }
    
    /**
     * 判断当前网络是否为wifi
     * 
     * @param context
     * @return true:是; false:不是
     */
    public static boolean isWifi(Context context) {  
 	    ConnectivityManager connectivityManager = 
 		    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);  
 	    NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();  
 	    if (activeNetInfo != null  && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI){  
     	    return true;  
        }  
        return false;  
    }
    
    
    /**
     * 判断当前网络是否为3G
     * 
     * @param context
     * @return true:是; false:不是
     */
    public static boolean is3rd(Context context) {
        ConnectivityManager connectivityManager = 
        		(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
            return true;
        }
        return false;
    }
    
    /**
     * 检查是否有网络可用，不可用时进行提示消息
     * 
     * @param activity
     * @return true:可用; false:不可用
     */
    public static boolean checkNetworkStatus(final Activity activity) {
    	if(!Tools.isNetworkAvailable(activity)) {
    		Log.d("网络连接: ", "不可用");
    		Tools.showToastShort(activity, Constants.NETWORK_NOT_AVAILABLE);
    		return false;
    	}
    	Log.d("网络连接: ", "可用");
    	return true;
    }
    
    /**
     * 进度条计算用
     * 
     * @param loadCount 已加载个数
     * @param size 要加载的总个数
     * @return 加载百分比，比如是96.66%，则返回96
     */
    public static int getProcessValue(float loadCount, float size) {
    	return Math.round((loadCount)*100/size);
    }
    
    /**
     * 设置定位相关参数
     */
	public static LocationClientOption getLocationOption(){
        return InitLocationOption.option;
	}
    
	private static class InitLocationOption {
		static LocationClientOption option = null;
		static{
			option = new LocationClientOption();
	        option.setOpenGps(true);//打开gps
	        option.setAddrType("all");//返回的定位结果包含地址信息
	        option.setCoorType("bd09ll");//返回的定位结果是百度经纬度,默认值gcj02
	        option.setScanSpan(100);//设置发起定位请求的间隔时间为100ms。小于1秒则一次定位;大于等于1秒则定时定位
	        option.disableCache(true);//禁止启用缓存定位
	//        option.setPoiNumber(5);	//最多返回POI个数
	//        option.setPoiDistance(1000); //poi查询距离
	//        option.setPoiExtraInfo(true); //是否需要POI的电话和地址等详细信息
	        option.setPriority(LocationClientOption.NetWorkFirst);  //设置定位优先级
	        option.setProdName(Constants.APP_NAME); //设置产品线名称。强烈建议您使用自定义的产品线名称，方便我们以后为您提供更高效准确的定位服务。
		}
	}
	
    /**
     * 根据经纬度计算2点间距离，较粗糙
     * 
     * @param lng1
     * @param lat1
     * @param lng2
     * @param lat2
     * @return
     */
    public static double getDistance(double lng1,double lat1,double lng2,double lat2) {
      //将角度转为狐度
        double radLat1=deg2rad(lat1);
        double radLat2=deg2rad(lat2);
        double radLng1=deg2rad(lng1);
        double radLng2=deg2rad(lng2);
        double a=radLat1-radLat2;//两纬度之差,纬度<90
        double b=radLng1-radLng2;//两经度之差纬度<180
        double s=2*Math.asin(Math.sqrt(Math.pow(Math.sin(a/2),2)+Math.cos(radLat1)*Math.cos(radLat2)*Math.pow(Math.sin(b/2),2)))*6378.137;
        return s;
    }
    
    private static double deg2rad(double lat1) {
        return lat1/59;
    }
    
    /**
     * 根据经纬度计算2点间距离，远的返回km，近的返回m
     * 
     * @param lng1
     * @param lat1
     * @param lng2
     * @param lat2
     * @return
     */
    public static String getDistanceFormat(double lng1,double lat1,double lng2,double lat2) {
    	double dis = getDistance(lng1, lat1, lng2, lat2);
    	return getDistanceFormat(dis);
    }
    
    /**
     * 距离格式化，远的返回km，近的返回m
     * 
     * @param dis
     * @return
     */
    public static String getDistanceFormat(double dis) {
    	double aDis = Math.abs(dis);
    	if(aDis < 1) {
    		return new DecimalFormat("##0").format(aDis * 1000) + "m";
    	} else if(aDis < 10){
    		return new DecimalFormat("0.0").format(aDis) + "km";
    	} else {
    		return new DecimalFormat(",###").format(aDis) + "km";
    	}
    }
    
    /**
     * 字符串为null或空
     * 
     * @param cs
     * @return
     */
    public static boolean isEmpty(CharSequence cs) {
        return cs == null || cs.length() == 0;
    }
    
    /**
     * 字符串非空
     * 
     * @param cs
     * @return
     */
    public static boolean isNotEmpty(CharSequence cs) {
        return !isEmpty(cs);
    }
    
}
