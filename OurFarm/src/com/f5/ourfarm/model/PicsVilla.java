package com.f5.ourfarm.model;

import java.io.Serializable;

/**
 * 景点的图文推荐和介绍
 * 
 * @author lify
 *
 */
public class PicsVilla implements Serializable {

	private int type;
	private String content;
	private String pic;
	
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getPic() {
		return pic;
	}
	public void setPic(String pic) {
		this.pic = pic;
	}

}
