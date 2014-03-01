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
 * 我的本次行程页面
 * 
 * @author lfy
 *
 */
public class MyThisTripActivity extends Activity {

	OnClickListener lback = null;
    OnClickListener details = null;
    OnClickListener delete = null;
    
    //本地存图片信息
    HashMap<Long, Bitmap> pics = new HashMap<Long ,Bitmap>();
    //显示内容
    HashMap<Long, Summary> lds = new HashMap<Long ,Summary>();
    List<Long> sort = new ArrayList();
    //sqlite 
    DestinationDbAdpter destinationDbAdpter;
    
    
  	private static String TAG = "我的本次行程页面";
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); 
        // 去除标题栏
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_my_thistrip);
        
        //准备listeners
        this.prepareListeners();
        
        //打开数据库
        destinationDbAdpter = new DestinationDbAdpter(this);
        destinationDbAdpter.open();
        
        
        //加载本地收藏的数据
        this.showMyfavoriteList();
        
        //设置listeners
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
     * 先加载本地数据
     */
    private void showMyfavoriteList() {
        try {
            //数据库中查询收藏的数据
            Cursor cursor = destinationDbAdpter.getThisTrip();
            if (cursor != null && cursor.getCount() > 0) {
                Gson gson = new Gson();
                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                    String json = cursor.getString(1);
                    Destination destination = gson.fromJson(json, new TypeToken<Destination>() {}.getType());
                    Summary summary = destination.getScenerySummary();
                    lds.put(summary.getDestinationId(), summary);
                    sort.add(summary.getDestinationId());
                    // 获取本地图片
                    Bitmap bitmap = destinationDbAdpter.getHeadBitmap(summary.getDestinationId());
                    if (bitmap != null) {
                        pics.put(summary.getDestinationId(), bitmap);
                    }
                }
            }
            // 显示列表  
            // 判断本地数据是否为null，若为null则进行提示
            if(lds.size() == 0){
            	Tools.showToastLong(MyThisTripActivity.this, Constants.NONE_THIS_TRIP);
            }else{
            	//显示本地数据
            	this.showList(lds);
            }
            
        } catch (JsonSyntaxException e) {
            Log.e("json转换失败", "json 2 Destination" + e.getMessage());
        } catch (SQLException e) {
            Log.e("获取图片数据错误", "获取图片数据错误");
        }
    }

    /**
     * 监听到事件后的动作；
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
            	// 判断网络是否可用，若不可用则提示用户，并不进行跳转
            	if(!Tools.checkNetworkStatus(MyThisTripActivity.this)) {
            		return;
            	}
                long destinationId =  (Long) v.getTag(); 
                Log.i("res", ""+destinationId);
                Intent nearby2detail = new Intent(MyThisTripActivity.this,DetailActivity.class);
                //将服务器返回数据保存入Intent，确保数据可在activity间传递
                nearby2detail.putExtra("destinationId", destinationId);
                startActivity(nearby2detail);
            } 
        };
        //删除
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
     * 绑定view和监听
     */
    private void batchSetListeners() {
        // back
        ImageView iback2home = (ImageView) this.findViewById(R.id.ImageView_button_back);
        iback2home.setOnClickListener(lback);
    }
    
    /**
     * 构造附近列表
     * 根据查询参数查找附近内容，然后将内容概要信息显示出来。
     */
    private void showList(HashMap<Long, Summary> lds) {
        LayoutInflater flater = LayoutInflater.from(this);
        LinearLayout list = (LinearLayout) this
                .findViewById(R.id.ListView_main);
        list.removeAllViews();
        // 获取查询结果，循环构造概要列表
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
            tvHot.setText("爆棚指数:" + ds.getHot());
            // Characteristic
            TextView tvCharacteristic = (TextView) v
                    .findViewById(R.id.ListView_destination_characteristic);
            tvCharacteristic.setText("特色:" + ds.getCharacteristic());
            // add view
            list.addView(v);
            
            // 设置监听
            // delete
            ImageView deleteImageView = (ImageView) v.findViewById(R.id.ImageView_delete_pic);
            v.setTag(desid);
            ivPic.setTag(desid);
            deleteImageView.setTag(R.string.tag_first, i);//点击要关闭的View
            deleteImageView.setTag(R.string.tag_second, desid);//View的ID
            
            deleteImageView.setOnClickListener(delete);
            v.setOnClickListener(details);
            ImageView upImageView = (ImageView) v.findViewById(R.id.ListView_distance_pic);
            upImageView.setTag(R.string.tag_first, i);//点击要关闭的View
            upImageView.setTag(R.string.tag_second, desid);//View的ID
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
     * 变更排序，更新界面及数据库
     *	type=0：向上移动 1：向下移动
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
