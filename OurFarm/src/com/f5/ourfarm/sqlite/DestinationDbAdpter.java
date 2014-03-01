/**
 * 
 */
package com.f5.ourfarm.sqlite;



import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.f5.ourfarm.model.Destination;
import com.f5.ourfarm.util.DbConstants;
import com.f5.ourfarm.util.Tools;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import static com.f5.ourfarm.util.DbConstants.*;
/**
 * @author tianhao
 *
 */
public class DestinationDbAdpter {
    
    private static final String TAG = "DestinationDbAdpter";  
   
    private final Context ctx4destination;
    private SQLiteDatabase mDb4destination;
    private DestinationHelper destinationHelper ;
    
    private double scale = 111.31949079327;//1维度的距离差；
   
    /** 表列数据集合 **/
    private String[] image = {"id","destinationId","type", "url", "cache","blob" };
    private String[] destination = { "destination_id", "json","cache"};
    private String[] thisTrip = { "destinationId", "sort"};
    
    
    /**
     * @author tianhao
     *
     */
    private static class DestinationHelper extends SQLiteOpenHelper {
        
        public DestinationHelper (Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(TABLE_DESTINATION_CREATE);
            db.execSQL(TABLE_IMAGE_CREATE);
            db.execSQL(TABLE_FAVORITE_CREATE);
            db.execSQL(TABLE_THIS_TRIP);
        }

