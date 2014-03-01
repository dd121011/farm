package com.farm;

import java.io.File;

public class test {
	
	private static final String READ_PIC_ROOT = "D:/ReadPicture";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String url1 = "http://www.shidutour.com/uploads/allimg/100312/1_100312231028_1.jpg";
		String url2 = "http://www.shidutour.com/uploads/allimg/100312/1_100312231028_3.jpg";
        
		url1 = url1.substring(7);
		url2 = url2.substring(7);
		
		String[] url1Arr = url1.split("/");
		String[] url2Arr = url2.split("/");
		
		String path = READ_PIC_ROOT;
		for (int i = 0; i < url1Arr.length - 1; i++) {
			path = path + File.separator + url1Arr[i];
			File dir = new File(path);
			dir.mkdir();
		}
		path = READ_PIC_ROOT;
		for (int i = 0; i < url2Arr.length; i++) {
			path = path + File.separator + url2Arr[i];
			File dir = new File(path);
			dir.mkdir();
		}
		
		System.out.println(url1Arr.toString());
	}

}
