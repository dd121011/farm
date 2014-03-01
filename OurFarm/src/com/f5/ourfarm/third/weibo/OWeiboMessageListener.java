package com.f5.ourfarm.third.weibo;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.f5.ourfarm.third.qq.OQQ;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.net.RequestListener;

public class OWeiboMessageListener implements RequestListener {
	
	private Context mcontext;
	private Activity mactivity;
	/*public OWeiboMessageListener(Context context)
	{
		mcontext = context;
	}*/
	public OWeiboMessageListener(Activity activity)
	{
		mactivity = activity;
	}
	@Override
	public void onComplete(String arg0) {
		// TODO Auto-generated method stub
		OQQ.showToast(mactivity, "���ͳɹ� : ");
	}
	@Override
	public void onError(WeiboException arg0) {
		// TODO Auto-generated method stub
		OQQ.showToast(mactivity, "����ʧ�� : ");
	}
	@Override
	public void onIOException(IOException arg0) {
		// TODO Auto-generated method stub
		OQQ.showToast(mactivity, "io�쳣 : ");
	}

}
