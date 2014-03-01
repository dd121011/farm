package com.f5.ourfarm.vsm;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

public class CosSimilarityTest {

	@Test
	public void test() {
//		fail("Not yet implemented");


		
		// TODO Auto-generated method stub
		String D1 = "北京 农家乐 家常菜 特色菜 ";
		String D2 = "云居寺 北京 农家乐 房山区 农家乐 白带山 农家乐 上方山 农家乐 拒马河 农家乐 敦煌 农家乐 世界之最 农家乐 家常菜 特色菜 促销";
		PorterStemmer s = new PorterStemmer(); // 单词词形规范化
		D1 = s.getStemmer(D1);
		D2 = s.getStemmer(D2);
		String[] s1, s2;
		s1 = D1.split(" ");
		s2 = D2.split(" ");
		// 测试进行单词词形规范化 System.out.println(D1);
		// System.out.println(D2);

		ArrayList a = new ArrayList(); // 创建动态数组,记录不同的单词
		ArrayList<Integer> aNum = new ArrayList<Integer>(); // 创建动态数组,统计不同的单词各自出现的次数
		ArrayList b = new ArrayList();
		ArrayList<Integer> bNum = new ArrayList<Integer>();

		for (int i = 0; i < s1.length; i++) // 将s1复制到动态数组a, 且词频统计数组初始化
		{
		a.add(s1[i]);
		aNum.add(i, 1);
		}


		for (int i = 0; i < a.size() - 1; i++) // 记录a不同单词且统计词频
		{
		int tem = 1; // -----------------------暂存词频
		for (int j = i + 1; j < a.size(); j++)
		{
		if (a.get(i).equals(a.get(j)))
		{
		tem++;
		aNum.set(i, tem);
		a.remove(j);
		aNum.remove(j);
		}
		}
		}

		for (int i = 0; i < s2.length; i++) // 将s2复制到动态数组b, 且词频统计数组初始化
		{
		b.add(s2[i]);
		bNum.add(i, 1);
		}

		for (int i = 0; i < b.size() - 1; i++) // 记录b不同单词且统计词频
		{
		int tem = 1; // -----------------------暂存词频
		for (int j = i + 1; j < b.size(); j++)
		{
		if (b.get(i).equals(b.get(j)))
		{
		tem++;
		bNum.set(i, tem);
		b.remove(j);
		bNum.remove(j);
		}
		}
		}

		double denominator = 0; // 计算W1K×W2K

		for (int i = 0; i < a.size(); i++) // 计算W1K×W2K
		{
		for (int j = 0; j < b.size(); j++)
		{
		if (a.get(i).equals(b.get(j)))
		denominator += ((double) aNum.get(i) * (double) bNum.get(j));

		}
		}

		double sqW1 = 0, sqW2 = 0; // 计算两个向量的模
		for (int i = 0; i < aNum.size(); i++)
		{
		sqW1 += (double) aNum.get(i) * (double) aNum.get(i);
		}

		for (int i = 0; i < bNum.size(); i++)
		{
		sqW2 += (double) bNum.get(i) * (double) bNum.get(i);
		}

		System.out.println("余弦相似度为" + denominator / Math.sqrt(sqW1 * sqW2)); // 输出结果

		}

	

}
