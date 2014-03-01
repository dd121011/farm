package com.f5.ourfarm.model;

import java.io.Serializable;

public class QQJson implements Serializable {

	public QQJson(String appid, String pay_token, String openid,
			long expires_in, String pf, String pfkey, String access_token) {
		super();
		this.appid = appid;
		this.pay_token = pay_token;
		this.openid = openid;
		this.expires_in = expires_in;
		this.pf = pf;
		this.pfkey = pfkey;
		this.access_token = access_token;
	}
	public String getAppid() {
		return appid;
	}
	public void setAppid(String appid) {
		this.appid = appid;
	}
	public String getPay_token() {
		return pay_token;
	}
	public void setPay_token(String pay_token) {
		this.pay_token = pay_token;
	}
	public String getOpenid() {
		return openid;
	}
	public void setOpenid(String openid) {
		this.openid = openid;
	}
	public long getExpires_in() {
		return expires_in;
	}
	public void setExpires_in(long expires_in) {
		this.expires_in = expires_in;
	}
	public String getPf() {
		return pf;
	}
	public void setPf(String pf) {
		this.pf = pf;
	}
	public String getPfkey() {
		return pfkey;
	}
	public void setPfkey(String pfkey) {
		this.pfkey = pfkey;
	}
	public String getAccess_token() {
		return access_token;
	}
	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	private static final long serialVersionUID = 1L;
	private String appid;
	private String pay_token;
	private String openid;
	private long expires_in;
	private String pf;
	private String pfkey;
	private String access_token;


}
