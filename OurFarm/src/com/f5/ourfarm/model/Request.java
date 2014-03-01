package com.f5.ourfarm.model;

import java.util.HashMap;

import android.content.Context;

public class Request {

	private String url;
	private Context context;
	private HashMap<String,String> par;
	private int callBack;
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public Context getContext() {
		return context;
	}
	public void setContext(Context context) {
		this.context = context;
	}
	public HashMap<String,String> getPar() {
		return par;
	}
	public void setPar(HashMap<String,String> par) {
		this.par = par;
	}
	public Request(String url, Context context, HashMap<String,String> par,int callBack) {
		super();
		this.url = url;
		this.context = context;
		this.par = par;
		this.callBack = callBack;
	}
	public int getCallBack() {
		return callBack;
	}
	public void setCallBack(int callBack) {
		this.callBack = callBack;
	} 
}
