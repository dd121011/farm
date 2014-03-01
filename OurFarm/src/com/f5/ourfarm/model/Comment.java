package com.f5.ourfarm.model;

import java.io.Serializable;

/**
 * 评论
 * 
 * @author lify
 *
 */
public class Comment implements Serializable {

	private static final long serialVersionUID = 1L;

	private long destination_id;//内容id，景点/农家乐等内容在后台系统中的唯一标示
	private long user_id;//评价者的用户id，匿名用户默认为0
	private int otherSYS_type;//用户类型：0为匿名用户，1为新浪微博，2为腾讯，3微信，4为邮箱
	private String content;//评论内容
	private String comment_time;//评论时间
	private float comment_score;//评分
		
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
