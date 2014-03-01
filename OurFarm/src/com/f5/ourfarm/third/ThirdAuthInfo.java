package com.f5.ourfarm.third;

import com.f5.ourfarm.third.qq.OQQToken;
import com.f5.ourfarm.third.weibo.OWeiboToken;
import com.f5.ourfarm.util.AccessTokenKeeper;

public class ThirdAuthInfo {
	public static OQQToken getQqToken() {
		return qqToken;
	}
	public static void setQqToken(OQQToken qqToken) {
		ThirdAuthInfo.qqToken = qqToken;
	}
	public static OWeiboToken weiboAccessToken;
	public static OQQToken qqToken;
	public static OWeiboToken getWeiboAccessToken()
	{
		return weiboAccessToken;
	}
	public static void setWeiboAccessToken(OWeiboToken at)
	{
		weiboAccessToken = at;
	}
}