        /* (non-Javadoc)
         * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)
         * 当DATABASE_VERSION更新后执行
         */
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.i("DestinationDbAdpter", "更新数据!");
            //TODO 系统更新时可将更新内容以sql的形式更新，更新操作应该仅包括表结构调整，新增表，不包括大数据插入操作，大数据插入通过更新接口进行
            db.execSQL(TABLE_DESTINATION_DORP);
            db.execSQL(TABLE_IMAGE_DORP);
            db.execSQL(TABLE_FAVORITE_DORP);
            db.execSQL(TABLE_THIS_TRIP_DORP);
            onCreate(db);
        }
    }
    
    public DestinationDbAdpter(Context ctx) {
        this.ctx4destination = ctx;
    }
    
    public DestinationDbAdpter open() throws SQLException {
        destinationHelper = new DestinationHelper(ctx4destination);
        mDb4destination = destinationHelper.getWritableDatabase();
        return this;
    }

    public void close()throws SQLException {
        destinationHelper.close();
    }
    
    
  
    /**
     * 本地存储destionation，存储内容包括DestinationId，destionation的json串，是否仅存cache标识
     * @param destionation
     * @param cache  是否仅存储为缓存，true仅缓存，false本地永久存储
     * @return
     */
    public long insertDestination(Destination destination,Boolean cache)throws SQLException{
        ContentValues initialValues = new ContentValues();
        initialValues.put("destination_id", destination.getScenerySummary().getDestinationId());
        Gson gson = new Gson();
        String json = gson.toJson(destination, (new TypeToken<Destination>(){}).getType());
        initialValues.put("json",json );
        initialValues.put("cache", cache);
        initialValues.put("lat",destination.getScenerySummary().getLat() );
        initialValues.put("lng",destination.getScenerySummary().getLng() );
        initialValues.put("type", destination.getScenerySummary().getType());
        try{
            Log.i(TAG, "INSERT destination");
            long res =  mDb4destination.insert(DATABASE_TABLE_DESTINATION, null, initialValues); 
            return res;
        }catch(Exception e){
            Log.e(TAG, "INSERT ERROR",e);
            return 0;
        }
    }
    
    /** 创建数据 **/
    /**
     * @param destinationId
     * @param type 图片类型，1为首页图片，2其它图片
     * @param cache 是否为缓存 ， true为仅缓存，对应数据库为1；
     * @param url 图片url
     * @return
     */
    public Long insertImage(long destinationId,int type,boolean cache,String url) throws SQLException{
        //
        Bitmap bmp = Tools.getBitmapFromUrl(url);
        try{
            if(bmp != null){
                ContentValues initValues = new ContentValues();
                Long id = null;
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                /**
                 * Bitmap.CompressFormat.JPEG 和 Bitmap.CompressFormat.PNG JPEG 与 PNG
                 * 的是区别在于 JPEG是有损数据图像，PNG使用从LZ77派生的无损数据压缩算法。 这里建议使用PNG格式保存 100
                 * 表示的是质量为100%。当然，也可以改变成你所需要的百分比质量。 os 是定义的字节输出流
                 * 
                 * .compress() 方法是将Bitmap压缩成指定格式和质量的输出流
                 */
                bmp.compress(Bitmap.CompressFormat.JPEG, 10, os);
                initValues.put("destinationId", destinationId);
                initValues.put("type", type);
                initValues.put("cache", cache);
                initValues.put("url", url);
                initValues.put("blob", os.toByteArray());// 以字节形式保存
                id = mDb4destination.insert(DATABASE_TABLE_IMAGE, null, initValues);// 保存数据
                Log.i("Image ", "insert done.");
                return id;
            } 
        }catch(SQLiteConstraintException e){
            Log.e(TAG, "INSERT ERROR");
            return 0l;
        }
		return 0l;
    }
    
    /**
     * 添加收藏数据
     * 
     * @param destinationId 景点Id
     */
    public void addFavorite (long destinationId){
        String sql = "INSERT OR REPLACE INTO favorite VALUES (" + destinationId + ");";
        mDb4destination.execSQL(sql);
    }
    
    /**
     * 删除收藏数据
     * 
     * @param destinationId 景点Id
     */
    public void deleteFavorite (long destinationId){
    	String sql = "delete from favorite where destinationId = " + destinationId;
    	mDb4destination.execSQL(sql);
    }
    
    /**
     * 查找收藏的destination信息
     * 
     * @return Cursor
     * @throws SQLException
     */
    public Cursor getFavoriteDestination() throws SQLException {
    	Cursor mCursor = mDb4destination.rawQuery(GET_USER_FAVORITES, null);
    	if (mCursor != null) {
    		mCursor.moveToFirst();
    	}
    	return mCursor;
    }
    
    /**
     * 添加本次行程数据
     * 
     * @param destinationId 景点Id
     */
    public void addThisTrip (long destinationId, int sort){
    	String sql = "INSERT OR REPLACE INTO this_trip VALUES (" +
    		    destinationId  + "," + sort + 
    		");";
    	mDb4destination.execSQL(sql);
    }
    
    /**
     * 修改本次行程数据
     * 
     * @param destinationId 景点Id
     */
    public void updateThisTrip (long destinationId, int sort){
    	String sql = "update this_trip set sort = " +  sort + " where destinationId = " + destinationId;
    	mDb4destination.execSQL(sql);
    }
    /**
     * 修改本次行程数据
     * 
     * @param destinationId 景点Id
     */
    public void updateMyFav (long destinationId, int sort){
    	String sql = "update favorite set sort = " +  sort + " where destinationId = " + destinationId;
    	mDb4destination.execSQL(sql);
    }
    /**
     * 删除本次行程数据
     * 
     * @param destinationId 景点Id
     */
    public void deleteThisTrip (long destinationId){
    	String sql = "delete from this_trip where destinationId = " + destinationId;
    	mDb4destination.execSQL(sql);
    }
    
    public Cursor getTrisTripWithId(long destinationId) {
    	String selection = "destinationId = "+ destinationId;
        return mDb4destination.query(DATABASE_THIS_TRIP, thisTrip, selection, null, null, null, null);
    }
    
    /**
     * 获取排序的最大值
     * @return
     */
    public int getMaxSortThisTrip() {
    	
    	Cursor mCursor = mDb4destination.rawQuery(GET_THIS_TRIP_MAX_SORT, null);
    	mCursor.moveToFirst();
    	
    	return mCursor.getInt(0);
    }
    
    /**
     * 查找本次行程信息
     * 
     * @return Cursor
     * @throws SQLException
     */
    public Cursor getThisTrip() throws SQLException {
    	Cursor mCursor = mDb4destination.rawQuery(GET_THIS_TRIP, null);
    	if (mCursor != null) {
    		mCursor.moveToFirst();
    	}
    	return mCursor;
    }
   
    /**
     * 获取景点 首页 头像图片
     * @param destinationId
     * @return
     */
    public Bitmap getHeadBitmap(long destinationId )throws SQLException{
        String selection = "destinationId = "+ destinationId +" and type = 1"; 
        Cursor cursor = mDb4destination.query(DATABASE_TABLE_IMAGE, image, selection, null, null, null, null);
        if (cursor != null & cursor.getCount() >0) {
            cursor.moveToFirst();
            /** 得到Bitmap字节数据 **/
            byte[] in = cursor.getBlob(5);
            /**
             * 根据Bitmap字节数据转换成 Bitmap对象 BitmapFactory.decodeByteArray()
             * 方法对字节数据，从0到字节的长进行解码，生成Bitmap对像。
             **/
            Bitmap bmpout = BitmapFactory.decodeByteArray(in, 0, in.length);
            return bmpout;
        }
        return null;
    }

    /**
     * @return
     * @throws SQLException
     */
    public List<Map<String, Object>> getAllImage() throws SQLException{
        List<Map<String, Object>> list = null;
        Cursor cursor = mDb4destination
                .query(DATABASE_TABLE_IMAGE, image, null, null, null, null, null);// 数据的查询
        HashMap<String, Object> bindData = null;
        list = new ArrayList<Map<String, Object>>();
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            bindData = new HashMap<String, Object>();
            bindData.put("url", cursor.getLong(0));
            /** 得到Bitmap字节数据 **/
            byte[] in = cursor.getBlob(1);
            /**
             * 根据Bitmap字节数据转换成 Bitmap对象 BitmapFactory.decodeByteArray()
             * 方法对字节数据，从0到字节的长进行解码，生成Bitmap对像。
             **/
            Bitmap bmpout = BitmapFactory.decodeByteArray(in, 0, in.length);
            bindData.put("blob", bmpout);

            list.add(bindData);
        }
        cursor.close();

        Log.i("Image ", "get a Bitmap.");
        return list;
    }

    
    /**
     * 根据id查找destination信息
     * @param destinationId
     * @return Cursor
     * @throws SQLException
     */
    public Cursor getDestination(long destinationId) throws SQLException {
        Cursor mCursor =
                mDb4destination.query(true, DATABASE_TABLE_DESTINATION, destination, "destination_id = " + destinationId, null, null,
                null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }
    
    /**
     * @return 全部景点信息
     * @throws SQLException
     */
    public Cursor getAllDestination()throws SQLException {
        return mDb4destination.query(true, DATABASE_TABLE_DESTINATION, destination, null, null, null,null, null, null);
    }
    
    /**
     * 获取本地数据，根据地理位置和类型过滤
     * @param lat
     * @param lng
     * @param type  类型，1景点2美食3住宿4特产
     * @return
     * @throws SQLException
     */
    public Cursor getNearbyDestination(double lat, double lng, double distacne,int type)throws SQLException {
        /*计算经纬度步长*/
        double halfDistacne = (distacne/scale);
        String selection  = String.valueOf(lat-halfDistacne) +" <lat<"+String.valueOf(lat-halfDistacne) +
                "   and "+String.valueOf(lng-halfDistacne)+ " <lng< "+ String.valueOf(lng-halfDistacne)+
                "   and type = "+type+" ;";
        return mDb4destination.query(true, DATABASE_TABLE_DESTINATION, destination, selection, null, null,null, null, null);
    }
    
    /**
     * 清除缓存信息
     * @return
     * @throws SQLException
     */
    public void deleteCache()throws SQLException{
        mDb4destination.delete(DATABASE_TABLE_IMAGE, " cache = '1'", null);// 数据的删除
        Log.i("Image ", "delete all data.");
        mDb4destination.delete(DATABASE_TABLE_DESTINATION, " cache = '1'", null);
        Log.i("DESTINATION ", "delete all data.");//
    }
    public void changeMyTripSort(long fromdestinationId,long todestinationId,int oldsort,int newsort)
    {
    	updateThisTrip(fromdestinationId, newsort);
    	updateThisTrip(todestinationId, oldsort);
    }
    public void changeMyFavSort(long fromdestinationId,long todestinationId,int oldsort,int newsort)
    {
    	updateMyFav(fromdestinationId, newsort);
    	updateMyFav(todestinationId, oldsort);
    }
    public void updateWhenDelTrip(long desid,int sort)
    {
    	mDb4destination.execSQL(DbConstants.UPDATE_UNDEL_TRIP, new Integer[]{sort});
    }
    public void updateWhenDelFav(long desid,int sort)
    {
    	mDb4destination.execSQL(DbConstants.UPDATE_UNDEL_FAV, new Integer[]{sort});
    }
}
