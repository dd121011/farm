/**
 * 
 */
package com.f5.ourfarm.util;



/**
 * @author tianhao
 *
 */
public class DbConstants {
    /** ���ݿ�汾�� **/
    public static  int DATABASE_VERSION = 4;
    /** ���ݿ��� **/
    public static final String DATABASE_NAME = "ourfarm.db";
    /** ���ݿ����ֻ����·�� **/
    public static String DATABASE_PATH="/data/data/com.f5.ourfarm/databases/";
    /** ���� **/
    public static final String DATABASE_TABLE_DESTINATION = "destination";
    /** ������SQL �ַ��� **/
    public static final String TABLE_DESTINATION_CREATE = "CREATE TABLE `destination` (" +
            "`destination_id` String NOT NULL ," +
            "`json` String DEFAULT NULL," +
            "`cache` String DEFAULT true," +
            "`lat` String DEFAULT 39.6692," +
            "`lng` String DEFAULT 115.448," +
            "`type` String DEFAULT 1," +
            "PRIMARY KEY (`destination_id`)" +
            ");";
    /** drop��SQL �ַ��� **/
    public static final String TABLE_DESTINATION_DORP = "DROP TABLE IF EXISTS " + DATABASE_TABLE_DESTINATION + ";";
    
    public static final String DATABASE_TABLE_IMAGE = "image";
    /** ������SQL �ַ��� **/
    public static final String TABLE_IMAGE_CREATE = "Create table " + DATABASE_TABLE_IMAGE + 
            "(`id` integer NOT NULL PRIMARY KEY AUTOINCREMENT ," +
            "`destinationId` String NOT NULL ," +
            "`type` String DEFAULT `1`  NOT NULL ,"+ 
            "`url` String UNIQUE DEFAULT `0` NOT NULL  ," +
            "`cache` String DEFAULT `1`,"+ 
            "'blob' BLOB );";
    /** drop��SQL �ַ��� **/
    public static final String TABLE_IMAGE_DORP = "DROP TABLE IF EXISTS " + DATABASE_TABLE_IMAGE + ";";
    
    public static final String  DATABASE_TABLE_FAVORITE = "favorite";
    public static final String  DATABASE_THIS_TRIP = "this_trip";
    /** ������SQL �ַ��� **/
    public static final String TABLE_FAVORITE_CREATE = "Create table " + DATABASE_TABLE_FAVORITE +
            "(`destinationId` String NOT NULL PRIMARY KEY);";
    /** drop��SQL �ַ��� **/
    public static final String TABLE_FAVORITE_DORP = "DROP TABLE IF EXISTS " + DATABASE_TABLE_FAVORITE+ ";";
    
    /** ���������г̱��SQL **/
    public static final String TABLE_THIS_TRIP = "Create table " + DATABASE_THIS_TRIP +
    		" (`destinationId` INTEGER PRIMARY KEY NOT NULL, " +
    		" `sort` INTEGER);";
    
    /** drop�����г̱��SQL **/
    public static final String TABLE_THIS_TRIP_DORP = "DROP TABLE IF EXISTS " + DATABASE_THIS_TRIP + ";";
    
    /** ��ȡ�ղصľ��� **/
    public static final String GET_USER_FAVORITES = 
    "select des.* from favorite tt,destination des " +
	"where tt.destinationId = des.destination_id " +
	"order by sort;";
    
    /** ��ȡ�����г̵ľ��� **/
    public static final String GET_THIS_TRIP = 
    		"select des.* from this_trip tt,destination des " +
    		"where tt.destinationId = des.destination_id " +
    		"order by sort;";
    /** ��ȡ�����г̵ľ��� **/ 		
    public static final String UPDATE_UNDEL_TRIP = 
    		"update this_trip set sort = sort - 1 where sort > ?;";
    public static final String UPDATE_UNDEL_FAV = 
    		"update favorite set sort = sort - 1 where sort > ?;";
    /** ��ȡ����sortֵ **/
    public static final String GET_THIS_TRIP_MAX_SORT = " select max(sort) from this_trip ";
}
