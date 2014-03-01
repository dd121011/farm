package com.f5.ourfarm.vsm;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

public class CosSimilarityTest {

	@Test
	public void test() {
//		fail("Not yet implemented");


		
		// TODO Auto-generated method stub
		String D1 = "���� ũ���� �ҳ��� ��ɫ�� ";
		String D2 = "�ƾ��� ���� ũ���� ��ɽ�� ũ���� �״�ɽ ũ���� �Ϸ�ɽ ũ���� ����� ũ���� �ػ� ũ���� ����֮�� ũ���� �ҳ��� ��ɫ�� ����";
		PorterStemmer s = new PorterStemmer(); // ���ʴ��ι淶��
		D1 = s.getStemmer(D1);
		D2 = s.getStemmer(D2);
		String[] s1, s2;
		s1 = D1.split(" ");
		s2 = D2.split(" ");
		// ���Խ��е��ʴ��ι淶�� System.out.println(D1);
		// System.out.println(D2);

		ArrayList a = new ArrayList(); // ������̬����,��¼��ͬ�ĵ���
		ArrayList<Integer> aNum = new ArrayList<Integer>(); // ������̬����,ͳ�Ʋ�ͬ�ĵ��ʸ��Գ��ֵĴ���
		ArrayList b = new ArrayList();
		ArrayList<Integer> bNum = new ArrayList<Integer>();

		for (int i = 0; i < s1.length; i++) // ��s1���Ƶ���̬����a, �Ҵ�Ƶͳ�������ʼ��
		{
		a.add(s1[i]);
		aNum.add(i, 1);
		}


		for (int i = 0; i < a.size() - 1; i++) // ��¼a��ͬ������ͳ�ƴ�Ƶ
		{
		int tem = 1; // -----------------------�ݴ��Ƶ
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

		for (int i = 0; i < s2.length; i++) // ��s2���Ƶ���̬����b, �Ҵ�Ƶͳ�������ʼ��
		{
		b.add(s2[i]);
		bNum.add(i, 1);
		}

		for (int i = 0; i < b.size() - 1; i++) // ��¼b��ͬ������ͳ�ƴ�Ƶ
		{
		int tem = 1; // -----------------------�ݴ��Ƶ
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

		double denominator = 0; // ����W1K��W2K

		for (int i = 0; i < a.size(); i++) // ����W1K��W2K
		{
		for (int j = 0; j < b.size(); j++)
		{
		if (a.get(i).equals(b.get(j)))
		denominator += ((double) aNum.get(i) * (double) bNum.get(j));

		}
		}

		double sqW1 = 0, sqW2 = 0; // ��������������ģ
		for (int i = 0; i < aNum.size(); i++)
		{
		sqW1 += (double) aNum.get(i) * (double) aNum.get(i);
		}

		for (int i = 0; i < bNum.size(); i++)
		{
		sqW2 += (double) bNum.get(i) * (double) bNum.get(i);
		}

		System.out.println("�������ƶ�Ϊ" + denominator / Math.sqrt(sqW1 * sqW2)); // ������

		}

	

}
