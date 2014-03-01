package com.farm.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DBUtil {

	public static Connection getConnect() {
		Connection myConnection = null;
		try {
			DriverManager.registerDriver(new com.mysql.jdbc.Driver());

			myConnection = DriverManager.getConnection(
					"jdbc:mysql://172.20.6.19:3306/travel_hunan", 
					"root", 
					"123");
	
			myConnection.setAutoCommit(false);
	
			return myConnection;
		
		} catch (SQLException e) {
			System.out.println("获取数据库连接失败!");
			e.printStackTrace();
		}
        
		return myConnection;
	}

}
