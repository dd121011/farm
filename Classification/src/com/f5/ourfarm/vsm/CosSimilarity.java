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
	
	ArrayList<String> a = new ArrayList<String>(); // ������̬����,��¼��ͬ�ĵ���
	ArrayList<Double> aTDIDF = new ArrayList<Double>(); // ������̬����,ͳ�Ʋ�ͬ�ĵ��ʵ�tf-idfֵ
	ArrayList<String> b = new ArrayList<String>();
	ArrayList<Double> bTDIDF = new ArrayList<Double>();
	
	CosSimilarity(ArrayList<String> textA ,ArrayList<String> textB){
		a = textA ;
		b = textB ;
	}
	
	void initValue4TFIDF() {
		// tdidfͳ�������ʼ��
		for (int i = 0; i < a.size(); i++) {
			aTDIDF.add(i, (double) 1);
		}
		for (int i = 0; i < b.size(); i++) {
			bTDIDF.add(i, (double) 1);
		}
		//����TDIDFֵ
		for (int i = 0; i < a.size() - 1; i++) {
			int tem = 1; // -----------------------�ݴ��Ƶ
			for (int j = i + 1; j < a.size(); j++) {
				// ��¼a��ͬ������ͳ�ƴ�Ƶ
				if (a.get(i).equals(a.get(j))) {
					tem++;
					aTDIDF.set(i, (double) tem);
					a.remove(j);
					aTDIDF.remove(j);
				}
				//����tdֵ
				double tf = (double)tem/a.size();
				//����idfֵ
				double idf = Statistics.getIDF(a.get(i));
				double tfidf = tf*idf;
				aTDIDF.set(i, tfidf);
			}
		}
		for (int i = 0; i < b.size() - 1; i++) {
			int tem = 1; // -----------------------�ݴ��Ƶ
			for (int j = i + 1; j < b.size(); j++) {
				// ��¼a��ͬ������ͳ�ƴ�Ƶ
				if (b.get(i).equals(b.get(j))) {
					tem++;
					bTDIDF.set(i, (double) tem);
					b.remove(j);
					bTDIDF.remove(j);
				}
				//����tdֵ
				double tf = (double)tem/b.size();
				//����idfֵ
				double idf = Statistics.getIDF(b.get(i));
				double tfidf = tf*idf;
				bTDIDF.set(i, tfidf);
			}
		}
	}
	
	public double Similarity() {
		
		double denominator = 0; // ����W1K��W2K
		double Similarity = 0;

		initValue4TFIDF();
		for (int i = 0; i < a.size(); i++) // ����W1K��W2K
		{
			for (int j = 0; j < b.size(); j++) {
				if (a.get(i).equals(b.get(j)))
					denominator += ((double) aTDIDF.get(i) * (double) bTDIDF.get(j));

			}
		}

		double sqW1 = 0, sqW2 = 0; // ��������������ģ
		for (int i = 0; i < aTDIDF.size(); i++) {
			sqW1 += (double) aTDIDF.get(i) * (double) aTDIDF.get(i);
		}

		for (int i = 0; i < bTDIDF.size(); i++) {
			sqW2 += (double) bTDIDF.get(i) * (double) bTDIDF.get(i);
		}

		Similarity = denominator / Math.sqrt(sqW1 * sqW2); // ������
//		System.out.println("���ƶ��ǣ�"+Similarity);
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
