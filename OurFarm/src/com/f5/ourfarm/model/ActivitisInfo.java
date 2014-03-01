package com.f5.ourfarm.model;

import java.io.Serializable;

/**
 * 活动信息
 * 
 * @author lify
 *
 */
public class ActivitisInfo implements Serializable {

	private static final long serialVersionUID = -3455333192009372428L;

	private long activityId;//后台数据中， 促销的唯一标示
	private String name;//活动名称
	private String introduction;//活动内容
	private String start_time;//促销开始时间，具体到某天，8位字符串
	private String end_time;//促销结束时间
	private float lat;//纬度
	private float lng;//经度
	private String address;//地点
	private String pic;//活动的图片
	private String tel;//联系电话
	private String region_code;//区域编码
	private String type;//活动类型
	private String www;//官方网站
	
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
