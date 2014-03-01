package com.f5.ourfarm.activity;

import static com.f5.ourfarm.util.Constants.PROGRESS_MESSAGE;
import static com.f5.ourfarm.util.Constants.PROGRESS_TITLE;
import static com.f5.ourfarm.util.URLConstants.ACTIVITY_URL;
import static com.f5.ourfarm.util.URLConstants.HOTTOP_URL;
import static com.f5.ourfarm.util.URLConstants.PROMOTIONS_URL;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.ProgressDialog;
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
import android.widget.TextView;

import com.f5.ourfarm.R;
import com.f5.ourfarm.activity.HotActivity.RunnableImp;
import com.f5.ourfarm.layout.PullToRefreshView;
import com.f5.ourfarm.layout.PullToRefreshView.OnFooterRefreshListener;
import com.f5.ourfarm.model.Activitis;
import com.f5.ourfarm.model.LoadWay;
import com.f5.ourfarm.model.Promotions;
import com.f5.ourfarm.util.HttpUtil;
import com.f5.ourfarm.util.Tools;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.umeng.analytics.MobclickAgent;

/**
 * 活动促销
 * 
 * @author tianhao
 *
 */
public class PromotionsActivity extends Activity implements OnFooterRefreshListener {
    
    public String res4activity ;
    public String res4promotions;
    
    OnClickListener lback = null;
    OnClickListener lactivity = null;
    OnClickListener lpromotions = null;
    OnClickListener ldetails = null;
    
    //进度条
    ProgressDialog progressDialog = null;
  
    //记录第几次请求，每请求一次，返回10条记录
    private int queryCountsActivity = 1;//活动
    private int queryCountsPromotions = 1;//促销
    //上拉刷新
    PullToRefreshView mPullToRefreshView;
    
    private static String TagPro = "促销页面";
    private static String errMsgPro = "获取促销信息失败";
    private static String TagAct = "活动页面";
    private static String errMsgAct = "获取活动信息失败";
    
