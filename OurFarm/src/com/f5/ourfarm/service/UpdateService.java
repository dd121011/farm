/**
 * 
 */
package com.f5.ourfarm.service;

import static com.f5.ourfarm.util.URLConstants.CLASSIC_URL;
import static com.f5.ourfarm.util.URLConstants.NEARBY_DETAIL_URL;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.f5.ourfarm.activity.HomeMoreActivity;
import com.f5.ourfarm.model.Destination;
import com.f5.ourfarm.model.Summary;
import com.f5.ourfarm.sqlite.DestinationDbAdpter;
import com.f5.ourfarm.util.Constants;
import com.f5.ourfarm.util.HttpUtil;
import com.f5.ourfarm.util.Tools;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

/**
 * @author tianhao
 *
 */
public class UpdateService extends Service {
    
  //定义个一个Tag标签  
    private static final String TAG = "UpdateService";  
    
    private static final String errMsg = "操作失败";
    
    //数据库连接
    DestinationDbAdpter destinationDbAdpter;
    
  //这里定义吧一个Binder类，用在onBind()有方法里，这样Activity那边可以获取到  
    private MyBinder mBinder = new MyBinder(); 

    /* (non-Javadoc)
     * @see android.app.Service#onBind(android.content.Intent)
     */
    @Override
    public IBinder onBind(Intent arg0) {
        Log.i(TAG, "start IBinder~~~");  
        return mBinder;  
    }

    @Override  
    public void onCreate() {  
      //数据库初始化
        destinationDbAdpter = new DestinationDbAdpter(this);
        destinationDbAdpter.open();
        Log.i(TAG, "start onCreate~~~"); 
        super.onCreate();  
    }  
      
    @Override  
    public void onStart(Intent intent, int startId) {  
        Log.i(TAG, "start onStart~~~");  
        super.onStart(intent, startId);  
        updateClassic();
    }  
      
    @Override  
    public void onDestroy() {  
        Log.i(TAG, "start onDestroy~~~");  
        super.onDestroy();  
    }  
      
      
    @Override  
    public boolean onUnbind(Intent intent) {  
        Log.i(TAG, "start onUnbind~~~");  
        return super.onUnbind(intent);  
    }  
    
    //更新classic数据
    public void updateClassic(){ 
        Log.i(TAG, "start update"); 
        new Thread(update).start();
    } 
    
    public class MyBinder extends Binder{  
        public UpdateService getService()  
        {  
            return UpdateService.this;  
        }  
    }  
    
    Handler handler4update = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            int res = data.getInt("value");
            Log.d(TAG,"请求结果为-->" + res);
            
//            progressDialog.dismiss(); 
//            Tools.showToastShort(HomeMoreActivity.this, Constants.MORE_UPDATE_DATA);
        }
    };
    
    
    Runnable update = new Runnable(){
        @Override
        public void run() {
            try {
                int time = 10 ;//请求次数
                for(int t= 1;t<time;t++){
                    List<NameValuePair> param = new ArrayList<NameValuePair>();
                    BasicNameValuePair count  = new BasicNameValuePair("count", String.valueOf(t));
                    param.add(count);
                    String res = null;
                    res = HttpUtil.postUrl(CLASSIC_URL, param);
                    List<Summary> listSummary = null;
                    Gson gson = null;
                    gson = new Gson();
                    // 获取经典内容的概要
                    listSummary = gson.fromJson(res,
                            new TypeToken<List<Summary>>() {
                            }.getType());
                    // 遍历下载信息，保存到本地DB
                    Iterator<Summary> iterator = listSummary.iterator();
                    while (iterator.hasNext()) {
                        Summary summary = iterator.next();
                        long destinationId = summary.getDestinationId();
                        param = new ArrayList<NameValuePair>();
                        BasicNameValuePair id = new BasicNameValuePair(
                                "destinationId", Long.toString(destinationId));
                        param.add(id);

                        String res4detail = HttpUtil.postUrl(NEARBY_DETAIL_URL,
                                param);
                        Destination destination = gson.fromJson(res4detail,
                                new TypeToken<Destination>() {
                                }.getType());
                        // 本地存储 , 改为FALSE
                        destinationDbAdpter.insertDestination(destination, false);
                        /** 加载图片 */
                        destinationDbAdpter.insertImage(destinationId, 1, false,
                                summary.getPic());
                        String[] urls = destination.getPics();
                        for (int i = 0; i < urls.length; i++) {
                            destinationDbAdpter.insertImage(destinationId, 1, true,
                                    urls[i]);
                        }
                    } 
                }
                
                Message msg = new Message();
                Bundle data = new Bundle();
                data.putString("value","ok");
                msg.setData(data);
                handler4update.sendMessage(msg);
            }catch (ClientProtocolException e1) {
                Log.e(TAG, errMsg, e1);
            } catch (IOException e1) {
                Log.e(TAG, errMsg, e1);
            }catch (Exception e1) {
                Log.e(TAG, errMsg, e1);
            }
        }
        
    };
}
