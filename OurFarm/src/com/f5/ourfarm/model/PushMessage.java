package com.f5.ourfarm.model;



import java.io.Serializable;

/**
 * ϵͳ���͵���Ϣ
 * 
 * @author lify
 *
 */
public class PushMessage implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private long idRecommend;//�Ƽ���Ϣid
	private long destination_id;//����id������/ũ���ֵ������ں�̨ϵͳ�е�Ψһ��ʾ
	private String content;//�Ƽ�����
	private String time;//�Ƽ�����
	private int type;//�Ƽ����ͣ�1.��ҳ 2.ϵͳ��Ĭ��Ϊ2��
	private boolean isused;//�Ƿ�Ϊ��ǰ�Ƽ�
	
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
