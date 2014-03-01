package com.f5.ourfarm.activity;

import static com.f5.ourfarm.util.Constants.PROGRESS_MESSAGE;
import static com.f5.ourfarm.util.Constants.PROGRESS_TITLE;
import static com.f5.ourfarm.util.URLConstants.FIND_URL;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.f5.ourfarm.layout.PullToRefreshView;
import com.f5.ourfarm.layout.PullToRefreshView.OnFooterRefreshListener;
import com.f5.ourfarm.model.LoadWay;
import com.f5.ourfarm.model.Summary;
import com.f5.ourfarm.util.HttpUtil;
import com.f5.ourfarm.util.Tools;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.umeng.analytics.MobclickAgent;


/**
 * 查找信息页面
 * 
 * @author tianhao
 *
 */
public class FindActivity extends Activity implements OnFooterRefreshListener {

	private static final String TAG = "查找信息页面";
	private static final String errMsg = "查找信息失败";
	
    OnClickListener lback = null;
    OnClickListener details = null;
    HashMap<String, Bitmap> pics = new HashMap<String ,Bitmap>();
    ArrayList<String> urls = new ArrayList<String>();

    //进度条
    private ProgressDialog progressDialog = null;
    
    //记录第几次请求，每请求一次，返回10条记录
    private int queryCounts = 1;
    //上拉刷新
    PullToRefreshView mPullToRefreshView;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); 
        // 去除标题栏
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_classicline);
        //初始化，表示请求是第一次
        queryCounts = 1;
        mPullToRefreshView = (PullToRefreshView) findViewById(R.id.main_pull_refresh_view);
        
        //准备listeners
        this.prepareListeners();
        //设置listeners
        this.batchSetListeners();
        // 画面上方显示具体的区域名称 
        TextView tvTop = (TextView) this.findViewById(R.id.TextView_toppanel_title);
        tvTop.setText(getIntent().getExtras().getString("regionName"));
        
        progressDialog = ProgressDialog.show(FindActivity.this, PROGRESS_TITLE, PROGRESS_MESSAGE, true);
        progressDialog.setCancelable(true);
        new Thread(runnable4find).start();
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
     * 监听到事件后的动作；
     */
    private void prepareListeners() {
        //nearby->home
        lback = new OnClickListener() {
            public void onClick(View v) {
            	finish();
            }
        };
        //details
        details = new OnClickListener(){
            public void onClick(View v) {
                long destinationId =  (Long) v.getTag(); 
                Log.i("res", ""+destinationId);
                Intent nearby2detail = new Intent(FindActivity.this,DetailActivity.class);
                //将服务器返回数据保存入Intent，确保数据可在activity间传递
                nearby2detail.putExtra("destinationId", destinationId);
                startActivity(nearby2detail);
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
        //上拉刷新
        mPullToRefreshView.setOnFooterRefreshListener(this);
    }
    
    /**
     * 加载没有数据时的处理
     * 
     * @param loadWay 加载方式：初始化或者上拉加载
     */
    private void loadNoData(int loadWay) {
    	if(loadWay == LoadWay.POLLUP_LOAD.ordinal()) {
    		Tools.showToastLong(FindActivity.this, "没有更多的景点了。");
    		mPullToRefreshView.onFooterRefreshComplete();
    	} else {
            // TODO 查询无结果
            TextView tvTop = (TextView) this.findViewById(R.id.TextView_toppanel_title);
            tvTop.setText("区域内未找到相关信息");
    	}
    }

    /**
     * 构造附近列表
     * 根据查询参数查找附近内容，然后将内容概要信息显示出来。
     */
    private void creatListView(String res, int loadWay) {
        if(res == null || res.startsWith("<br />")){
        	loadNoData(loadWay);
            return;
        }else{
            List<Summary> lds = null;
            try {
	            Gson gson = new Gson();
	            lds = gson.fromJson(res, new TypeToken<List<Summary>>() {}.getType());
			} catch (JsonSyntaxException e) {
                Tools.showToastShort(FindActivity.this, errMsg);
                FindActivity.this.finish();
			}
            
            if(lds == null || lds.size() == 0) {
            	loadNoData(loadWay);
            	return;
            }

            LayoutInflater flater = LayoutInflater.from(this);
            LinearLayout list = (LinearLayout) this
                    .findViewById(R.id.ListView_main);
            // 获取查询结果，循环构造概要列表
            for (Summary ds : lds) {
                View v = flater.inflate(R.layout.listview_child_nearby, null);
                // pic
                ImageView ivPic = (ImageView) v
                        .findViewById(R.id.ImageView_destination_pic);
                ivPic.setTag(ds.getPic());
                urls.add(ds.getPic());
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
                
                //隐藏距离
                v.findViewById(R.id.ListView_distance_pic).setVisibility(View.GONE);
                v.findViewById(R.id.ListView_destination_distance).setVisibility(View.GONE);

                // add view
                list.addView(v);
                // 设置监听
                long destinationId = ds.getDestinationId();
                v.setTag(destinationId);
                v.setOnClickListener(details);
            }
            
            //上拉完成加载
            mPullToRefreshView.onFooterRefreshComplete();
            
            new Thread(runnable4loadpic).start();
        }
    }
    
    private void addPics2ListView() {
        LinearLayout list = (LinearLayout) this
                .findViewById(R.id.ListView_main);
        int count = list.getChildCount();
        for (int i = 0; i < count; i++) {
            LinearLayout ly = (LinearLayout) list.getChildAt(i);
            ImageView iv = (ImageView) ly
                    .findViewById(R.id.ImageView_destination_pic);
            String url = (String) iv.getTag();
            Bitmap bm = (Bitmap) pics.get(url);
            iv.setImageBitmap(bm);
            iv.invalidate();
            ly.invalidate();
        }
        list.invalidate();
    }
    
    Handler handler4find = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String res = data.getString("value");
            Log.i("mylog","请求结果为-->" + res);
            progressDialog.dismiss(); 
            creatListView(res, data.getInt("loadway"));
        }
    };

    Runnable runnable4find = new RunnableImp(LoadWay.INIT_LOAD);
    
    /**
     * Runnable实现类，用来请求景点list
     */
    class RunnableImp implements Runnable {
        //数据加载方式：1：初始加载, 2：选择spinner方式, 3：选择上拉加载方式
        LoadWay loadWay;
        RunnableImp(LoadWay loadWay) {
            this.loadWay = loadWay;
        }
        
        @Override
        public void run() {
            Bundle extras = getIntent().getExtras();
            List<NameValuePair> param = new ArrayList<NameValuePair>();
            BasicNameValuePair regionId = new BasicNameValuePair("regionCode", extras.getString("regionId"));
            param.add(regionId);
            BasicNameValuePair classicFlag = new BasicNameValuePair("classicFlag",  extras.getString("classicFlag"));
            param.add(classicFlag);
            BasicNameValuePair labels = new BasicNameValuePair("labels", extras.getString("labels"));
            param.add(labels);
            BasicNameValuePair count  = new BasicNameValuePair("count", String.valueOf(queryCounts));
            param.add(count);
            String res = null;
            try {
                res = HttpUtil.postUrl(FIND_URL, param);
            } catch (ClientProtocolException e1) {
                Log.e(TAG, errMsg, e1);
            } catch (IOException e1) {
                Log.e(TAG, errMsg, e1);
            } catch (Exception e2) {
                Log.e(TAG, errMsg, e2);
            } 
            Message msg = new Message();
            Bundle data = new Bundle();
            data.putString("value",res);
            //传输加载类型
            data.putInt("loadway", loadWay.ordinal());
            msg.setData(data);
            handler4find.sendMessage(msg);
        }
    }

    Handler handler4loadpic = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String res = data.getString("value");
            Log.i("mylog","请求结果为-->" + res);
            addPics2ListView();
        }
    };

    Runnable runnable4loadpic = new Runnable(){
        @Override
        public void run() {
            Log.d(TAG, "加载图片");
        	Iterator<String> iterator = urls.iterator();
        	try {
	        	while(iterator.hasNext()) {
            		String picUrl = iterator.next();
            		Bitmap bitmap = Tools.getBitmapFromUrl(picUrl);
            		pics.put(picUrl, bitmap);
	        	}
        	} catch (Exception e) {
        		Log.e(TAG, "加载图片时出错", e);
        	}
            
            Message msg = new Message();
            Bundle data = new Bundle();
            data.putString("value","ok");
            msg.setData(data);
            handler4loadpic.sendMessage(msg);
        }
        
    };

    /**
     * 上拉刷新处理
     */
	@Override
	public void onFooterRefresh(PullToRefreshView view) {
		++queryCounts;
		Log.d("请求次数", queryCounts + "");
		new Thread(new RunnableImp(LoadWay.POLLUP_LOAD)).start();
	}
    
}
