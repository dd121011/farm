package com.f5.ourfarm.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.f5.ourfarm.util.Tools;

/**
 * ��������
 * 
 * @author lify
 *
 */
public class DestinationType implements Serializable {

	private static final long serialVersionUID = 4803422479907195327L;
	
	//��ž�������
	private static List<RankingParams> type = new ArrayList<RankingParams>();
	
	static {
		type.add(new RankingParams(1, 1, "����"));
		type.add(new RankingParams(1, 2, "ũ����"));
		type.add(new RankingParams(1, 3, "ɽׯ"));
		type.add(new RankingParams(1, 4, "ũ��Ʒ"));       
		type.add(new RankingParams(2,20, "�ȼ�����"));
		type.add(new RankingParams(2,21, "����м�"));
		type.add(new RankingParams(2,7, "����"));
		type.add(new RankingParams(2,4, "��ժ"));
		type.add(new RankingParams(2,2, "��ɽ"));
		type.add(new RankingParams(2,5, "����"));
	}
	
	
	/**
     * �������Ʒ���������������
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
     * ����������ʾ�ľ�����������
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
