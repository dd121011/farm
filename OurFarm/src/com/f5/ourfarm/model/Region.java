package com.f5.ourfarm.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Region implements Serializable {

	private static final long serialVersionUID = 1L;
	public static final String REGION_ID = "region_id";
	public static final String REGION_NAMWE = "region_name";
	public static final String REGION_PARENT_ID = "region_parentId";
	
	private int id;//����Id
	private String name;//��������
	private int parentId;//�ϼ�����Id
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public int getParentId() {
		return parentId;
	}
	public void setParentId(int parentId) {
		this.parentId = parentId;
	}
	
	/**
	 * ��listת����listView
	 * 
	 * @param reglist
	 * @return
	 */
	public static List<Map<String, Object>> getAllRegionForListView(List<Region> reglist){
		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		int size = reglist.size();
		for(int i = 0; i < size; i++){
			HashMap<String, Object>	item = new HashMap<String, Object>();
			item.put(REGION_ID, reglist.get(i).getId());
			item.put(REGION_NAMWE, reglist.get(i).getName());
			item.put(REGION_PARENT_ID, reglist.get(i).getParentId());
			data.add(item);
		}
		return data;
	}
	
    /**
     * ��ȡ�����ܱߵ�����id��10001�𣬱�������id��10000
     * 
     * @return �����ܱߵ������б�
     */
    public static List<Region> getRegions() {
    	List<Region> reglist = new ArrayList<Region>();
    	
    	Region reg = new Region();
    	reg.setId(110105);
    	reg.setName("����");
    	reglist.add(reg);
    	
    	reg = new Region();
        reg.setId(110106);
        reg.setName("��̨");
        reglist.add(reg);
        
        reg = new Region();
        reg.setId(110107);
        reg.setName("ʯ��ɽ");
        reglist.add(reg);
        
        reg = new Region();
        reg.setId(110108);
        reg.setName("����");
        reglist.add(reg);
        
        reg = new Region();
        reg.setId(110109);
        reg.setName("��ͷ��");
        reglist.add(reg);
        
        reg = new Region();
        reg.setId(110111);
        reg.setName("��ɽ");
        reglist.add(reg);

        reg = new Region();
        reg.setId(110112);
        reg.setName("ͨ��");
        reglist.add(reg);
        
        reg = new Region();
        reg.setId(110113);
        reg.setName("˳��");
        reglist.add(reg);

        reg = new Region();
        reg.setId(110114);
        reg.setName("��ƽ");
        reglist.add(reg);

        reg = new Region();
        reg.setId(110115);
        reg.setName("����");
        reglist.add(reg);

        reg = new Region();
        reg.setId(110116);
        reg.setName("����");
        reglist.add(reg);

        reg = new Region();
        reg.setId(110117);
        reg.setName("ƽ��");
        reglist.add(reg);

        reg = new Region();
        reg.setId(110228);
        reg.setName("����");
        reglist.add(reg);
        
        reg = new Region();
        reg.setId(110229);
        reg.setName("����");
        reglist.add(reg);
        
        reg = new Region();
        reg.setId(0);
        reg.setName("�����ܱ�");
        reglist.add(reg);
    	
    	return reglist;
    }
	
}