    private static String msgActUploadNodata = "没有更多的活动了"; 
    private static String msgProUploadNodata = "没有更多的促销了"; 
    private static String msgActInitNodata = "查询不到活动信息"; 
    private static String msgProInitNodata = "查询不到促销信息"; 

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 去除标题栏
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_promotions);
        
        showBackgroundActivity();
        
        //初始化，表示请求是第一次
        queryCountsActivity = 1;
        queryCountsPromotions = 1;
        mPullToRefreshView = (PullToRefreshView) findViewById(R.id.main_pull_refresh_view);
        
        //准备listeners
        this.prepareListeners();
        //设置listeners
        this.batchSetListeners();
        
        progressDialog = ProgressDialog.show(PromotionsActivity.this, PROGRESS_TITLE, PROGRESS_MESSAGE, true);
        progressDialog.setCancelable(true);
        //设置listview
        new Thread(runnable4activity).start();
        new Thread(runnable4promotions).start();
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
     * 点击活动时背景颜色
     */
    private void showBackgroundActivity() {
        PromotionsActivity.this.findViewById(R.id.TextView_activity_promotion).
        	setBackgroundResource(R.drawable.activity_buttom);
        PromotionsActivity.this.findViewById(R.id.TextView_activity_activity).
        	setBackgroundResource(R.drawable.activity_buttom_activity);
    }
    
    /**
     * 点击促销时背景颜色
     */
    private void showBackgroundPromotion() {
        PromotionsActivity.this.findViewById(R.id.TextView_activity_promotion).
        	setBackgroundResource(R.drawable.activity_buttom_activity);
        PromotionsActivity.this.findViewById(R.id.TextView_activity_activity).
        	setBackgroundResource(R.drawable.activity_buttom);
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
        //活动
        lactivity = new  OnClickListener() {
            public void onClick(View v) {
            	showBackgroundActivity();
            	
            	//活动可见，促销隐藏
            	PromotionsActivity.this.findViewById(R.id.ListView_main_active).
            		setVisibility(View.VISIBLE);
            	PromotionsActivity.this.findViewById(R.id.ListView_main_promotions).
            	setVisibility(View.GONE);
            }
        };
        //促销
        lpromotions = new  OnClickListener() {
            public void onClick(View v) {
            	showBackgroundPromotion();
            	//促销可见，活动隐藏
            	PromotionsActivity.this.findViewById(R.id.ListView_main_active).
            		setVisibility(View.GONE);
            	PromotionsActivity.this.findViewById(R.id.ListView_main_promotions).
            	setVisibility(View.VISIBLE);
            }
        };
    }
    
    /**
     * 绑定view和监听
     */
    private void batchSetListeners() {
    	//返回
        ImageView iback2home = (ImageView) this.findViewById(R.id.ImageView_button_back);
        iback2home.setOnClickListener(lback);
        
        //活动
        TextView activity = (TextView) this.findViewById(R.id.TextView_activity_activity);
        activity.setOnClickListener(lactivity);
        
        //促销
        TextView promotions = (TextView) this.findViewById(R.id.TextView_activity_promotion);
        promotions.setOnClickListener(lpromotions);
        
        //上拉刷新
        mPullToRefreshView.setOnFooterRefreshListener(this);
    }
    
    /**
     * 加载没有数据时的处理
     * 
     * @param loadWay 加载方式：初始化或者上拉加载
     * @param msgInit 初始化无数据消息提示
     * @param msgUpload 上拉加载无数据消息提示
     */
    private void loadNoData(int loadWay, String msgInit, String msgUpload) {
    	if(loadWay == LoadWay.POLLUP_LOAD.ordinal()) {
    		Tools.showToastLong(PromotionsActivity.this, msgUpload);
    		mPullToRefreshView.onFooterRefreshComplete();
    	} else {
    		//点击搜索按钮，没有查询出数据，进行提示
    		Tools.showToastLong(PromotionsActivity.this, msgInit);
    	}
    }

    /**
     * 组装促销信息
     * 
     * @param res
     */
    private void creatListView4promotions(String res, int loadWay) {
        LayoutInflater flater = LayoutInflater.from(this);
        LinearLayout list = (LinearLayout) this
                .findViewById(R.id.ListView_main_promotions);
        List<Promotions> lds = null;
        try{
	        Gson gson = new Gson();
	        lds = gson.fromJson(res, new TypeToken<List<Promotions>>() {}.getType());
		} catch (JsonSyntaxException e) {
//	        Tools.showToastShort(PromotionsActivity.this, errMsgPro);
//	        PromotionsActivity.this.finish();
	        loadNoData(loadWay, msgProInitNodata, msgProUploadNodata);
	        return;
		}
        if(lds == null || lds.size() == 0){
        	loadNoData(loadWay, msgProInitNodata, msgProUploadNodata);
        	return;
        } 
        
        for (Promotions ds : lds) {
            View v = flater.inflate(R.layout.listview_child_promotions,null);
         // name
            TextView tvName = (TextView) v
                    .findViewById(R.id.ListView_chlid_name);
            tvName.setText(ds.getName());
            // start time
            TextView tvSTime = (TextView) v
                    .findViewById(R.id.ListView_child_start);
            tvSTime.setText("开始时间："+ds.getStartTime());
         // end time
            TextView tvETime = (TextView) v
                    .findViewById(R.id.ListView_child_end);
            tvETime.setText("结束时间："+ds.getEndTime());
            //Content
            TextView tvContent = (TextView) v
                    .findViewById(R.id.ListView_chlid_content);
            tvContent.setText(ds.getContent());
            //
            list.addView(v);
            v.invalidate();
            list.invalidate();
        }
        
        mPullToRefreshView.onFooterRefreshComplete();
    }
    
    /**
     * 组装活动信息数据
     * 
     * @param res
     */
    private void creatListView4activity(String res, int loadWay) {
        LayoutInflater flater = LayoutInflater.from(this);
        LinearLayout list = (LinearLayout) this
                .findViewById(R.id.ListView_main_active);
        List<Activitis> lds = null;
        try{
	        Gson gson = new Gson();
	        lds = gson.fromJson(res, new TypeToken<List<Activitis>>() {}.getType());
		} catch (JsonSyntaxException e) {
//	        Tools.showToastShort(PromotionsActivity.this, errMsgAct);
//	        PromotionsActivity.this.finish();
	        loadNoData(loadWay, msgActInitNodata, msgActUploadNodata);
        	return;
		}
        if(lds == null || lds.size() == 0){
        	loadNoData(loadWay, msgActInitNodata, msgActUploadNodata);
        	return;
        }
        
        for (Activitis ds : lds) {
            View v = flater.inflate(R.layout.listview_child_promotions,null);
         // name
            TextView tvName = (TextView) v
                    .findViewById(R.id.ListView_chlid_name);
            tvName.setText(ds.getName());
            // start time
            TextView tvSTime = (TextView) v
                    .findViewById(R.id.ListView_child_start);
            tvSTime.setText("开始时间："+ds.getStartTime());
         // end time
            TextView tvETime = (TextView) v
                    .findViewById(R.id.ListView_child_end);
            tvETime.setText("结束时间："+ds.getEndTime());
            //Content
            TextView tvContent = (TextView) v
                    .findViewById(R.id.ListView_chlid_content);
            tvContent.setText(ds.getContent());
            //
            list.addView(v);
            v.invalidate();
            list.invalidate();
        }
        mPullToRefreshView.onFooterRefreshComplete();
    }
    //活动
    Handler handler4activity = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String res = data.getString("value");
            Log.i(TagAct, "请求结果为-->" + res);
            progressDialog.dismiss();
            //保存结果
            setRes4activity(res);
            creatListView4activity(res, data.getInt("loadway"));
        }
    };

    //活动
    Runnable runnable4activity = new RunnableImpActivity(LoadWay.INIT_LOAD);
    
    class RunnableImpActivity implements Runnable {
        //数据加载方式：1：初始加载, 2：选择spinner方式, 3：选择上拉加载方式
        LoadWay loadWay;
        RunnableImpActivity(LoadWay loadWay) {
            this.loadWay = loadWay;
        }
        
        @Override
        public void run() {
            List<NameValuePair> param = new ArrayList<NameValuePair>();
            BasicNameValuePair count  = new BasicNameValuePair("count ", String.valueOf(queryCountsActivity));
            param.add(count);
            String res = null;
            try {
                res = HttpUtil.postUrl(ACTIVITY_URL, param);
            } catch (ClientProtocolException e1) {
                Log.e(TagAct, errMsgAct, e1);
            } catch (IOException e1) {
                Log.e(TagAct, errMsgAct, e1);
            }
            Message msg = new Message();
            Bundle data = new Bundle();
            data.putString("value",res);
            //传输加载类型
            data.putInt("loadway", loadWay.ordinal());
            msg.setData(data);
            handler4activity.sendMessage(msg);
        }
    }
    
    //促销
    Handler handler4promotions = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String res = data.getString("value");
            Log.i(TagPro, "请求结果为-->" + res);
            progressDialog.dismiss();
            //保存结果
            setRes4promotions(res);
            creatListView4promotions(res, data.getInt("loadway"));
        }
    };

    //促销信息
    Runnable runnable4promotions = new RunnableImpPromotions(LoadWay.INIT_LOAD);
    
    class RunnableImpPromotions implements Runnable {
        //数据加载方式：1：初始加载, 2：选择spinner方式, 3：选择上拉加载方式
        LoadWay loadWay;
        RunnableImpPromotions(LoadWay loadWay) {
            this.loadWay = loadWay;
        }
        
        @Override
        public void run() {
            List<NameValuePair> param = new ArrayList<NameValuePair>();
            BasicNameValuePair count  = new BasicNameValuePair("count ", String.valueOf(queryCountsPromotions));
            param.add(count);
            String res = null;
            try {
                res = HttpUtil.postUrl(PROMOTIONS_URL, param);
            } catch (ClientProtocolException e1) {
                Log.e(TagPro, errMsgPro, e1);
            } catch (IOException e1) {
                Log.e(TagPro, errMsgPro, e1);
            }
            Message msg = new Message();
            Bundle data = new Bundle();
            data.putString("value",res);
            //传输加载类型
            data.putInt("loadway", loadWay.ordinal());
            msg.setData(data);
            handler4promotions.sendMessage(msg);
        }
    }

    public String getRes4activity() {
        return res4activity;
    }

    public void setRes4activity(String res4activity) {
        this.res4activity = res4activity;
    }

    public String getRes4promotions() {
        return res4promotions;
    }

    public void setRes4promotions(String res4promotions) {
        this.res4promotions = res4promotions;
    }
    
    /**
     * 上拉刷新处理                                                                                                                                                                                                                          
     */
	@Override
	public void onFooterRefresh(PullToRefreshView view) {
		
		LinearLayout listActive = (LinearLayout) this
                .findViewById(R.id.ListView_main_active);
		//判断哪个是活动、促销
		if(listActive.getVisibility() == View.VISIBLE) {
			++queryCountsActivity;
			Log.d("活动请求次数", queryCountsActivity + "");
			new Thread(new RunnableImpActivity(LoadWay.POLLUP_LOAD)).start();
		} else {
			++queryCountsPromotions;
			Log.d("促销请求次数", queryCountsPromotions + "");
			new Thread(new RunnableImpPromotions(LoadWay.POLLUP_LOAD)).start();
		}
	}
}
