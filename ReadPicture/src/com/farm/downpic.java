package com.farm;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

public class downpic {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		String url1 = "http://www.shidutour.com/uploads/allimg/100312/1_100312231028_1.jpg";
		save(url1);
	}
	
	static void save(String url) throws Exception {
		String fileName = url.substring(url.lastIndexOf("/"));
		String filePath = "D:/ReadPicture" + "/" + fileName;
		BufferedOutputStream out = null;
		byte[] bit = getByte(url);
		if (bit.length > 0) {
			try {
				out = new BufferedOutputStream(new FileOutputStream(filePath));
				out.write(bit);
				out.flush();
				System.out.println("Create File success! [" + filePath + "]");
			} finally {
				if (out != null)
					out.close();
			}
		}
	}
	
	
	static byte[] getByte(String uri) throws Exception {
		HttpClient client = new DefaultHttpClient();
		client.getParams().setParameter(
				CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
		HttpGet get = new HttpGet(uri);
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
		} finally {
			client.getConnectionManager().shutdown();
		}
		return new byte[0];
	}
	
	
	/**
		     * 下载文件
		     *
		     * @param url
		     *            文件http地址
		     * @param dir
		     *            目标文件
		     * @throws IOException
		     */
	/*
		    public synchronized void downloadFile(String url, String dir)
		            throws Exception {
		    	HttpClient httpClient = new DefaultHttpClient();
		    	
		        if (url == null || "".equals(url)) {
		            return;
		        }
		        if (dir == null || "".equals(dir)) {
		            return;
		        }
		        if (!dir.endsWith(File.separator)) {
		            dir += File.separator;
		        }
		        File desPathFile = new File(dir);
		        if (!desPathFile.exists()) {
		            desPathFile.mkdirs();
		        }
		        String fullPath = dir;
		  
		        HttpGet httpget = new HttpGet(url);
		        httpget.addHeader("Referer", "http://jandan.net/ooxx");
		  
		        HttpResponse response = httpclient.get().execute(httpget);
		        String type = null;
		        try {
		  
		            type = response.getHeaders("Content-Type")[0].getValue();
		  
		            if (type == null || "".equals(type)) {
		                type = "jpg";
		            } else {
		                type = type.substring(type.lastIndexOf("/") + 1, type.length());
		            }
		        } catch (Exception e) {
		            type = "jpg";
		        }
		  
		        fullPath = fullPath + "." + type;
		  
		        HttpEntity entity = response.getEntity();
		        InputStream input = null;
		        try {
		            input = entity.getContent();
		  
		            File file = new File(fullPath);
		            FileOutputStream output = FileUtils.openOutputStream(file);
		            try {
		                IOUtils.copy(input, output);
		            } finally {
		                IOUtils.closeQuietly(output);
		            }
		  
		            EntityUtils.consume(entity);
		        } finally {
		            IOUtils.closeQuietly(input);
		            if (httpget != null) {
		                httpget.abort();
		            }
		        }
		    }*/

}
