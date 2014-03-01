package com.f5.ourfarm.model;

/**
 * 新浪微博错误反馈消息
 * 错误编号含义参考：http://open.weibo.com/wiki/Help/error
 * 
 * @author lify
 *
 */
public class SinaWeiboErrorMsg {

	private int error_code;
	private String request;
	private String error;
	
	public int getError_code() {
		return error_code;
	}
	public void setError_code(int error_code) {
		this.error_code = error_code;
	}
	public String getRequest() {
		return request;
	}
	public void setRequest(String request) {
		this.request = request;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	
	

}
