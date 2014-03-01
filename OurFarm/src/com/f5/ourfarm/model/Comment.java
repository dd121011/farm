package com.f5.ourfarm.model;

import java.io.Serializable;

/**
 * ����
 * 
 * @author lify
 *
 */
public class Comment implements Serializable {

	private static final long serialVersionUID = 1L;

	private long destination_id;//����id������/ũ���ֵ������ں�̨ϵͳ�е�Ψһ��ʾ
	private long user_id;//�����ߵ��û�id�������û�Ĭ��Ϊ0
	private int otherSYS_type;//�û����ͣ�0Ϊ�����û���1Ϊ����΢����2Ϊ��Ѷ��3΢�ţ�4Ϊ����
	private String content;//��������
	private String comment_time;//����ʱ��
	private float comment_score;//����
		
	public Comment(){		
	}
	
	public Comment(long destination_id, long user_id, int otherSYS_type,
			String content, String comment_time,float comment_score) {
		super();
		this.destination_id = destination_id;
		this.user_id = user_id;
		this.otherSYS_type = otherSYS_type;
		this.content = content;
		this.comment_time = comment_time;
		this.comment_score = comment_score;
	}

	public long getDestination_id() {
		return destination_id;
	}

	public void setDestination_id(long destination_id) {
		this.destination_id = destination_id;
	}

	public long getUser_id() {
		return user_id;
	}

	public void setUser_id(long user_id) {
		this.user_id = user_id;
	}

	public int getOtherSYS_type() {
		return otherSYS_type;
	}

	public void setOtherSYS_type(int otherSYS_type) {
		this.otherSYS_type = otherSYS_type;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getComment_time() {
		return comment_time;
	}

	public void setComment_time(String comment_time) {
		this.comment_time = comment_time;
	}

	public float getComment_score() {
		return comment_score;
	}

	public void setComment_score(float comment_score) {
		this.comment_score = comment_score;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
}
