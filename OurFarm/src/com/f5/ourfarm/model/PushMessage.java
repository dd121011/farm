package com.f5.ourfarm.model;



import java.io.Serializable;

/**
 * 系统推送的信息
 * 
 * @author lify
 *
 */
public class PushMessage implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private long idRecommend;//推荐信息id
	private long destination_id;//内容id，景点/农家乐等内容在后台系统中的唯一标示
	private String content;//推荐内容
	private String time;//推荐日期
	private int type;//推荐内型：1.首页 2.系统（默认为2）
	private boolean isused;//是否为当前推荐
	
	public PushMessage(){		
	}
	
	public PushMessage(long idRecommend, long destination_id, String content, 
			String time, int type, boolean isused) {
		super();
		this.idRecommend = idRecommend;
		this.destination_id = destination_id;
		this.content = content;
		this.time = time;
		this.type = type;
		this.isused = isused;
	}

	public long getIdRecommend() {
		return idRecommend;
	}

	public void setIdRecommend(long idRecommend) {
		this.idRecommend = idRecommend;
	}

	public long getDestination_id() {
		return destination_id;
	}

	public void setDestination_id(long destination_id) {
		this.destination_id = destination_id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public boolean isIsused() {
		return isused;
	}

	public void setIsused(boolean isused) {
		this.isused = isused;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
}
