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
		// 将doc分别与 类别向量计算相似度；
		Iterator<Entry<String, ArrayList<String>>> iterClass = Statistics.classification.entrySet().iterator();
		while (iterClass.hasNext()) {
			Entry<String, ArrayList<String>> entryDoc = (Entry<String, ArrayList<String>>) iterClass
					.next();
			String classificationName = entryDoc.getKey();
			ArrayList<String> wordList4classification = entryDoc.getValue();
			//计算相似度vsm的值
			CosSimilarity cosSimilarity = new CosSimilarity(wordList ,wordList4classification);
			double vsmValue = cosSimilarity.Similarity();
//			保存vsm的值
			map4vsmValue.put(classificationName, vsmValue);
		}
		//对相似度进行排序，
		List<Map.Entry<String, Double>> infoIds =
			    new ArrayList<Map.Entry<String, Double>>(map4vsmValue.entrySet());
		//排序算法
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
		//排在首位的即此doc的类别
		className = infoIds.get(0).getKey();
		System.out.println(className);
		return className;
	}
}
