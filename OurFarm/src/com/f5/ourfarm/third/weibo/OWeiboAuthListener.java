package com.f5.ourfarm.third.weibo;

import java.io.IOException;
import java.text.SimpleDateFormat;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.f5.ourfarm.activity.DetailActivity;
import com.f5.ourfarm.third.ThirdAuthInfo;
import com.f5.ourfarm.util.AccessTokenKeeper;
import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.WeiboAuthListener;
import com.weibo.sdk.android.WeiboDialogError;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.net.RequestListener;

public class OWeiboAuthListener implements WeiboAuthListener {
	private Context mactivity;
	public OWeiboAuthListener(Context context)
	{
		mactivity = context;
	}
	@Override
	public void onComplete(Bundle values) {
		String token = values.getString("access_token");
		String expires_in = values.getString("expires_in");
		Log.i("weibo auth","token="+token);
		Log.i("weibo auth","expires_in="+expires_in);
		//重新加载token
		ThirdAuthInfo.weiboAccessToken = new OWeiboToken(token, expires_in);
		if (ThirdAuthInfo.weiboAccessToken.isSessionValid()) {
			//更新到share中
			ThirdAuthInfo.weiboAccessToken.setWeibotype(true);
			AccessTokenKeeper.keepAccessToken(mactivity, ThirdAuthInfo.weiboAccessToken);
			Log.i("weibo auth",ThirdAuthInfo.weiboAccessToken.getToken());
			Toast.makeText(mactivity, "微博绑定成功 : ",
					Toast.LENGTH_LONG).show();
		}
		
		
		
		//expires_in
	}

	@Override
	public void onError(WeiboDialogError e) {
		Toast.makeText(mactivity, "Auth error : " + e.getMessage(),
				Toast.LENGTH_LONG).show();
	}

	@Override
	public void onCancel() {
		Toast.makeText(mactivity, "Auth cancel", Toast.LENGTH_LONG).show();
	}

	@Override
	public void onWeiboException(WeiboException e) {
		Toast.makeText(mactivity, "Auth exception : " + e.getMessage(),
				Toast.LENGTH_LONG).show();
	}

}
