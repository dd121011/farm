package com.webcontent.htmlconvert.xslt;
import java.io.File;
import java.io.FileReader;
import java.io.StringReader;
import java.util.Calendar;
import java.util.Properties;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.html.dom.HTMLDocumentImpl;
import org.cyberneko.html.parsers.DOMFragmentParser;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Node;
import org.w3c.dom.html.HTMLDocument;
import org.xml.sax.InputSource;
public class htmltoxml {

	/*public static void main(String args[]){
        //if(args!=null&&args.length>=2){
            try {
                String path="E:/pageclawer/source1.htm";
               // String fromfile=args[1];
                String outputfile="E:/pageclawer/source_new1.htm";
                boolean b=true;
                htmltoxml h2x=new htmltoxml();
                DocumentFragment df=h2x.getSourceNode(path,b);
               // System.out.println(df.getTextContent());
                File file=new File(outputfile);
                if(file.exists())
                    file.delete();
                h2x.genXmlFile(df);
                System.out.println("generate "+file.getCanonicalPath()+" successfully!");   
            } catch (Exception e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

    }*/

    public void genXmlFile(Node output,String outFile) throws Exception,Error{
            TransformerFactory tf=TransformerFactory.newInstance();
            Transformer transformer=tf.newTransformer();
            DOMSource source=new DOMSource(output);
            java.io.FileOutputStream fos=new java.io.FileOutputStream(outFile);
            HTMLDocument document = new HTMLDocumentImpl();
            DocumentFragment fragment = document.createDocumentFragment();
            DOMResult result=new DOMResult(fragment);
            StreamResult fileresult = new StreamResult(fos);
            Properties props = new Properties();
            props.setProperty("encoding", "utf-8");
            props.setProperty("method", "xml");
            props.setProperty("omit-xml-declaration", "yes");
            transformer.setOutputProperties(props);
          //  transformer.transform(source,result);
            transformer.transform(source,fileresult);
            fos.close();
           // return result;
    }

    public DocumentFragment getSourceNode(String path,boolean fromfile) throws Exception,Error{
        DOMFragmentParser parser = new DOMFragmentParser();
        HTMLDocument document = new HTMLDocumentImpl();
        DocumentFragment fragment = document.createDocumentFragment();
            if(path!=null&&!path.trim().equals(""))
            {
                    //File input = new File(path);
                    StringReader fr=new StringReader(path);
                    InputSource is=new InputSource(fr);
                    parser.parse(is,fragment);
                    fr.close();

                return fragment;
            }else{
                return null;
            }

    }
    public DocumentFragment getSourceNode(String path) throws Exception,Error{
        DOMFragmentParser parser = new DOMFragmentParser();
        HTMLDocument document = new HTMLDocumentImpl();
        DocumentFragment fragment = document.createDocumentFragment();
            if(path!=null&&!path.trim().equals(""))
            {
                    File input = new File(path);
                    FileReader fr=new FileReader(path);
                    InputSource is=new InputSource(fr);
                    parser.parse(is,fragment);
                    fr.close();
                return fragment;
            }else{
                return null;
            }

    }
    public static String getFileName() throws Exception{
        Calendar c=Calendar.getInstance();
        String name="tmp"+c.get(Calendar.YEAR)+(c.get(Calendar.MONTH)<9?"0":"")+
                (c.get(Calendar.MONTH)+1)+(c.get(Calendar.DAY_OF_MONTH)<10?"0":"")+
                c.get(Calendar.DAY_OF_MONTH)+(c.get(Calendar.HOUR_OF_DAY)<10?"0":"")+
                c.get(Calendar.HOUR_OF_DAY)+(c.get(Calendar.MINUTE)<10?"0":"")+
                c.get(Calendar.MINUTE)+(c.get(Calendar.SECOND)<10?"0":"")+
                c.get(Calendar.SECOND)+(c.get(Calendar.MILLISECOND)<10?"0":"")+
                (c.get(Calendar.MILLISECOND)<100?"0":"")+c.get(Calendar.MILLISECOND);
        return name;
    }
    
}
