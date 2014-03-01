package com.farm;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.ResultSet;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.farm.util.DBUtil;
import com.mysql.jdbc.Statement;

public class ReadPic {

	// ������̬ȫ�ֱ���  
    private static Connection conn;
    private static final Logger logger = Logger.getLogger(ReadPic.class);
    private static final String READ_PIC_ROOT = "D:/ReadPicture/hunan";
    
    //��destination���ж�ȡ
    private static final String sql = "select destination_id, name, pic from travel_hunan.destination";// where pic like '%njl8.com%' and destination_id > 840";  
  
	/**
	 * ��ȡ���ݿ���ͼƬ��ַ����������
	 * 
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		ReadPic readpic = new ReadPic();
		readpic.changeUrl();
	}
	
	/**
	 * ת��url
	 * @throws Exception 
	 */
	private void changeUrl() throws Exception {
		readUrl();
	}
	
	/**
	 * ������ж�ȡurl
	 * @throws Exception 
	 */
	private void readUrl() throws Exception {
		conn = DBUtil.getConnect();
		try(Statement st = (Statement) conn.createStatement()) {  
			   // ��ѯ���ݵ�sql���  
            //��������ִ�о�̬sql����Statement����st���ֲ�����  
            ResultSet rs = st.executeQuery(sql);    //ִ��sql��ѯ��䣬���ز�ѯ���ݵĽ����  
            System.out.println("��ѯ���Ϊ��");  
            while (rs.next()) { // �ж��Ƿ�����һ������  
                  
                // �����ֶ�����ȡ��Ӧ��ֵ  
                int destinationId = rs.getInt("destination_id");  
                String name = rs.getString("name");  
                String url = rs.getString("pic");
                
                if(!checkUrl(url)) {
                	logger.error(getLogMsg(destinationId, name, url, "url��ʽ����"));
                } else {
                	String tempUrl = url.substring(7);
            		
            		String[] urlArr = tempUrl.split("/");
            		
            		String path = READ_PIC_ROOT;
            		for (int i = 0; i < urlArr.length - 1; i++) {
            			path = path + File.separator + urlArr[i];
            			File desPathFile = new File(path);
            			if(!desPathFile.exists()) {
            				desPathFile.mkdir();
            			}
            		}
            		
            		save(path,destinationId, name, url);
                }
                  
                //����鵽�ļ�¼�ĸ����ֶε�ֵ  
                System.out.println(destinationId + " " + name + " " + url);  
            }
        }
	}
	

	/**
	 * ���url����Ч��
	 * 
	 * @return
	 */
	private boolean checkUrl(String url) {
		return url == null || "".equals(url)? false : url.startsWith("http:");
	}
	
	/**
	 * ����message��Ϣ
	 * 
	 * @param destinationId
	 * @param name
	 * @param url
	 * @param msg
	 * @return
	 */
	private static String getLogMsg(int destinationId, String name, String url, String msg) {
		return msg + ",destinationId:" + destinationId+ ",name:" + name + ",url:" + url;
	}
	
	/**
	 * ����ͼƬ
	 * 
	 * @param url
	 * @throws Exception
	 */
	static void save(String path, int destinationId, String name, String url) throws Exception {
		String fileName = url.substring(url.lastIndexOf("/"));
		String filePath = path + File.separator + fileName;
		BufferedOutputStream out = null;
		byte[] bit = getByte(destinationId, name, url);
		if (bit.length > 0) {
			try {
				out = new BufferedOutputStream(new FileOutputStream(filePath));
				out.write(bit);
				out.flush();
				System.out.println("Create File success! [" + filePath + "]");
			} finally {
				if (out != null){
					out.close();
				}
			}
		}
	}
	
	/**
	 * ����ͼƬ��
	 * 
	 * @param uri
	 * @return
	 * @throws Exception
	 */
	static byte[] getByte(int destinationId, String name, String url) throws Exception {
		HttpClient client = new DefaultHttpClient();
		client.getParams().setParameter(
				CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
		HttpGet get = new HttpGet(url);
		get.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,
				5000);
		try {
			HttpResponse resonse = client.execute(get);
			if (resonse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity entity = resonse.getEntity();
				if (entity != null) {
					return EntityUtils.toByteArray(entity);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(getLogMsg(destinationId, name, url, "��ȡͼƬʧ��"));
		} finally {
			client.getConnectionManager().shutdown();
		}
		return new byte[0];
	}
}
