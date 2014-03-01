package com.f5.ourfarm.model;



import java.io.Serializable;

/**
 * �����ܱߵ�ũ��Ժ
 * 
 * @author lify
 *
 */
public class MoreAroundFarm implements Serializable {

	private static final long serialVersionUID = 1L;

	private long view_id;//����ID
	private long farmhome_id;//ũ����ID
	private int type;//0��ϵͳ�Ƽ���ũ����;����Ϊ��ѯ���
	private double distance;//�뾰���km
	private Summary summary;//��Ҫ��Ϣ
	
	public MoreAroundFarm(){		
	}

	public MoreAroundFarm(long view_id, long farmhome_id, int type, double distance, Summary scenerySummary) {
		super();
		this.view_id = view_id;
		this.farmhome_id = farmhome_id;
		this.type = type;
		this.distance = distance;
		this.summary = scenerySummary;
	}

	public long getView_id() {
		return view_id;
	}

	public void setView_id(long view_id) {
		this.view_id = view_id;
	}

	public long getFarmhome_id() {
		return farmhome_id;
	}

	public void setFarmhome_id(long farmhome_id) {
		this.farmhome_id = farmhome_id;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public Summary getSummary() {
		return summary;
	}

	public void setSummary(Summary summary) {
		this.summary = summary;
	}

}
