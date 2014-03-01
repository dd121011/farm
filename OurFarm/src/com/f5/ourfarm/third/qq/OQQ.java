package com.f5.ourfarm.third.qq;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.f5.ourfarm.activity.ShareContentActivity;
import com.f5.ourfarm.third.ThirdAuthInfo;
import com.f5.ourfarm.util.AccessTokenKeeper;
import com.tencent.weibo.oauthv2.OAuthV2;
import com.tencent.weibo.oauthv2.OAuthV2Client;
import com.tencent.weibo.sdk.android.api.WeiboAPI;
import com.tencent.weibo.sdk.android.model.AccountModel;
import com.tencent.weibo.sdk.android.model.BaseVO;
import com.tencent.weibo.sdk.android.model.ModelResult;
import com.tencent.weibo.sdk.android.network.HttpCallback;
import com.tencent.weibo.webview.OAuthV2AuthorizeWebView;


public class OQQ {

	private static String APP_ID = "801354281";
	private static String App_Secret = "c6fd0cdf8bff6c30903af9b0ebe2abc4";
	private static String REDIRECT_URI = "http://apk.91.com/Soft/Android/com.f5.ourfarm-1.html";
	public static void authorize(Activity activity) {
		OAuthV2 oAuth=new OAuthV2(REDIRECT_URI);
        oAuth.setClientId(APP_ID);
        oAuth.setClientSecret(App_Secret);
        OAuthV2Client.getQHttpClient().shutdownConnection();
		
        Intent i = new Intent(activity, OAuthV2AuthorizeWebView.class);//创建Intent，使用WebView让用户授权
        i.putExtra("oauth", oAuth);
        activity.startActivityForResult(i,2); 
	}
	public static void sendToQQ(Context mcontext, String content) {
		Intent nIntent = new Intent(mcontext,ShareContentActivity.class);
		nIntent.putExtra("shareContent", content);
		mcontext.startActivity(nIntent);
		/*final Context qqcontext = mcontext;
		if (ThirdAuthInfo.qqToken == null)
			ThirdAuthInfo.qqToken = AccessTokenKeeper.readAccessQQToken(mcontext);
		Log.i("qq weibo send", ""+ThirdAuthInfo.qqToken.weibotype);
		if (ThirdAuthInfo.qqToken.weibotype) {
				
			WeiboAPI userAPI=new WeiboAPI(new AccountModel(ThirdAuthInfo.qqToken.accessToken));
			userAPI.reAddWeibo(mcontext, content, null, null, null, null, null,new HttpCallback()
			{

				@Override
				public void onResult(Object result) {
					// TODO Auto-generated method stub
					//com.tencent.weibo.sdk.android.model.ModelResult@417f0b00

					Log.i("qq weibo send", ""+((ModelResult)result).isSuccess());
					if(((ModelResult)result).isSuccess())
						Toast.makeText(qqcontext,"发送成功",Toast.LENGTH_LONG).show();
					else
						Toast.makeText(qqcontext,"发送失败",Toast.LENGTH_LONG).show();
				}
				
			}, null, BaseVO.TYPE_JSON);

			}*/
		
	}
	public static void sendToQQNew(Context mcontext, String content) {
		final Context qqcontext = mcontext;
		if (ThirdAuthInfo.qqToken == null)
			ThirdAuthInfo.qqToken = AccessTokenKeeper.readAccessQQToken(mcontext);
		Log.i("qq weibo send", ""+ThirdAuthInfo.qqToken.weibotype);
		if (ThirdAuthInfo.qqToken.weibotype) {
				
			WeiboAPI userAPI=new WeiboAPI(new AccountModel(ThirdAuthInfo.qqToken.accessToken));
			userAPI.reAddWeibo(mcontext, content, null, null, null, null, null,new HttpCallback()
			{

				@Override
				public void onResult(Object result) {
					// TODO Auto-generated method stub
					//com.tencent.weibo.sdk.android.model.ModelResult@417f0b00

					Log.i("qq weibo send", ""+((ModelResult)result).isSuccess());
					if(((ModelResult)result).isSuccess())
						Toast.makeText(qqcontext,"发送成功",Toast.LENGTH_LONG).show();
					else
						Toast.makeText(qqcontext,"发送失败",Toast.LENGTH_LONG).show();
				}
				
			}, null, BaseVO.TYPE_JSON);

			}
		
	}
	public static void setQQType(Context context,boolean type)
	{
		if (ThirdAuthInfo.qqToken == null)
			ThirdAuthInfo.qqToken = AccessTokenKeeper.readAccessQQToken(context);
		ThirdAuthInfo.qqToken.weibotype = type;
		AccessTokenKeeper.keepAccessToken(context, ThirdAuthInfo.qqToken);
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
