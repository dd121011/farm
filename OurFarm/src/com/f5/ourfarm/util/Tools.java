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
 * ������
 * 
 * @author tianhao
 */
public class Tools {
    
    /**
     * ��Urlȥ���d�DƬ�؂�BITMAP�؁�
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
            Log.e("û�п��õ�����", e.toString());
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("��ȡ����ͼƬʧ��", "�����쳣���޷���"+imgUrl+e.toString());
        } catch (Exception e) {
        	Log.e("��ȡ����ͼƬʧ��", "�����쳣", e);
        }
        return bitmap;
    }
    
    /**
     * ��ʾ��ʾ(��ʱ��)
     * 
     * @param activity �����Ļ���
     * @param content ��ʾ������
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
     * ��ʾ��ʾ(��ʱ��)
     * 
     * @param activity �����Ļ���
     * @param content ��ʾ������
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
     * ��ʾ��ʾ(��ʱ��)
     * 
     * @param activity �����Ļ���
     * @param content ��ʾ������
     * @param seconds ������
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
     * �жϵ�ǰ�����Ƿ����
     * 
     * @param context
     * @return true:����; false:������
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
     * �жϵ�ǰ�����Ƿ�Ϊwifi
     * 
     * @param context
     * @return true:��; false:����
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
     * �жϵ�ǰ�����Ƿ�Ϊ3G
     * 
     * @param context
     * @return true:��; false:����
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
     * ����Ƿ���������ã�������ʱ������ʾ��Ϣ
     * 
     * @param activity
     * @return true:����; false:������
     */
    public static boolean checkNetworkStatus(final Activity activity) {
    	if(!Tools.isNetworkAvailable(activity)) {
    		Log.d("��������: ", "������");
    		Tools.showToastShort(activity, Constants.NETWORK_NOT_AVAILABLE);
    		return false;
    	}
    	Log.d("��������: ", "����");
    	return true;
    }
    
    /**
     * ������������
     * 
     * @param loadCount �Ѽ��ظ���
     * @param size Ҫ���ص��ܸ���
     * @return ���ذٷֱȣ�������96.66%���򷵻�96
     */
    public static int getProcessValue(float loadCount, float size) {
    	return Math.round((loadCount)*100/size);
    }
    
    /**
     * ���ö�λ��ز���
     */
	public static LocationClientOption getLocationOption(){
        return InitLocationOption.option;
	}
    
	private static class InitLocationOption {
		static LocationClientOption option = null;
		static{
			option = new LocationClientOption();
	        option.setOpenGps(true);//��gps
	        option.setAddrType("all");//���صĶ�λ���������ַ��Ϣ
	        option.setCoorType("bd09ll");//���صĶ�λ����ǰٶȾ�γ��,Ĭ��ֵgcj02
	        option.setScanSpan(100);//���÷���λ����ļ��ʱ��Ϊ100ms��С��1����һ�ζ�λ;���ڵ���1����ʱ��λ
	        option.disableCache(true);//��ֹ���û��涨λ
	//        option.setPoiNumber(5);	//��෵��POI����
	//        option.setPoiDistance(1000); //poi��ѯ����
	//        option.setPoiExtraInfo(true); //�Ƿ���ҪPOI�ĵ绰�͵�ַ����ϸ��Ϣ
	        option.setPriority(LocationClientOption.NetWorkFirst);  //���ö�λ���ȼ�
	        option.setProdName(Constants.APP_NAME); //���ò�Ʒ�����ơ�ǿ�ҽ�����ʹ���Զ���Ĳ�Ʒ�����ƣ����������Ժ�Ϊ���ṩ����Ч׼ȷ�Ķ�λ����
		}
	}
	
    /**
     * ���ݾ�γ�ȼ���2�����룬�ϴֲ�
     * 
     * @param lng1
     * @param lat1
     * @param lng2
     * @param lat2
     * @return
     */
    public static double getDistance(double lng1,double lat1,double lng2,double lat2) {
      //���Ƕ�תΪ����
        double radLat1=deg2rad(lat1);
        double radLat2=deg2rad(lat2);
        double radLng1=deg2rad(lng1);
        double radLng2=deg2rad(lng2);
        double a=radLat1-radLat2;//��γ��֮��,γ��<90
        double b=radLng1-radLng2;//������֮��γ��<180
        double s=2*Math.asin(Math.sqrt(Math.pow(Math.sin(a/2),2)+Math.cos(radLat1)*Math.cos(radLat2)*Math.pow(Math.sin(b/2),2)))*6378.137;
        return s;
    }
    
    private static double deg2rad(double lat1) {
        return lat1/59;
    }
    
    /**
     * ���ݾ�γ�ȼ���2�����룬Զ�ķ���km�����ķ���m
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
     * �����ʽ����Զ�ķ���km�����ķ���m
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
     * �ַ���Ϊnull���
     * 
     * @param cs
     * @return
     */
    public static boolean isEmpty(CharSequence cs) {
        return cs == null || cs.length() == 0;
    }
    
    /**
     * �ַ����ǿ�
     * 
     * @param cs
     * @return
     */
    public static boolean isNotEmpty(CharSequence cs) {
        return !isEmpty(cs);
    }
    
}
