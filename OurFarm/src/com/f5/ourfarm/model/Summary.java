package com.f5.ourfarm.model;



import java.io.Serializable;

/**
 * 景点/农家乐概要信息
 * 
 * @author lify
 *
 */
public class Summary implements Serializable {

	private static final long serialVersionUID = 1L;

	private long destinationId;//内容id，景点/农家乐等内容在后台系统中的唯一标示
	private String name;//内容名称
	private double lat;//内容在地图上标点的纬度坐标
	private double lng;//内容在地图上标点的经度坐标
	private String address;//内容详细地址
	private String pic;//图片URL
	private String tel;//座机电话
	private String phone;//手机号码
	private float hot;//热度
	private float price;//人均消费/票价
	private String priceInfo;//价格信息
	private float score;//平均分数
	private double distance;//离用户距离，单位为km
	private String characteristic;//特色，用$分隔
	private int type;//用于区别4中类型
		
	public Summary(){		
	}
	
	public Summary(long destinationId, String name, float lat,
			float lng, String address, String pic, String tel, String phone,
			float hot, float price, String priceInfo, float score,
			float distance, String characteristic,int type) {
		super();
		this.destinationId = destinationId;
		this.name = name;
		this.lat = lat;
		this.lng = lng;
		this.address = address;
		this.pic = pic;
		this.tel = tel;
		this.phone = phone;
		this.hot = hot;
		this.price = price;
		this.priceInfo = priceInfo;
		this.score = score;
		this.distance = distance;
		this.characteristic = characteristic;
		this.type = type;
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
	public double getLat() {
		return lat;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}
	public double getLng() {
		return lng;
	}
	public void setLng(double lng) {
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
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public float getHot() {
		return hot;
	}
	public void setHot(float hot) {
		this.hot = hot;
	}
	public float getPrice() {
		return price;
	}
	public void setPrice(float price) {
		this.price = price;
	}
	public String getPriceInfo() {
		return priceInfo;
	}
	public void setPriceInfo(String priceInfo) {
		this.priceInfo = priceInfo;
	}
	public float getScore() {
		return score;
	}
	public void setScore(float score) {
		this.score = score;
	}
	public double getDistance() {
		return distance;
	}
	public void setDistance(double distance) {
		this.distance = distance;
	}
	public String getCharacteristic() {
		return characteristic;
	}
	public void setCharacteristic(String characteristic) {
		this.characteristic = characteristic;
	}
    public int getType() {
        return type;
    }
    public void setType(int type) {
        this.type = type;
    }
	
}
