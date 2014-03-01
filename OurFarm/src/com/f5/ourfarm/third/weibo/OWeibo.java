package com.f5.ourfarm.third.weibo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.f5.ourfarm.activity.ShareContentActivity;
import com.f5.ourfarm.third.ThirdAuthInfo;
import com.f5.ourfarm.util.AccessTokenKeeper;
import com.weibo.sdk.android.Weibo;
import com.weibo.sdk.android.api.StatusesAPI;

public class OWeibo {

	private static Weibo mweibo;
	private static final String REDIRECT_URL = "http://www.sina.com";
	// ע�⣡���˴���������appkey��appsecret����λ�ȡ����΢��appkey��appsecret�������ѯ�����Ϣ���˴���������
	private static final String CONSUMER_KEY = "2913772243";// �滻Ϊ�����ߵ�appkey������"1646212860";
	public static final String EXTRA_ACCESS_TOKEN = "com.weibo.android.accesstoken";
	public static final String EXTRA_EXPIRES_IN = "com.weibo.android.token.expires";
	public static final String WEIBO_TEXT = "weibo_text";

	public static Weibo getInstance() {
		if (mweibo == null) {
			mweibo = Weibo.getInstance(CONSUMER_KEY, REDIRECT_URL);
		}
		return mweibo;
	}

	public static void authorize(Context context) {
		if (ThirdAuthInfo.weiboAccessToken == null)
			ThirdAuthInfo.weiboAccessToken = AccessTokenKeeper
					.readAccessWeiboToken(context);
		if (!ThirdAuthInfo.weiboAccessToken.isSessionValid()) {
			mweibo = OWeibo.getInstance();
			mweibo.authorize(context, new OWeiboAuthListener(context));
		}
	}
	public static void authorize(Context context,OWeiboAuthListener listener) {
		if (ThirdAuthInfo.weiboAccessToken == null)
			ThirdAuthInfo.weiboAccessToken = AccessTokenKeeper
					.readAccessWeiboToken(context);
			mweibo = OWeibo.getInstance();
			mweibo.authorize(context, listener);
	}
	public static void sendToWeibo(Context context, String content,OWeiboMessageListener listener) {
		/*if (ThirdAuthInfo.weiboAccessToken == null)
			ThirdAuthInfo.weiboAccessToken = AccessTokenKeeper
					.readAccessWeiboToken(context);
		if(ThirdAuthInfo.weiboAccessToken.isWeibotype())
			return;
		if (ThirdAuthInfo.weiboAccessToken.isSessionValid()) {
				// ֱ��ȥ��΢��
				StatusesAPI api = new StatusesAPI(ThirdAuthInfo.weiboAccessToken);
				// Just update a text weibo!
				api.update(content, "", "",listener);

			} else {// ���»������
				authorize(context);
		}*/
		Intent nIntent = new Intent(context,ShareContentActivity.class);
		nIntent.putExtra("shareContent", content);
		context.startActivity(nIntent);
	}
	public static void sendToWeiboNew(Context context, String content,OWeiboMessageListener listener) {
		if (ThirdAuthInfo.weiboAccessToken == null)
			ThirdAuthInfo.weiboAccessToken = AccessTokenKeeper
					.readAccessWeiboToken(context);
		if(!ThirdAuthInfo.weiboAccessToken.isWeibotype())
			return;
		if (ThirdAuthInfo.weiboAccessToken.isSessionValid()) {
				// ֱ��ȥ��΢��
				StatusesAPI api = new StatusesAPI(ThirdAuthInfo.weiboAccessToken);
				// Just update a text weibo!
				api.update(content, "", "",listener);

			} else {// ���»������
				authorize(context);
		}
	}
	public static void setWeiboType(Context context, boolean type) {
		if (ThirdAuthInfo.weiboAccessToken == null)
			ThirdAuthInfo.weiboAccessToken = AccessTokenKeeper
					.readAccessWeiboToken(context);
		ThirdAuthInfo.weiboAccessToken.setWeibotype(type);
		AccessTokenKeeper.keepAccessToken(context, ThirdAuthInfo.weiboAccessToken);
	}
	
	public static void showToast(final Activity activity,final String content) {
        activity.runOnUiThread(new Runnable() {
            
            @Override
            public void run() {
                Toast toast = Toast.makeText(activity, content, Toast.LENGTH_LONG);
                toast.show();
                
            }
        });
       
    }
}
