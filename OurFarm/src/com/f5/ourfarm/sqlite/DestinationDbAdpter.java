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
    
    private double scale = 111.31949079327;//1ά�ȵľ���
   
    /** �������ݼ��� **/
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
         * ��DATABASE_VERSION���º�ִ��
         */
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.i("DestinationDbAdpter", "��������!");
            //TODO ϵͳ����ʱ�ɽ�����������sql����ʽ���£����²���Ӧ�ý�������ṹ�����������������������ݲ�������������ݲ���ͨ�����½ӿڽ���
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
     * ���ش洢destionation���洢���ݰ���DestinationId��destionation��json�����Ƿ����cache��ʶ
     * @param destionation
     * @param cache  �Ƿ���洢Ϊ���棬true�����棬false�������ô洢
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
    
    /** �������� **/
    /**
     * @param destinationId
     * @param type ͼƬ���ͣ�1Ϊ��ҳͼƬ��2����ͼƬ
     * @param cache �Ƿ�Ϊ���� �� trueΪ�����棬��Ӧ���ݿ�Ϊ1��
     * @param url ͼƬurl
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
                 * Bitmap.CompressFormat.JPEG �� Bitmap.CompressFormat.PNG JPEG �� PNG
                 * ������������ JPEG����������ͼ��PNGʹ�ô�LZ77��������������ѹ���㷨�� ���ｨ��ʹ��PNG��ʽ���� 100
                 * ��ʾ��������Ϊ100%����Ȼ��Ҳ���Ըı��������Ҫ�İٷֱ������� os �Ƕ�����ֽ������
                 * 
                 * .compress() �����ǽ�Bitmapѹ����ָ����ʽ�������������
                 */
                bmp.compress(Bitmap.CompressFormat.JPEG, 10, os);
                initValues.put("destinationId", destinationId);
                initValues.put("type", type);
                initValues.put("cache", cache);
                initValues.put("url", url);
                initValues.put("blob", os.toByteArray());// ���ֽ���ʽ����
                id = mDb4destination.insert(DATABASE_TABLE_IMAGE, null, initValues);// ��������
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
     * ����ղ�����
     * 
     * @param destinationId ����Id
     */
    public void addFavorite (long destinationId){
        String sql = "INSERT OR REPLACE INTO favorite VALUES (" + destinationId + ");";
        mDb4destination.execSQL(sql);
    }
    
    /**
     * ɾ���ղ�����
     * 
     * @param destinationId ����Id
     */
    public void deleteFavorite (long destinationId){
    	String sql = "delete from favorite where destinationId = " + destinationId;
    	mDb4destination.execSQL(sql);
    }
    
    /**
     * �����ղص�destination��Ϣ
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
     * ��ӱ����г�����
     * 
     * @param destinationId ����Id
     */
    public void addThisTrip (long destinationId, int sort){
    	String sql = "INSERT OR REPLACE INTO this_trip VALUES (" +
    		    destinationId  + "," + sort + 
    		");";
    	mDb4destination.execSQL(sql);
    }
    
    /**
     * �޸ı����г�����
     * 
     * @param destinationId ����Id
     */
    public void updateThisTrip (long destinationId, int sort){
    	String sql = "update this_trip set sort = " +  sort + " where destinationId = " + destinationId;
    	mDb4destination.execSQL(sql);
    }
    /**
     * �޸ı����г�����
     * 
     * @param destinationId ����Id
     */
    public void updateMyFav (long destinationId, int sort){
    	String sql = "update favorite set sort = " +  sort + " where destinationId = " + destinationId;
    	mDb4destination.execSQL(sql);
    }
    /**
     * ɾ�������г�����
     * 
     * @param destinationId ����Id
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
     * ��ȡ��������ֵ
     * @return
     */
    public int getMaxSortThisTrip() {
    	
    	Cursor mCursor = mDb4destination.rawQuery(GET_THIS_TRIP_MAX_SORT, null);
    	mCursor.moveToFirst();
    	
    	return mCursor.getInt(0);
    }
    
    /**
     * ���ұ����г���Ϣ
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
     * ��ȡ���� ��ҳ ͷ��ͼƬ
     * @param destinationId
     * @return
     */
    public Bitmap getHeadBitmap(long destinationId )throws SQLException{
        String selection = "destinationId = "+ destinationId +" and type = 1"; 
        Cursor cursor = mDb4destination.query(DATABASE_TABLE_IMAGE, image, selection, null, null, null, null);
        if (cursor != null & cursor.getCount() >0) {
            cursor.moveToFirst();
            /** �õ�Bitmap�ֽ����� **/
            byte[] in = cursor.getBlob(5);
            /**
             * ����Bitmap�ֽ�����ת���� Bitmap���� BitmapFactory.decodeByteArray()
             * �������ֽ����ݣ���0���ֽڵĳ����н��룬����Bitmap����
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
                .query(DATABASE_TABLE_IMAGE, image, null, null, null, null, null);// ���ݵĲ�ѯ
        HashMap<String, Object> bindData = null;
        list = new ArrayList<Map<String, Object>>();
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            bindData = new HashMap<String, Object>();
            bindData.put("url", cursor.getLong(0));
            /** �õ�Bitmap�ֽ����� **/
            byte[] in = cursor.getBlob(1);
            /**
             * ����Bitmap�ֽ�����ת���� Bitmap���� BitmapFactory.decodeByteArray()
             * �������ֽ����ݣ���0���ֽڵĳ����н��룬����Bitmap����
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
     * ����id����destination��Ϣ
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
     * @return ȫ��������Ϣ
     * @throws SQLException
     */
    public Cursor getAllDestination()throws SQLException {
        return mDb4destination.query(true, DATABASE_TABLE_DESTINATION, destination, null, null, null,null, null, null);
    }
    
    /**
     * ��ȡ�������ݣ����ݵ���λ�ú����͹���
     * @param lat
     * @param lng
     * @param type  ���ͣ�1����2��ʳ3ס��4�ز�
     * @return
     * @throws SQLException
     */
    public Cursor getNearbyDestination(double lat, double lng, double distacne,int type)throws SQLException {
        /*���㾭γ�Ȳ���*/
        double halfDistacne = (distacne/scale);
        String selection  = String.valueOf(lat-halfDistacne) +" <lat<"+String.valueOf(lat-halfDistacne) +
                "   and "+String.valueOf(lng-halfDistacne)+ " <lng< "+ String.valueOf(lng-halfDistacne)+
                "   and type = "+type+" ;";
        return mDb4destination.query(true, DATABASE_TABLE_DESTINATION, destination, selection, null, null,null, null, null);
    }
    
    /**
     * ���������Ϣ
     * @return
     * @throws SQLException
     */
    public void deleteCache()throws SQLException{
        mDb4destination.delete(DATABASE_TABLE_IMAGE, " cache = '1'", null);// ���ݵ�ɾ��
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
