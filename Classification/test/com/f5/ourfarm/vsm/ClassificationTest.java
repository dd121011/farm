/**
 * 
 */
package com.f5.ourfarm.vsm;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;

import org.junit.Test;

/**
 * @author tianhao
 *
 */
public class ClassificationTest {

	/**
	 * Test method for {@link com.f5.ourfarm.vsm.Classification#classify(java.util.ArrayList)}.
	 */
	@Test
	public void testClassify() {
//		fail("Not yet implemented");
		Classification cl = new Classification();
		Statistics.statisticsDocs();
		//��ѯ���������ĵ���
		Iterator<Entry<String, ArrayList<String>>> iterDoc = Statistics.docs.entrySet().iterator();
		while (iterDoc.hasNext()) {
			Entry<String, ArrayList<String>> entryDoc = (Entry<String, ArrayList<String>>) iterDoc
					.next();
			// �ĵ���������ͳ�ư���ĳword���ĵ���Ŀ
			String tempDocName = entryDoc.getKey();
			System.out.print("�ԣ�"+tempDocName+"�ķ�����Ϊ��");
			cl.classify(entryDoc.getValue());
			}
		
	}

}
