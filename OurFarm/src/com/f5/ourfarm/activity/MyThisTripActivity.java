package com.f5.ourfarm.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.f5.ourfarm.R;
import com.f5.ourfarm.model.Destination;
import com.f5.ourfarm.model.Summary;
import com.f5.ourfarm.sqlite.DestinationDbAdpter;
import com.f5.ourfarm.util.Constants;
import com.f5.ourfarm.util.Tools;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.umeng.analytics.MobclickAgent;


/**
 * �ҵı����г�ҳ��
 * 
 * @author lfy
 *
 */
public class MyThisTripActivity extends Activity {

	OnClickListener lback = null;
    OnClickListener details = null;
    OnClickListener delete = null;
    
    //���ش�ͼƬ��Ϣ
    HashMap<Long, Bitmap> pics = new HashMap<Long ,Bitmap>();
    //��ʾ����
    HashMap<Long, Summary> lds = new HashMap<Long ,Summary>();
    List<Long> sort = new ArrayList();
    //sqlite 
    DestinationDbAdpter destinationDbAdpter;
    
    
  	private static String TAG = "�ҵı����г�ҳ��";
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); 
        // ȥ��������
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_my_thistrip);
        
        //׼��listeners
        this.prepareListeners();
        
        //�����ݿ�
        destinationDbAdpter = new DestinationDbAdpter(this);
        destinationDbAdpter.open();
        
        
        //���ر����ղص�����
        this.showMyfavoriteList();
        
        //����listeners
        this.batchSetListeners();
    }
    
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
    
    /**
     * �ȼ��ر�������
     */
    private void showMyfavoriteList() {
        try {
            //���ݿ��в�ѯ�ղص�����
            Cursor cursor = destinationDbAdpter.getThisTrip();
            if (cursor != null && cursor.getCount() > 0) {
                Gson gson = new Gson();
                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                    String json = cursor.getString(1);
                    Destination destination = gson.fromJson(json, new TypeToken<Destination>() {}.getType());
                    Summary summary = destination.getScenerySummary();
                    lds.put(summary.getDestinationId(), summary);
                    sort.add(summary.getDestinationId());
                    // ��ȡ����ͼƬ
                    Bitmap bitmap = destinationDbAdpter.getHeadBitmap(summary.getDestinationId());
                    if (bitmap != null) {
                        pics.put(summary.getDestinationId(), bitmap);
                    }
                }
            }
            // ��ʾ�б�  
            // �жϱ��������Ƿ�Ϊnull����Ϊnull�������ʾ
            if(lds.size() == 0){
            	Tools.showToastLong(MyThisTripActivity.this, Constants.NONE_THIS_TRIP);
            }else{
            	//��ʾ��������
            	this.showList(lds);
            }
            
        } catch (JsonSyntaxException e) {
            Log.e("jsonת��ʧ��", "json 2 Destination" + e.getMessage());
        } catch (SQLException e) {
            Log.e("��ȡͼƬ���ݴ���", "��ȡͼƬ���ݴ���");
        }
    }

    /**
     * �������¼���Ķ�����
     */
    private void prepareListeners() {
        //myFavorite->my
        lback = new OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        };
        //details
        details = new OnClickListener(){
            public void onClick(View v) {
            	// �ж������Ƿ���ã�������������ʾ�û�������������ת
            	if(!Tools.checkNetworkStatus(MyThisTripActivity.this)) {
            		return;
            	}
                long destinationId =  (Long) v.getTag(); 
                Log.i("res", ""+destinationId);
                Intent nearby2detail = new Intent(MyThisTripActivity.this,DetailActivity.class);
                //���������������ݱ�����Intent��ȷ�����ݿ���activity�䴫��
                nearby2detail.putExtra("destinationId", destinationId);
                startActivity(nearby2detail);
            } 
        };
        //ɾ��
        delete = new OnClickListener(){
            public void onClick(View v) {
            	if(v.getId()==R.id.ImageView_delete_pic)
            	{
                /*long destinationId =  (Long) v.getTag(R.string.tag_second);
                destinationDbAdpter.deleteThisTrip(destinationId);
                
                View view = (View)v.getTag(R.string.tag_first);
                view.setVisibility(View.GONE);*/
            		long destinationId =  (Long) v.getTag(R.string.tag_second);
            		int sortnum =  (Integer) v.getTag(R.string.tag_first);
                    /*destinationDbAdpter.deleteFavorite(destinationId);
                    
                    View view = (View)v.getTag(R.string.tag_first);
                    view.setVisibility(View.GONE);*/
            		deleMytrip(destinationId,sortnum);
            	}
                if(v.getId()==R.id.ListView_distance_pic)
            	{
            		long destinationId =  (Long) v.getTag(R.string.tag_second);
            		int sortnum =  (Integer) v.getTag(R.string.tag_first);
            		changeSort(destinationId,sortnum);
                    /*destinationDbAdpter.deleteFavorite(destinationId);
                    
                    View view = (View)v.getTag(R.string.tag_first);
                    view.setVisibility(View.GONE);*/
            	}
            } 
        };
    }
    
    /**
     * ��view�ͼ���
     */
    private void batchSetListeners() {
        // back
        ImageView iback2home = (ImageView) this.findViewById(R.id.ImageView_button_back);
        iback2home.setOnClickListener(lback);
    }
    
    /**
     * ���츽���б�
     * ���ݲ�ѯ�������Ҹ������ݣ�Ȼ�����ݸ�Ҫ��Ϣ��ʾ������
     */
    private void showList(HashMap<Long, Summary> lds) {
        LayoutInflater flater = LayoutInflater.from(this);
        LinearLayout list = (LinearLayout) this
                .findViewById(R.id.ListView_main);
        list.removeAllViews();
        // ��ȡ��ѯ�����ѭ�������Ҫ�б�
        int i=1;
        for(long desid:sort)
        {
        
            Summary ds = lds.get(desid);
            View v = flater.inflate(R.layout.listview_child_my, null);
            // pic
            ImageView ivPic = (ImageView) v
                    .findViewById(R.id.ImageView_destination_pic);
            Bitmap bm = (Bitmap) pics.get(ds.getDestinationId());
            if(bm != null){
                ivPic.setImageBitmap(bm);
            }
            
            // name
            TextView tvName = (TextView) v
                    .findViewById(R.id.ListView_destination_name);
            tvName.setText(ds.getName());
            // sroce
            RatingBar rbSroce = (RatingBar) v
                    .findViewById(R.id.ratingBar_destination_sroce);
            rbSroce.setRating(ds.getScore());
            // price info
            TextView tvPrice = (TextView) v
                    .findViewById(R.id.ListView_destination_price);
            tvPrice.setText(ds.getPriceInfo());
            // hot
            TextView tvHot = (TextView) v
                    .findViewById(R.id.ListView_destination_hot);
            tvHot.setText("����ָ��:" + ds.getHot());
            // Characteristic
            TextView tvCharacteristic = (TextView) v
                    .findViewById(R.id.ListView_destination_characteristic);
            tvCharacteristic.setText("��ɫ:" + ds.getCharacteristic());
            // add view
            list.addView(v);
            
            // ���ü���
            // delete
            ImageView deleteImageView = (ImageView) v.findViewById(R.id.ImageView_delete_pic);
            v.setTag(desid);
            ivPic.setTag(desid);
            deleteImageView.setTag(R.string.tag_first, i);//���Ҫ�رյ�View
            deleteImageView.setTag(R.string.tag_second, desid);//View��ID
            
            deleteImageView.setOnClickListener(delete);
            v.setOnClickListener(details);
            ImageView upImageView = (ImageView) v.findViewById(R.id.ListView_distance_pic);
            upImageView.setTag(R.string.tag_first, i);//���Ҫ�رյ�View
            upImageView.setTag(R.string.tag_second, desid);//View��ID
            upImageView.setOnClickListener(delete);
            i++;
        }
       /* 
        for(Entry<Long, Summary> entry: lds.entrySet()){
            
        
        }*/
        //
        list.invalidate();
    }
    /**
     * ������򣬸��½��漰���ݿ�
     *	type=0�������ƶ� 1�������ƶ�
     *
     */
    private void changeSort(long destinationId,int sortnum)
    {
    	if(sortnum > 1)
    	{
    		long toDesid = sort.get(sortnum-2);
    		destinationDbAdpter.changeMyTripSort(destinationId, toDesid, sortnum, sortnum-1);
        	sort.set(sortnum-1, toDesid);
        	sort.set(sortnum-2, destinationId);
        	showList(lds);
    	}
    }
    private void deleMytrip(long desid,int sortnum)
    {
    	
    	destinationDbAdpter.deleteThisTrip(desid);
    	destinationDbAdpter.updateWhenDelTrip(desid, sortnum);
    	sort.remove(desid);
    	lds.remove(desid);
    	pics.remove(desid);
    	showList(lds);
    }
    //private void changePageSort
}
