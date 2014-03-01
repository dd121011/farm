package com.f5.ourfarm.model;

import java.io.Serializable;
import java.util.List;

/**
 * ����/ũ��Ժ��ϸ����
 * 
 * @author lify
 *
 */
public class Destination implements Serializable {

	private static final long serialVersionUID = 1L;

	private Summary summary;//��Ҫ��Ϣ
	private String introduction;//���ݽ���
	private String otherContact;//������ϵ��ʽ
	private String car;//�Լ�·�߽���
	private String bus;//������ͨ·�߽���
	private String bike;//����·�߽���
	private boolean classicFlag;//�Ƿ�Ϊ����
	private int regionCode;//�������
	private String region;//��������
	private String mapPic;//��̬��ͼ
	private String preferentialInfo;//�Ż���Ϣ
	private String label;//�������ͣ���̨ϵͳ���ڷ���
	private String[] pics;//�þ���ͼƬ
	private List<PicsVilla> picsVilla;//ͼ�Ľ���
	
	public Destination(){		
	}

	public Destination(Summary scenerySummary, String introduction,
			String otherContact, String car, String bus, String bike,
			boolean classicFlag, int regionCode, String region, String mapPic,
			String preferentialInfo, String label, String[] pics, List<PicsVilla> picsVilla) {
		super();
		this.summary = scenerySummary;
		this.introduction = introduction;
		this.otherContact = otherContact;
		this.car = car;
		this.bus = bus;
		this.bike = bike;
		this.classicFlag = classicFlag;
		this.regionCode = regionCode;
		this.region = region;
		this.mapPic = mapPic;
		this.preferentialInfo = preferentialInfo;
		this.label = label;
		this.pics = pics;
		this.picsVilla = picsVilla;
	}

	public Summary getScenerySummary() {
		return summary;
	}
	public void setScenerySummary(Summary scenerySummary) {
		this.summary = scenerySummary;
	}
	public String getIntroduction() {
		return introduction;
	}
	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}
	public String getOtherContact() {
		return otherContact;
	}
	public void setOtherContact(String otherContact) {
		this.otherContact = otherContact;
	}
	public String getCar() {
		return car;
	}
	public void setCar(String car) {
		this.car = car;
	}
	public String getBus() {
		return bus;
	}
	public void setBus(String bus) {
		this.bus = bus;
	}
	public String getBike() {
		return bike;
	}
	public void setBike(String bike) {
		this.bike = bike;
	}
	public boolean isClassicFlag() {
		return classicFlag;
	}
	public void setClassicFlag(boolean classicFlag) {
		this.classicFlag = classicFlag;
	}
	public int getRegionCode() {
		return regionCode;
	}
	public void setRegionCode(int regionCode) {
		this.regionCode = regionCode;
	}
	public String getRegion() {
		return region;
	}
	public void setRegion(String region) {
		this.region = region;
	}
	public String getMapPic() {
		return mapPic;
	}
	public void setMapPic(String mapPic) {
		this.mapPic = mapPic;
	}
	public String getPreferentialInfo() {
		return preferentialInfo;
	}
	public void setPreferentialInfo(String preferentialInfo) {
		this.preferentialInfo = preferentialInfo;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String[] getPics() {
		return pics;
	}
	public void setPics(String[] pics) {
		this.pics = pics;
	}
	public List<PicsVilla> getPicsVilla() {
		return picsVilla;
	}
	public void setPicsVilla(List<PicsVilla> picsVilla) {
		this.picsVilla = picsVilla;
	}
}
