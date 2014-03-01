/**
 * 
 */
package com.f5.ourfarm.vsm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

/**
 * @author tianhao
 *
 */
public class Statistics {
	//HashMap<String word ,Double idf>
	public static HashMap<String ,Double > map4IDF = new HashMap<String ,Double >();
	
	//HashMap<String word ,Set<String>>;set集合的大小即包含此word的doc数目
	public static HashMap<String ,Set<String > > map4word = new HashMap<String ,Set<String>>();
	
	//HashMap<String DocName ,ArrayList<String word>>
	public static HashMap<String ,ArrayList<String>> docs = new HashMap<String ,ArrayList<String>>();
	
	//HashMap<String class ,ArrayList<String word>>
	public static HashMap<String ,ArrayList<String>> classification = new HashMap<String ,ArrayList<String>>();
	
	//总文档数目
	static double totol ;
	
	/* 加载docs,并轮询处理所有文档，统计文档相关word的idf值；*/
	public static  void statisticsDocs(){
		//加载docs
		loadDocs();
		//轮询处理所有文档，统计文档相关word的idf值；
		Iterator<Entry<String, ArrayList<String>>> iterDoc = docs.entrySet().iterator();
		while (iterDoc.hasNext()) {
			Entry<String, ArrayList<String>> entryDoc = (Entry<String, ArrayList<String>>) iterDoc
					.next();
			// 文档名，用于统计包含某word的文档数目
			String tempDocName = entryDoc.getKey();
			ArrayList<String> tempDoc = entryDoc.getValue();
			Set<String> tempSet;

			for (int i = 0; i < tempDoc.size(); i++) {
				String word = tempDoc.get(i);
				/**
				 * 如果map4word中包含此word，则在此word的set集合中增加tempDocName；
				 * 若不存在则新建set集合并写入tempDocName
				 */
				if (map4word.containsKey(word)) {
					tempSet = map4word.get(word);
					tempSet.add(tempDocName);
				} else {
					tempSet = new HashSet<String>();
					tempSet.add(tempDocName);
					map4word.put(word, tempSet);
				}

			}

			
			// 在map4IDF中初始化idf值；
			Iterator<Entry<String, Set<String>>> iter = map4word.entrySet()
					.iterator();
			while (iter.hasNext()) {
				Entry<String, Set<String>> entry = (Entry<String, Set<String>>) iter
						.next();
				String word = entry.getKey();
				double num = entry.getValue().size();
				map4IDF.put(word, num);
			}
		}
		System.out.println("ok!");
	}
	
	private static void loadDocs() {
		// TODO Auto-generated method stub
		totol = 5;
		PorterStemmer s = new PorterStemmer();
		
		String longjiale = "农家乐";
		String fengjing = "风景";
		String docljl ="农家院 农家乐 住宿 营业 食宿 饭菜 路线";
		String docfj ="景区 门票 路线 ";
		docljl = s.getStemmer(docljl);
		docfj = s.getStemmer(docfj);
		String[] sljl = docljl.split(" ");
		String[] sfj = docfj.split(" ");
		ArrayList<String> listljl = new ArrayList<String>();
		for (int i = 0; i < sljl.length; i++) {
			listljl.add(sljl[i]);
		}
		ArrayList<String> listfj = new ArrayList<String>();
		for (int i = 0; i < sfj.length; i++) {
			listfj.add(sfj[i]);
		}
		classification.put(longjiale, listljl);
		classification.put(fengjing, listfj);
		
		String docname1 = "云蒙峡";
		String docname2 = "云居寺";
		String docname3 = "大山农家院";
		String doc1 = "云蒙峡 密云区 水堡子 民俗村 峡谷 景区 门票 景区 山顶 大峡谷 路线 密云 水库 黑龙潭 ";
		String doc2 = "云居寺 房山区 大石窝镇 景区 拒马河 石经山 藏经洞 文物保护单位 文化遗产 千年 门票";
		String doc3 = "大山 农家院 怀柔 黄花城 民俗村 营业 住宿 饭菜 食宿 农家乐";
		doc1= s.getStemmer(doc1);
		doc2= s.getStemmer(doc2);
		doc3= s.getStemmer(doc3);
		String[] s1 = doc1.split(" ");
		String[] s2 = doc2.split(" ");
		String[] s3 = doc3.split(" ");
		ArrayList<String> list1 = new ArrayList<String>();
		for (int i = 0; i < s1.length; i++) {
			list1.add(s1[i]);
		}
		ArrayList<String> list2 = new ArrayList<String>();
		for (int i = 0; i < s2.length; i++) {
			list2.add(s2[i]);
		}
		ArrayList<String> list3 = new ArrayList<String>();
		for (int i = 0; i < s3.length; i++) {
			list3.add(s3[i]);
		}
		docs.put(docname1, list1);
		docs.put(docname2, list2);
		docs.put(docname3, list3);
	}

	/* 取出word的idf值，若word不存在则初始化*/
	public static  double getIDF(String word){
		return map4IDF.containsKey(word)?map4IDF.get(word):map4IDF.put(word, totol);
	}

	/* 设置word的idf值*/
	public void setIDF(String word ){
		//word对应的set集合的大小即包含此word的doc数目
		double num = map4word.get(word).size();
		map4IDF.put(word, totol/num);
	}
	
	
}
