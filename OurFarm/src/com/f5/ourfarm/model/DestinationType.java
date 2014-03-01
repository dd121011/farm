package com.f5.ourfarm.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.f5.ourfarm.util.Tools;

/**
 * 景点类型
 * 
 * @author lify
 *
 */
public class DestinationType implements Serializable {

	private static final long serialVersionUID = 4803422479907195327L;
	
	//存放景点类型
	private static List<RankingParams> type = new ArrayList<RankingParams>();
	
	static {
		type.add(new RankingParams(1, 1, "景点"));
		type.add(new RankingParams(1, 2, "农家乐"));
		type.add(new RankingParams(1, 3, "山庄"));
		type.add(new RankingParams(1, 4, "农产品"));       
		type.add(new RankingParams(2,20, "度假休闲"));
		type.add(new RankingParams(2,21, "乡村市集"));
		type.add(new RankingParams(2,7, "亲子"));
		type.add(new RankingParams(2,4, "采摘"));
		type.add(new RankingParams(2,2, "爬山"));
		type.add(new RankingParams(2,5, "垂钓"));
	}
	
	
	/**
     * 根据名称返回排名参数对象
     * 
     * @return
     */
    public static RankingParams getKeyFromTypeName(String name) {
    	if(Tools.isEmpty(name)) return null; 
    	for (RankingParams rp : type) {
			if(name.equals(rp.getName())) return rp;
		}
    	
    	return null;
    }
    
    /**
     * 弹出窗口显示的景点类型名称
     * 
     * @return
     */
    public static List<String> getGroupList() {
    	List<String> showPopwindowName = new ArrayList<String>();
    	for (RankingParams rp : type) {
    		showPopwindowName.add(rp.getName());
		}
    	
    	return showPopwindowName;
    }

}
