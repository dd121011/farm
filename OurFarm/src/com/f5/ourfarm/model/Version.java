package com.f5.ourfarm.model;

import java.io.Serializable;

public class Version implements Serializable {
	private static final long serialVersionUID = -3455333192009372428L;
	private String version;
	private String content;
	private String type;
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Version()
	{}
	public Version(String version, String content, String type) {
		super();
		this.version = version;
		this.content = content;
		this.type = type;
	}



}
