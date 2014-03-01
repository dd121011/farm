package com.webcontent.task.changshajingdain;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.webcontent.htmlconvert.xslt.XsltTest;
import com.webcontent.htmlconvert.xslt.htmltoxml;
import com.webcontent.task.changshajingdain.*;

public class crawlerTask extends Thread {
	String htmlsource;
	String url;
	public static hunan hn = new hunan();
	public crawlerTask(String html)
	{
		htmlsource = html;
	}
	public crawlerTask(String url,String html)
	{
		htmlsource = html;
		this.url = url;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		htmltoxml h2x=new htmltoxml();
		XsltTest xslt = new XsltTest();
		DocumentFragment df;
		String filename = "";
		try {
			filename = "F:/0project/tmp/"+h2x.getFileName()+".xml";
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			df = h2x.getSourceNode(htmlsource,true);
			
			h2x.genXmlFile(df,filename);
			
			//System.out.println(hn.getName());
			//System.out.println("htmlsource="+htmlsource);
			//System.out.println("df="+result.getNode());
			DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
			   // domFactory.setNamespaceAware(true); // never forget this!
			DocumentBuilder builder = domFactory.newDocumentBuilder();
			Document doc = builder.parse(filename);
			//System.out.println("name="+xslt.getXPathContent(doc,hn.getName()));
			String name = xslt.getXPathContent(doc,hn.getName()).replaceAll("\\n", "").trim();
			String address = xslt.getXPathContent(doc,hn.getAddress()).replaceAll("\\n", "").trim();
			String region_code = "430112";
			String phone  = "";
			String destination_type = "¾°µã";
			String price = xslt.getXPathContent(doc,hn.getPrice()).replaceAll("\\n", "").trim();
			String pic= xslt.getXPathProValue(doc,hn.getPic(), "src").replaceAll("\\n", "").trim();
			String bus = "";
			String car= "";
			String introduction  = xslt.getXPathContent(doc,hn.getIntroduction()).replaceAll("\\n", "").trim();
			String hot100= "";
			String hot80= "";
			String hot60= "";
			String hot40= "";
			String hot20= "";
			String[] pic1= {"","","",""};
			XPathFactory factory = XPathFactory.newInstance();
		    XPath xpath = factory.newXPath();
		    XPathExpression expr = xpath.compile(hn.getPic1());

		    Object result = expr.evaluate(doc,XPathConstants.NODESET);
		    NodeList nodes = (NodeList) result;
		    int length = nodes.getLength()>4?4:nodes.getLength();
		    for (int i = 0; i < length; i++) {
		    	pic1[i] = nodes.item(i).getAttributes().getNamedItem("src").getNodeValue();
		        
		    }
		    //pic = pic1[0];
		    try {
				writeDate( name,
						 address,
						 region_code,
						 phone,
						 destination_type,
						 price,
						 pic,
						 bus,
						 car,
						 introduction,
						 hot100,
						 hot80,
						 hot60,
						 hot40,
						 hot20,
						 pic1[0],pic1[1],pic1[2],pic1[3]);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Error e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		super.run();
	}
	public synchronized void writeDate(String name,
			String address,
			String region_code,
			String phone,
			String destination_type,
			String price,
			String pic,
			String bus,
			String car,
			String introduction,
			String hot100,
			String hot80,
			String hot60,
			String hot40,
			String hot20,
			String pic1,String pic2,String pic3,String pic4) throws IOException
			{
				File file = new File("F:/0project/date/"+region_code+".csv");
				if(!file.exists())
					file.createNewFile();
				BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
				//double hot = 0.08*Double.valueOf(hot80.replaceAll("%",""))+0.06*Double.valueOf(hot60.replaceAll("%",""))+0.04*Double.valueOf(hot40.replaceAll("%",""))+0.02*Double.valueOf(hot20.replaceAll("%",""));
				double hot = 0;
				writer.write(name+","+address.replaceAll(",", " ")+","+pic+","+introduction.replaceAll(",", " ")+","+destination_type+",,,,"+phone.replaceAll(",", " ")+",,,"+hot+","+price+","+price+",,"+bus.replaceAll(",", " ")+","+car.replaceAll(",", " ")+",0,"+destination_type.replaceAll("±êÇ©£º", "").replaceAll(" ", "&")+",1,"+region_code+",,0,0,0,0,0,"+pic1+","+pic2+","+pic3+","+pic4+"\n");
				writer.flush();
				writer.close();
			}

}
