package com.f5.ourfarm.util;

import com.f5.ourfarm.model.QQJson;
import com.f5.ourfarm.third.ThirdAuthInfo;
import com.f5.ourfarm.third.qq.OQQToken;
import com.f5.ourfarm.third.weibo.OWeiboToken;
import com.weibo.sdk.android.Oauth2AccessToken;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
/**
 * 璇ョ被鐢ㄤ簬淇濆瓨Oauth2AccessToken鍒皊harepreference锛屽苟鎻愪緵璇诲彇鍔熻兘
 * @author xiaowei6@staff.sina.com.cn
 *
 */
public class AccessTokenKeeper {
	private static final String PREFERENCES_NAME = "com_weibo_sdk_android";
	/**
	 * 淇濆瓨accesstoken鍒癝haredPreferences
	 * @param context Activity 涓婁笅鏂囩幆澧�	 * @param token Oauth2AccessToken
	 */
	public static void init(Context context)
	{
		if(ThirdAuthInfo.qqToken == null)
		ThirdAuthInfo.qqToken = readAccessQQToken(context);
		if(ThirdAuthInfo.weiboAccessToken == null)
		ThirdAuthInfo.weiboAccessToken = readAccessWeiboToken(context);
	}
	public static void keepAccessToken(Context context, OWeiboToken token) {
		SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
		Editor editor = pref.edit();
		editor.putString(Constants.SINA_WEIBO_TOKEN, token.getToken());
		editor.putLong(Constants.SINA_WEIBO_TIME, token.getExpiresTime());
		editor.putBoolean(Constants.SINA_WEIBO_BAND, token.isWeibotype());
		editor.commit();
	}
	public static void keepAccessToken(Context context, OQQToken token) {
		SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
		Editor editor = pref.edit();
		editor.putString(Constants.QQ_WEIBO_TOKEN, token.accessToken);
		editor.putLong(Constants.QQ_WEIBO_TIME, token.expiresIn);
		editor.putBoolean(Constants.QQ_WEIBO_BAND, token.weibotype);
		editor.commit();
	}
	public static void keepAccessToken(Context context, Oauth2AccessToken token) {
		SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
		Editor editor = pref.edit();
		editor.putString("token", token.getToken());
		editor.putLong("expiresTime", token.getExpiresTime());
		editor.commit();
	}
	/**
	 * 娓呯┖sharepreference
	 * @param context
	 */
	public static void clear(Context context){
	    SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
	    Editor editor = pref.edit();
	    editor.clear();
	    editor.commit();
	}

	/**
	 * 浠嶴haredPreferences璇诲彇accessstoken
	 * @param context
	 * @return Oauth2AccessToken
	 */
	public static Oauth2AccessToken readAccessToken(Context context){
		Oauth2AccessToken token = new Oauth2AccessToken();
		SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
		token.setToken(pref.getString("token", ""));
		token.setExpiresTime(pref.getLong("expiresTime", 0));
		return token;
	}
	public static OWeiboToken readAccessWeiboToken(Context context){
		OWeiboToken token = new OWeiboToken();
		SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
		token.setToken(pref.getString(Constants.SINA_WEIBO_TOKEN, ""));
		token.setExpiresTime(pref.getLong(Constants.SINA_WEIBO_TIME, 0));
		token.setWeibotype(pref.getBoolean(Constants.SINA_WEIBO_BAND, false));
		return token;
	}
	public static OQQToken readAccessQQToken(Context context){
		OQQToken token = new OQQToken();
		SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
		token.accessToken = pref.getString(Constants.QQ_WEIBO_TOKEN, "");
		token.expiresIn = pref.getLong(Constants.QQ_WEIBO_TIME, 0);
		token.weibotype = pref.getBoolean(Constants.QQ_WEIBO_BAND, false);
		return token;
	}
}
