package com.f5.ourfarm.third.qq;

import com.tencent.weibo.sdk.android.component.sso.WeiboToken;

public class OQQToken extends WeiboToken{
	private static long APP_ID = 801354281;
	public boolean weibotype = false;
	public boolean isWeibotype() {
		return weibotype;
	}
	public void setWeibotype(boolean weibotype) {
		this.weibotype = weibotype;
	}
	public OQQToken() {
		super();
	}

}
