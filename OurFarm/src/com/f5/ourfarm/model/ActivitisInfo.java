package com.f5.ourfarm.model;

import java.io.Serializable;

/**
 * ���Ϣ
 * 
 * @author lify
 *
 */
public class ActivitisInfo implements Serializable {

	private static final long serialVersionUID = -3455333192009372428L;

	private long activityId;//��̨�����У� ������Ψһ��ʾ
	private String name;//�����
	private String introduction;//�����
	private String start_time;//������ʼʱ�䣬���嵽ĳ�죬8λ�ַ���
	private String end_time;//��������ʱ��
	private float lat;//γ��
	private float lng;//����
	private String address;//�ص�
	private String pic;//���ͼƬ
	private String tel;//��ϵ�绰
	private String region_code;//�������
	private String type;//�����
	private String www;//�ٷ���վ
	
	public ActivitisInfo(){
		
	}
	
	public ActivitisInfo(long promotionsId, String name, String introduction,String start_time,String end_time,
			float lat, float lng, String address, String pic, String tel, String region_code, String type, String www) {
		super();
		this.activityId = promotionsId;
		this.name = name;
		this.introduction = introduction;
		this.start_time = start_time;
		this.end_time = end_time;
		this.lat = lat;
		this.lng = lng;
		this.address = address;
		this.pic = pic;
		this.tel = tel;
		this.region_code = region_code;
		this.type = type;
		this.www = www;
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

	public String getIntroduction() {
		return introduction;
	}

	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}

	public String getStart_time() {
		return start_time;
	}

	public void setStart_time(String start_time) {
		this.start_time = start_time;
	}

	public String getEnd_time() {
		return end_time;
	}

	public void setEnd_time(String end_time) {
		this.end_time = end_time;
	}

	public float getLat() {
		return lat;
	}

	public void setLat(float lat) {
		this.lat = lat;
	}

	public float getLng() {
		return lng;
	}

	public void setLng(float lng) {
		this.lng = lng;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPic() {
		return pic;
	}

	public void setPic(String pic) {
		this.pic = pic;
	}

	public String getRegion_code() {
		return region_code;
	}

	public void setRegion_code(String region_code) {
		this.region_code = region_code;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getWww() {
		return www;
	}

	public void setWww(String www) {
		this.www = www;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}
	
}
