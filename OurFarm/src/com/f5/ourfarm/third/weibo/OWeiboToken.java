package com.f5.ourfarm.third.weibo;

import com.weibo.sdk.android.Oauth2AccessToken;

public class OWeiboToken extends Oauth2AccessToken {

	
	public OWeiboToken() {
		super();
		// TODO Auto-generated constructor stub
	}

	public OWeiboToken(String accessToken, String expires_in) {
		super(accessToken, expires_in);
		// TODO Auto-generated constructor stub
	}

	public OWeiboToken(String responsetext) {
		super(responsetext);
		// TODO Auto-generated constructor stub
	}

	private boolean weibotype = false;

	public boolean isWeibotype() {
		return weibotype;
	}

	public void setWeibotype(boolean weibotype) {
		this.weibotype = weibotype;
	}
	

}
