package com.f5.ourfarm.model;

import java.io.Serializable;

/**
 * ������Ϣ
 * 
 * @author lify
 *
 */
public class Promotions implements Serializable {

	private static final long serialVersionUID = -3455333192009372428L;

	private long promotionsId;//��̨�����У� ������Ψһ��ʾ
	private long destinationId;//����id
	private String name;//��������
	private String content;//��������
	private String startTime;//������ʼʱ�䣬���嵽ĳ�죬8λ�ַ���
	private String endTime;//��������ʱ��
	
	public Promotions(){
		
	}
	
	public Promotions(long promotionsId, long destinationId, String name,
			String content, String startTime, String endTime) {
		super();
		this.promotionsId = promotionsId;
		this.destinationId = destinationId;
		this.name = name;
		this.content = content;
		this.startTime = startTime;
		this.endTime = endTime;
	}
	
	public long getPromotionsId() {
		return promotionsId;
	}
	public void setPromotionsId(long promotionsId) {
		this.promotionsId = promotionsId;
	}
	public long getDestinationId() {
		return destinationId;
	}
	public void setDestinationId(long destinationId) {
		this.destinationId = destinationId;
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
