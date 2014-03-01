package com.f5.ourfarm.service;

import static com.f5.ourfarm.util.URLConstants.PUSH_MESSAGE_URL;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

import com.f5.ourfarm.R;
import com.f5.ourfarm.activity.MainActivity;
import com.f5.ourfarm.model.PushMessage;
import com.f5.ourfarm.util.Constants;
import com.f5.ourfarm.util.HttpUtil;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

public class PullService extends Service {

	public static final String TAG = "PullService";
	public static final String ACTION_START = "action_start";
	public static final String ACTION_STOP = "action_stop";
	public static boolean isAvailable = true;
	public static final int TYPE_DEFAULT = 0;
	private Notification mNotification;
	private NotificationManager mManager;
	
	private static final String errRecommendMsg = "获取推荐信息失败";
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		initNotifiManager();
	}

	private void initNotifiManager() {
		mManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		mNotification = new Notification();
		mNotification.icon = R.drawable.ic_launcher_48;
		mNotification.tickerText = getResources().getString(R.string.newNotification);
		mNotification.defaults |= Notification.DEFAULT_SOUND;
		// 点击后自动消失
		mNotification.flags = Notification.FLAG_AUTO_CANCEL;
	}

	/**
	 * 推送内容并显示
	 * 
	 * @param pushMessage
	 */
	private void launchNotification(PushMessage pushMessage) {
		mNotification.when = System.currentTimeMillis();
		Intent i = new Intent(this, MainActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, Intent.FLAG_ACTIVITY_NEW_TASK);
		mNotification.setLatestEventInfo(this, getResources().getString(R.string.notificationTitle), 
				pushMessage.getContent(), pendingIntent);
		mManager.notify(TYPE_DEFAULT, mNotification);
	}

	public static void startAction(Context ctx) {
		Intent i = new Intent();
		i.setClass(ctx, PullService.class);
		i.setAction(ACTION_START);
		ctx.startService(i);
	}

	public static void stopAction(Context ctx) {
		Intent i = new Intent();
		i.setClass(ctx, PullService.class);
		i.setAction(ACTION_STOP);
		ctx.stopService(i);
	}

	@Override
	public void onStart(Intent intent, int startId) {
		new UpdataNotificationThread().start();
	}

	class UpdataNotificationThread extends Thread {
		@Override
		public void run() {
			//根据时间判断是否适合推送
			if(inNotPushMessageTime()) {
				return;
			}
			String res = "";
			List<PushMessage> pmList = null;
            try {
            	//请求首页显示的消息
            	List<NameValuePair> param = new ArrayList<NameValuePair>();
            	param.add(new BasicNameValuePair("type", String.valueOf("1")));
            	param.add(new BasicNameValuePair("isused", String.valueOf("1")));
            	
            	res = HttpUtil.postUrl(PUSH_MESSAGE_URL, param);
            } catch (ClientProtocolException e1) {
                Log.e(TAG, errRecommendMsg, e1);
                return;
            } catch (IOException e1) {
                Log.e(TAG, errRecommendMsg, e1);
                return;
            }
            
            try {
				Gson gson = new Gson();
				pmList = gson.fromJson(res, new TypeToken<List<PushMessage>>() {}.getType());
			} catch (JsonSyntaxException e) {
				Log.e(TAG, errRecommendMsg, e);
                return;
			}
            
            //有推荐通知情况下
            if(pmList != null && pmList.size() > 0) {
            	PushMessage pm = pmList.get(0);
            	
            	//将推荐的消息保存起来，用于判断请求的推荐信息是否跟原来的一致
            	SharedPreferences setPushMsg = getSharedPreferences(Constants.SAVE_PUSH_MESSAGE, MODE_PRIVATE);
            	long pushMsgId = setPushMsg.getLong(Constants.PUSH_MESSAGE_ID, -1);
            	//如果查询的Id与推荐的一致，就不再推送
            	if(pushMsgId == pm.getIdRecommend()) {
            		return;
            	}
            	
            	//保存推荐的通知Id
        		SharedPreferences.Editor editor = setPushMsg.edit();
        		editor.putLong(Constants.PUSH_MESSAGE_ID, pm.getIdRecommend());
        		editor.commit();
        		
        		launchNotification(pm);
            }
            
		}
	}
	
	/**
	 * 判断当前时间不要做推送处理
	 * 
	 * @return
	 */
	private boolean inNotPushMessageTime() {
		SimpleDateFormat formatYMDHMS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat formatYMD = new SimpleDateFormat("yyyy-MM-dd");
		//获取当前时间
		Calendar now = Calendar.getInstance();
		//用于比较的时间
		Calendar compare = Calendar.getInstance();
        //当天时间
        String today = formatYMD.format(now.getTime());
      
	    try {
	    	//该时间前不推送
			compare.setTime(formatYMDHMS.parse(today + " 12:00:00"));
			if(now.before(compare)) {
				return true;
			}
			//该时间后不推送
			compare.setTime(formatYMDHMS.parse(today + " 18:00:00"));
			if(now.after(compare)) {
				return true;
			}
		} catch (ParseException e) {
			Log.e(TAG, "做推送时，时间解析错误");
			return true;
		}
      
		return false;
	}

}
