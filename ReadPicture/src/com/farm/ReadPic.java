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

	// 创建静态全局变量  
    private static Connection conn;
    private static final Logger logger = Logger.getLogger(ReadPic.class);
    private static final String READ_PIC_ROOT = "D:/ReadPicture/hunan";
    
    //从destination表中读取
    private static final String sql = "select destination_id, name, pic from travel_hunan.destination";// where pic like '%njl8.com%' and destination_id > 840";  
  
	/**
	 * 读取数据库中图片地址，并做处理
	 * 
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		ReadPic readpic = new ReadPic();
		readpic.changeUrl();
	}
	
	/**
	 * 转换url
	 * @throws Exception 
	 */
	private void changeUrl() throws Exception {
		readUrl();
	}
	
	/**
	 * 从书库中读取url
	 * @throws Exception 
	 */
	private void readUrl() throws Exception {
		conn = DBUtil.getConnect();
		try(Statement st = (Statement) conn.createStatement()) {  
			   // 查询数据的sql语句  
            //创建用于执行静态sql语句的Statement对象，st属局部变量  
            ResultSet rs = st.executeQuery(sql);    //执行sql查询语句，返回查询数据的结果集  
            System.out.println("查询结果为：");  
            while (rs.next()) { // 判断是否还有下一个数据  
                  
                // 根据字段名获取相应的值  
                int destinationId = rs.getInt("destination_id");  
                String name = rs.getString("name");  
                String url = rs.getString("pic");
                
                if(!checkUrl(url)) {
                	logger.error(getLogMsg(destinationId, name, url, "url格式不对"));
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
                  
                //输出查到的记录的各个字段的值  
                System.out.println(destinationId + " " + name + " " + url);  
            }
        }
	}
	

	/**
	 * 检查url的有效性
	 * 
	 * @return
	 */
	private boolean checkUrl(String url) {
		return url == null || "".equals(url)? false : url.startsWith("http:");
	}
	
	/**
	 * 错误message信息
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
	 * 保存图片
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
	 * 返回图片流
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
			logger.error(getLogMsg(destinationId, name, url, "获取图片失败"));
		} finally {
			client.getConnectionManager().shutdown();
		}
		return new byte[0];
	}
}
