/**
 * 
 */
package com.f5.ourfarm.vsm;



import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author tianhao
 *
 */
public class Classification {
	
	
	
	public String classify(ArrayList<String> wordList){
		String className = "this doc's classification";
		HashMap<String ,Double> map4vsmValue = new HashMap<String ,Double>();
		// ��doc�ֱ��� ��������������ƶȣ�
		Iterator<Entry<String, ArrayList<String>>> iterClass = Statistics.classification.entrySet().iterator();
		while (iterClass.hasNext()) {
			Entry<String, ArrayList<String>> entryDoc = (Entry<String, ArrayList<String>>) iterClass
					.next();
			String classificationName = entryDoc.getKey();
			ArrayList<String> wordList4classification = entryDoc.getValue();
			//�������ƶ�vsm��ֵ
			CosSimilarity cosSimilarity = new CosSimilarity(wordList ,wordList4classification);
			double vsmValue = cosSimilarity.Similarity();
//			����vsm��ֵ
			map4vsmValue.put(classificationName, vsmValue);
		}
		//�����ƶȽ�������
		List<Map.Entry<String, Double>> infoIds =
			    new ArrayList<Map.Entry<String, Double>>(map4vsmValue.entrySet());
		//�����㷨
		Collections.sort(infoIds, new Comparator<Map.Entry<String, Double>>() {   
		    public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {      
		    	if ((o2.getValue() - o1.getValue())>0)  
		            return 1;  
		          else if((o2.getValue() - o1.getValue())==0)  
		            return 0;  
		          else   
		            return -1; 
		    }
		}); 
		//������λ�ļ���doc�����
		className = infoIds.get(0).getKey();
		System.out.println(className);
		return className;
	}
}
