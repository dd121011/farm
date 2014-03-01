package com.webcontent.htmlconvert.xslt;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XsltTest {

	public static String getXPathContent(Document doc,String xml) throws XPathExpressionException, ParserConfigurationException, SAXException, IOException
	{ 
        XPathFactory factory = XPathFactory.newInstance();
	    XPath xpath = factory.newXPath();
	    XPathExpression expr = xpath.compile(xml);

	    Object result = expr.evaluate(doc,XPathConstants.NODESET);
	    NodeList nodes = (NodeList) result;
	    //System.out.println(nodes.getLength());
	    String rs = "";
	    for (int i = 0; i < nodes.getLength(); i++) {
	    	rs += nodes.item(i).getTextContent(); 
	    }
        
		return rs;   
	}
	public static String getXPathProValue(Document doc,String xml,String proname) throws XPathExpressionException, ParserConfigurationException, SAXException, IOException
	{ 
        XPathFactory factory = XPathFactory.newInstance();
	    XPath xpath = factory.newXPath();
	    XPathExpression expr = xpath.compile(xml);

	    Object result = expr.evaluate(doc,XPathConstants.NODESET);
	    NodeList nodes = (NodeList) result;
	    //System.out.println(nodes.getLength());
	    String rs = "";
	    for (int i = 0; i < nodes.getLength(); i++) {
	    	rs += nodes.item(i).getAttributes().getNamedItem(proname).getNodeValue(); 
	    }
		return rs;   
	}
	
	 public static void main(String[] args) 
			   throws ParserConfigurationException, SAXException, 
			          IOException, XPathExpressionException {
		 		XsltTest xt = new XsltTest();
			    DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
			    //domFactory.setNamespaceAware(true); // never forget this!
			    DocumentBuilder builder = domFactory.newDocumentBuilder();
			    Document doc = builder.parse("F:/0project/tmp/tmp20130326185229307.xml");
			   // hunan aaa = new hunan();
			    
			    System.out.println(xt.getXPathProValue(doc, "/HTML/BODY/DIV[2]/DIV[6]/DIV/DIV[1]/UL/LI/IMG", "src"));
			    /*XPathFactory factory = XPathFactory.newInstance();
			    XPath xpath = factory.newXPath();
			    XPathExpression expr = xpath.compile(aaa.getIntroduction());

			    Object result = expr.evaluate(doc,XPathConstants.NODESET);
			    NodeList nodes = (NodeList) result;
			    System.out.println(nodes.getLength());
			    for (int i = 0; i < nodes.getLength(); i++) {
			        System.out.println(nodes.item(i).getTextContent().replaceAll("\\n", ""));
			        
			    }*/
		
			  }
}
