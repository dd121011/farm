package com.f5.ourfarm.model;

import java.io.Serializable;

/**
 * 周边活动
 * 
 * @author lify
 *
 */
public class Events implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private long activityId;//后台数据中，活动的唯一标示
	private String name;//活动名称
	private String content;//活动内容
	private String startTime;//活动开始时间，8位
	private String endTime;//活动结束时间
	
	public Events(){
		
	}
	
	public Events(long activityId, String name, String content,
			String startTime, String endTime) {
		this.activityId = activityId;
		this.name = name;
		this.content = content;
		this.startTime = startTime;
		this.endTime = endTime;
	}
	
	public long getActivityId() {
		return activityId;
	}
	public void setActivityId(long activityId) {
		this.activityId = activityId;
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
