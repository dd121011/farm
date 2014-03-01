package com.f5.ourfarm.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 行程步骤
 * 
 * @author lify
 *
 */
public class Line implements Serializable {

	private static final long serialVersionUID = 7446273417415426706L;

	private long itineraryId;//线路id
	private int stepNum;//步骤编号
	private long destinationId;//本步骤终端id
	private String destinationName;
	private String characteristic;//
	private String content;//终点的简介，特色及推荐理由等信息
	private String[] pics;
	
	public Line(long itineraryId, int stepNum, long destinationId,String destinationName,
			String characteristic,String content ,String[] pics){
		this.itineraryId = itineraryId;
		this.stepNum = stepNum;
		this.destinationId = destinationId;
		this.destinationName = destinationName;
		this.characteristic = characteristic;
		this.content = content;
		this.pics = pics;
	}

	public long getItineraryId() {
		return itineraryId;
	}

	public void setItineraryId(long itineraryId) {
		this.itineraryId = itineraryId;
	}

	public int getStepNum() {
		return stepNum;
	}

	public void setStepNum(int stepNum) {
		this.stepNum = stepNum;
	}

	public long getDestinationId() {
		return destinationId;
	}

	public void setDestinationId(long destinationId) {
		this.destinationId = destinationId;
	}

	public String getDestinationName() {
		return destinationName;
	}

	public void setDestinationName(String destinationName) {
		this.destinationName = destinationName;
	}

	public String getCharacteristic() {
		return characteristic;
	}

	public void setCharacteristic(String characteristic) {
		this.characteristic = characteristic;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String[] getPics() {
		return pics;
	}

	public void setPics(String[] pics) {
		this.pics = pics;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
