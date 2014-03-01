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
		//轮询处理所有文档；
		Iterator<Entry<String, ArrayList<String>>> iterDoc = Statistics.docs.entrySet().iterator();
		while (iterDoc.hasNext()) {
			Entry<String, ArrayList<String>> entryDoc = (Entry<String, ArrayList<String>>) iterDoc
					.next();
			// 文档名，用于统计包含某word的文档数目
			String tempDocName = entryDoc.getKey();
			System.out.print("对："+tempDocName+"的分类结果为：");
			cl.classify(entryDoc.getValue());
			}
		
	}

}
