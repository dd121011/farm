/**
 * 
 */
package com.f5.ourfarm.vsm;

import java.util.ArrayList;

/**
 * @author tianhao
 *
 */
public class CosSimilarity {
	
	ArrayList<String> a = new ArrayList<String>(); // 创建动态数组,记录不同的单词
	ArrayList<Double> aTDIDF = new ArrayList<Double>(); // 创建动态数组,统计不同的单词的tf-idf值
	ArrayList<String> b = new ArrayList<String>();
	ArrayList<Double> bTDIDF = new ArrayList<Double>();
	
	CosSimilarity(ArrayList<String> textA ,ArrayList<String> textB){
		a = textA ;
		b = textB ;
	}
	
	void initValue4TFIDF() {
		// tdidf统计数组初始化
		for (int i = 0; i < a.size(); i++) {
			aTDIDF.add(i, (double) 1);
		}
		for (int i = 0; i < b.size(); i++) {
			bTDIDF.add(i, (double) 1);
		}
		//计算TDIDF值
		for (int i = 0; i < a.size() - 1; i++) {
			int tem = 1; // -----------------------暂存词频
			for (int j = i + 1; j < a.size(); j++) {
				// 记录a不同单词且统计词频
				if (a.get(i).equals(a.get(j))) {
					tem++;
					aTDIDF.set(i, (double) tem);
					a.remove(j);
					aTDIDF.remove(j);
				}
				//计算td值
				double tf = (double)tem/a.size();
				//计算idf值
				double idf = Statistics.getIDF(a.get(i));
				double tfidf = tf*idf;
				aTDIDF.set(i, tfidf);
			}
		}
		for (int i = 0; i < b.size() - 1; i++) {
			int tem = 1; // -----------------------暂存词频
			for (int j = i + 1; j < b.size(); j++) {
				// 记录a不同单词且统计词频
				if (b.get(i).equals(b.get(j))) {
					tem++;
					bTDIDF.set(i, (double) tem);
					b.remove(j);
					bTDIDF.remove(j);
				}
				//计算td值
				double tf = (double)tem/b.size();
				//计算idf值
				double idf = Statistics.getIDF(b.get(i));
				double tfidf = tf*idf;
				bTDIDF.set(i, tfidf);
			}
		}
	}
	
	public double Similarity() {
		
		double denominator = 0; // 计算W1K×W2K
		double Similarity = 0;

		initValue4TFIDF();
		for (int i = 0; i < a.size(); i++) // 计算W1K×W2K
		{
			for (int j = 0; j < b.size(); j++) {
				if (a.get(i).equals(b.get(j)))
					denominator += ((double) aTDIDF.get(i) * (double) bTDIDF.get(j));

			}
		}

		double sqW1 = 0, sqW2 = 0; // 计算两个向量的模
		for (int i = 0; i < aTDIDF.size(); i++) {
			sqW1 += (double) aTDIDF.get(i) * (double) aTDIDF.get(i);
		}

		for (int i = 0; i < bTDIDF.size(); i++) {
			sqW2 += (double) bTDIDF.get(i) * (double) bTDIDF.get(i);
		}

		Similarity = denominator / Math.sqrt(sqW1 * sqW2); // 输出结果
//		System.out.println("相似度是："+Similarity);
		return Similarity;
	}

	public ArrayList<String> getA() {
		return a;
	}

	public void setA(ArrayList<String> a) {
		this.a = a;
	}

	public ArrayList<Double> getaTDIDF() {
		return aTDIDF;
	}

	public void setaTDIDF(ArrayList<Double> aTDIDF) {
		this.aTDIDF = aTDIDF;
	}

	public ArrayList<String> getB() {
		return b;
	}

	public void setB(ArrayList<String> b) {
		this.b = b;
	}

	public ArrayList<Double> getbTDIDF() {
		return bTDIDF;
	}

	public void setbTDIDF(ArrayList<Double> bTDIDF) {
		this.bTDIDF = bTDIDF;
	}
	
}
