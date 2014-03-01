package com.f5.ourfarm.model;

import java.io.Serializable;

/**
 * 请求排名时的参数
 * 
 * @author lify
 *
 */
public class RankingParams implements Serializable{

	private static final long serialVersionUID = -7925773590606053367L;
	
	private int type;
	private int type_value;
	private String name;
	
	RankingParams (int type, int type_value, String name) {
		this.type = type;
		this.type_value = type_value;
		this.name = name;
	}
	
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getType_value() {
		return type_value;
	}

	public void setType_value(int type_value) {
		this.type_value = type_value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
