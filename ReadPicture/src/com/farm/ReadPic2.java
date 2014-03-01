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

public class ReadPic2 {

	// ������̬ȫ�ֱ���  
    private static Connection conn;
    private static final Logger logger = Logger.getLogger(ReadPic2.class);
    private static final String READ_PIC_ROOT = "D:/ReadPicture/hunan";
    
    //��destination���ж�ȡ
    private static final String sql = "SELECT picture_id, destination_id, url, type " +
    		" FROM travel_hunan.picture ";// +
    		//" where url like '%njl8.com%' and picture_id > 3535 ";
  
	/**
	 * ��ȡ���ݿ���ͼƬ��ַ����������
	 * 
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		ReadPic2 readpic = new ReadPic2();
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
            	int picture_id = rs.getInt("picture_id");  
                int destinationId = rs.getInt("destination_id");  
            	int type = rs.getInt("type");  
                String url = rs.getString("url");
                
                if(!checkUrl(url)) {
                	logger.error(getLogMsg(picture_id, destinationId, type, url, "url��ʽ����"));
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
            		
            		save(path,picture_id,destinationId, type, url);
                }
                  
                //����鵽�ļ�¼�ĸ����ֶε�ֵ  
                System.out.println(picture_id + " " + destinationId + " " + type + " " + url);  
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
	 * 
	 */
	private static String getLogMsg(int picture_id, int destinationId, int type, String url, String msg) {
		return msg + ",picture_id:" + picture_id+",destinationId:" + destinationId+ ",type:" + type + ",url:" + url;
	}
	
	/**
	 * ����ͼƬ
	 * 
	 * @param url
	 * @throws Exception
	 */
	static void save(String path, int picture_id, int destinationId, int type, String url) throws Exception {
		String fileName = url.substring(url.lastIndexOf("/"));
		String filePath = path + File.separator + fileName;
		
		File desPathFile = new File(filePath);
		if(desPathFile.exists()) {
			return;
		}
		
		BufferedOutputStream out = null;
		byte[] bit = getByte(picture_id,destinationId, type, url);
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
	static byte[] getByte(int picture_id, int destinationId, int type, String url) throws Exception {
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
			logger.error(getLogMsg(picture_id, destinationId, type, url, "��ȡͼƬʧ��"));
		} finally {
			client.getConnectionManager().shutdown();
		}
		return new byte[0];
	}
}
