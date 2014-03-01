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
	
	private int id;//区域Id
	private String name;//区域名字
	private int parentId;//上级区域Id
	
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
	 * 将list转换成listView
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
     * 获取北京周边的区域，id从10001起，北京备用id是10000
     * 
     * @return 北京周边的区域列表
     */
    public static List<Region> getRegions() {
    	List<Region> reglist = new ArrayList<Region>();
    	
    	Region reg = new Region();
    	reg.setId(110105);
    	reg.setName("朝阳");
    	reglist.add(reg);
    	
    	reg = new Region();
        reg.setId(110106);
        reg.setName("丰台");
        reglist.add(reg);
        
        reg = new Region();
        reg.setId(110107);
        reg.setName("石景山");
        reglist.add(reg);
        
        reg = new Region();
        reg.setId(110108);
        reg.setName("海淀");
        reglist.add(reg);
        
        reg = new Region();
        reg.setId(110109);
        reg.setName("门头沟");
        reglist.add(reg);
        
        reg = new Region();
        reg.setId(110111);
        reg.setName("房山");
        reglist.add(reg);

        reg = new Region();
        reg.setId(110112);
        reg.setName("通州");
        reglist.add(reg);
        
        reg = new Region();
        reg.setId(110113);
        reg.setName("顺义");
        reglist.add(reg);

        reg = new Region();
        reg.setId(110114);
        reg.setName("昌平");
        reglist.add(reg);

        reg = new Region();
        reg.setId(110115);
        reg.setName("大兴");
        reglist.add(reg);

        reg = new Region();
        reg.setId(110116);
        reg.setName("怀柔");
        reglist.add(reg);

        reg = new Region();
        reg.setId(110117);
        reg.setName("平谷");
        reglist.add(reg);

        reg = new Region();
        reg.setId(110228);
        reg.setName("密云");
        reglist.add(reg);
        
        reg = new Region();
        reg.setId(110229);
        reg.setName("延庆");
        reglist.add(reg);
        
        reg = new Region();
        reg.setId(0);
        reg.setName("北京周边");
        reglist.add(reg);
    	
    	return reglist;
    }
	
}
