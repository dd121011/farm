package com.f5.ourfarm.model;

import java.io.Serializable;

/**
 * �ܱ߻
 * 
 * @author lify
 *
 */
public class Events implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private long activityId;//��̨�����У����Ψһ��ʾ
	private String name;//�����
	private String content;//�����
	private String startTime;//���ʼʱ�䣬8λ
	private String endTime;//�����ʱ��
	
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
