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
	
	//HashMap<String word ,Set<String>>;set���ϵĴ�С��������word��doc��Ŀ
	public static HashMap<String ,Set<String > > map4word = new HashMap<String ,Set<String>>();
	
	//HashMap<String DocName ,ArrayList<String word>>
	public static HashMap<String ,ArrayList<String>> docs = new HashMap<String ,ArrayList<String>>();
	
	//HashMap<String class ,ArrayList<String word>>
	public static HashMap<String ,ArrayList<String>> classification = new HashMap<String ,ArrayList<String>>();
	
	//���ĵ���Ŀ
	static double totol ;
	
	/* ����docs,����ѯ���������ĵ���ͳ���ĵ����word��idfֵ��*/
	public static  void statisticsDocs(){
		//����docs
		loadDocs();
		//��ѯ���������ĵ���ͳ���ĵ����word��idfֵ��
		Iterator<Entry<String, ArrayList<String>>> iterDoc = docs.entrySet().iterator();
		while (iterDoc.hasNext()) {
			Entry<String, ArrayList<String>> entryDoc = (Entry<String, ArrayList<String>>) iterDoc
					.next();
			// �ĵ���������ͳ�ư���ĳword���ĵ���Ŀ
			String tempDocName = entryDoc.getKey();
			ArrayList<String> tempDoc = entryDoc.getValue();
			Set<String> tempSet;

			for (int i = 0; i < tempDoc.size(); i++) {
				String word = tempDoc.get(i);
				/**
				 * ���map4word�а�����word�����ڴ�word��set����������tempDocName��
				 * �����������½�set���ϲ�д��tempDocName
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

			
			// ��map4IDF�г�ʼ��idfֵ��
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
		
		String longjiale = "ũ����";
		String fengjing = "�羰";
		String docljl ="ũ��Ժ ũ���� ס�� Ӫҵ ʳ�� ���� ·��";
		String docfj ="���� ��Ʊ ·�� ";
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
		
		String docname1 = "����Ͽ";
		String docname2 = "�ƾ���";
		String docname3 = "��ɽũ��Ժ";
		String doc1 = "����Ͽ ������ ˮ���� ���״� Ͽ�� ���� ��Ʊ ���� ɽ�� ��Ͽ�� ·�� ���� ˮ�� ����̶ ";
		String doc2 = "�ƾ��� ��ɽ�� ��ʯ���� ���� ����� ʯ��ɽ �ؾ��� ���ﱣ����λ �Ļ��Ų� ǧ�� ��Ʊ";
		String doc3 = "��ɽ ũ��Ժ ���� �ƻ��� ���״� Ӫҵ ס�� ���� ʳ�� ũ����";
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

	/* ȡ��word��idfֵ����word���������ʼ��*/
	public static  double getIDF(String word){
		return map4IDF.containsKey(word)?map4IDF.get(word):map4IDF.put(word, totol);
	}

	/* ����word��idfֵ*/
	public void setIDF(String word ){
		//word��Ӧ��set���ϵĴ�С��������word��doc��Ŀ
		double num = map4word.get(word).size();
		map4IDF.put(word, totol/num);
	}
	
	
}
