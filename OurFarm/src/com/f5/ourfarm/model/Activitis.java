package com.f5.ourfarm.model;

import java.io.Serializable;

/**
 * 促销信息
 * 
 * @author lify
 *
 */
public class Activitis implements Serializable {

	private static final long serialVersionUID = -3455333192009372428L;

	private long activityId;//后台数据中， 促销的唯一标示
	private String name;//促销名称
	private String content;//促销内容
	private String startTime;//促销开始时间，具体到某天，8位字符串
	private String endTime;//促销结束时间
	
	public Activitis(){
		
	}
	
	public Activitis(long promotionsId,  String name,
			String content, String startTime, String endTime) {
		super();
		this.activityId = promotionsId;
		
		this.name = name;
		this.content = content;
		this.startTime = startTime;
		this.endTime = endTime;
	}
	
	public long getPromotionsId() {
		return activityId;
	}
	public void setPromotionsId(long promotionsId) {
		this.activityId = promotionsId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
}
