package com.f5.ourfarm.model;

import java.io.Serializable;
import java.util.List;

/**
 * 经典旅游线路
 * 
 * @author lify
 *
 */
public class ClassicItineraries implements Serializable {

	private static final long serialVersionUID = -3336549172753990589L;

	private long itineraryId;//线路id
	private String name;
	private String itinerarySummary;//行程的概要
	private float score;//平均分数
	private float hot;//热度
	private String pic;//头像url
	private String picMap;//头像url
	private float price;//人均消费
	private String priceInfo;//价格信息
	private String characteristic;//特色，用$分隔
	private List<Line> line;//行程步骤
	
	public ClassicItineraries(){
		
	}
	
	public ClassicItineraries(long itineraryId, String itinerarySummary,
			float score, float hot, String pic, String picMap,float price, String priceInfo,
			String characteristic, List<Line> line) {
		this.itineraryId = itineraryId;
		this.itinerarySummary = itinerarySummary;
		this.score = score;
		this.hot = hot;
		this.pic = pic;
		this.picMap = picMap;
		this.price = price;
		this.priceInfo = priceInfo;
		this.characteristic = characteristic;
		this.line = line;
	}

	public long getItineraryId() {
		return itineraryId;
	}

	public void setItineraryId(long itineraryId) {
		this.itineraryId = itineraryId;
	}

	
	public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getItinerarySummary() {
		return itinerarySummary;
	}

	public void setItinerarySummary(String itinerarySummary) {
		this.itinerarySummary = itinerarySummary;
	}

	public float getScore() {
		return score;
	}

	public void setScore(float score) {
		this.score = score;
	}

	public float getHot() {
		return hot;
	}

	public void setHot(float hot) {
		this.hot = hot;
	}

	public String getPic() {
		return pic;
	}

	public void setPic(String pic) {
		this.pic = pic;
	}
	
	

	public String getPicMap() {
		return picMap;
	}

	public void setPicMap(String picMap) {
		this.picMap = picMap;
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

	public String getCharacteristic() {
		return characteristic;
	}

	public void setCharacteristic(String characteristic) {
		this.characteristic = characteristic;
	}

	public List<Line> getLine() {
		return line;
	}

	public void setLine(List<Line> line) {
		this.line = line;
	}

}
