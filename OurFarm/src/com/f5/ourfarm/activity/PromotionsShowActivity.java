package com.f5.ourfarm.activity;

import static com.f5.ourfarm.util.URLConstants.GET_ACTIVITY;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.f5.ourfarm.R;
import com.f5.ourfarm.model.ActivitisInfo;
import com.f5.ourfarm.model.LoadWay;
import com.f5.ourfarm.util.Constants;
import com.f5.ourfarm.util.HttpUtil;
import com.f5.ourfarm.util.Tools;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.umeng.analytics.MobclickAgent;

/**
 * 显示活动页面
 * 
 * @author lify
 */
public class PromotionsShowActivity extends BaseActivity {
    //去官方网站
    OnClickListener gotoNet = null;
    //loadingbar
    private LinearLayout loadingLayout;
    //活动概要信息
    private LinearLayout promotionsLayout;
    
    //存放活动List
    private List<ActivitisInfo> promotionsList = new ArrayList<ActivitisInfo>();
    
  	private static String TAG = "活动显示页面";
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); 
        setContentView(R.layout.activity_show_promotions);
        //准备listeners
        this.prepareListeners();
        
        initStatus();
        
        //设置listeners
        this.batchSetListeners();
    }
    
    /**
     * 初始页面时的控件状态设定
     */
    private void initStatus() {
        //loadingbar
        loadingLayout = (LinearLayout)findViewById(R.id.nearby_loadingbar);
        //概要信息
        promotionsLayout = (LinearLayout)findViewById(R.id.ListView_main);
        
        //设置显示情况
        loadingLayout.setVisibility(View.VISIBLE);
        promotionsLayout.setVisibility(View.GONE);
        
    	new Thread(new RunnableImp(LoadWay.INIT_LOAD)).start();
        
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
        //去官方网站
        gotoNet = new OnClickListener(){
            public void onClick(View v) {
            	// 判断网络是否可用，若不可用则提示用户，并不进行跳转
            	if(!Tools.checkNetworkStatus(PromotionsShowActivity.this)) {
            		return;
            	}
            	Intent toWriteCommentIntent = new Intent(PromotionsShowActivity.this, CommentWriteActivity.class);
            	toWriteCommentIntent.putExtra(Constants.DESTINATION_ID, getIntent().getExtras().getLong("destinationId"));
            	toWriteCommentIntent.putExtra("destinationName", getIntent().getExtras().getString("destinationName"));
            	toWriteCommentIntent.putExtra("fromActivity", "comment");
                startActivity(toWriteCommentIntent);
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
     * 构造活动信息列表,根据查询参数活动信息并显示出来。
     */
    private void showList(List<ActivitisInfo> activityList) {
        LayoutInflater flater = LayoutInflater.from(this);
        LinearLayout list = (LinearLayout) this.findViewById(R.id.ListView_main);
        
        // 获取查询结果，循环构造概要列表
        for(ActivitisInfo activity: activityList){
        	View v = flater.inflate(R.layout.listview_child_activity, null);
        	TextView name = (TextView)v.findViewById(R.id.TextView_activity_name);
        	name.setText(activity.getName());
        	String starTime = activity.getStart_time();
        	String endTime = activity.getEnd_time();
        	//如果开始和结束时间都为空,则不显示
        	if(Tools.isEmpty(starTime) && Tools.isEmpty(endTime)) {
        		v.findViewById(R.id.LinearLayout_activity_time).setVisibility(View.GONE);
        	}
        	//设置开始结束时间
        	TextView starTimeTextView = (TextView)v.findViewById(R.id.TextView_activity_startime);
        	starTimeTextView.setText(activity.getStart_time());
        	TextView endTimeTextView = (TextView)v.findViewById(R.id.TextView_activity_endtime);
        	endTimeTextView.setText(activity.getEnd_time());
        	//内容
        	TextView introduction = (TextView)v.findViewById(R.id.TextView_activity_text);
        	introduction.setText(activity.getIntroduction());
        	//地址
        	TextView address = (TextView)v.findViewById(R.id.TextView_activity_address);
        	address.setText(activity.getAddress());
        	//电话
        	TextView tel = (TextView)v.findViewById(R.id.TextView_activity_tel);
        	tel.setText(activity.getTel());
        	
    		//获取电话显示的一行
    		LinearLayout phoneLayout = (LinearLayout) v.findViewById(R.id.LinearLayout_activity_tel);
    		phoneLayout.setTag(activity.getTel());
    		phoneLayout.setOnClickListener(phoneCall);
    		//获取URL的一行
    		LinearLayout gotoNet = (LinearLayout) v.findViewById(R.id.LinearLayout_goto_net);
    		gotoNet.setTag(activity.getWww());
    		gotoNet.setOnClickListener(netCall);
        	
            // add view
            list.addView(v);
        }
        list.invalidate();
    }

    Handler handler4nearby = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String res = data.getString("value");
            Log.d(TAG,"请求活动结果为-->" + res);
            
        	//后台线程完成后更新显示结果
            showList(promotionsList);
            
            requestDataSuccess();
        }
    };
    
    /**
     * Runnable实现类，用来请求周边农家乐list
     */
    class RunnableImp implements Runnable {
        //数据加载方式：1：初始加载, 2：选择spinner方式, 3：选择上拉加载方式
        LoadWay loadWay;
        RunnableImp(LoadWay loadWay) {
            this.loadWay = loadWay;
        }
        
        @Override
        public void run() {
        	Message msg = new Message();
            Bundle data = new Bundle();
            data.putString("value","ok");
            msg.setData(data);
            
            String errMsg = "读取活动信息出错";
            String res4Comment = null;
            try {
            	res4Comment = HttpUtil.postUrl(GET_ACTIVITY, getParameters());
            	//TODO 模拟数据测试
    			try {
    				Gson gson = new Gson();
    				promotionsList = gson.fromJson(res4Comment, 
    	            		new TypeToken<List<ActivitisInfo>>() {}.getType());
    			} catch (JsonSyntaxException e) {
    				Log.e(TAG, "解析获取活动信息失败", e);
    				Tools.showToastLong(PromotionsShowActivity.this, "数据加载失败。");
                    return;
    			}
    			//没有找到活动信息时
    			if(promotionsList == null || promotionsList.size() <= 0) {
    				Tools.showToastLong(PromotionsShowActivity.this, "没有查询到活动信息。");
    				requestDataSuccess();
                	return;
    			} 
                
            } catch (ClientProtocolException e1) {
                Log.e(TAG, errMsg, e1);
                getDataError();
                return;
            } catch (IOException e1) {
                Log.e(TAG, errMsg, e1);
                getDataError();
                return;
            } catch (Exception e1) {
            	Log.e(TAG, errMsg, e1);
            	getDataError();
                return;
            }
            
            handler4nearby.sendMessage(msg);
        }
    }
    
    /**
     * 请求景点列表时，传入的参数
     * 
     * @param defaultDistance 搜索的距离
     * @return 包含参数对象的list
     */
    private List<NameValuePair> getParameters() {
        List<NameValuePair> param = new ArrayList<NameValuePair>();
        
         //第几次请求
        BasicNameValuePair count = new BasicNameValuePair("count", "1");
        param.add(count);
        return param;
    }

	
	/**
	 * 数据请求成功时画面的显示
	 */
	private void requestDataSuccess() {
		//上方圆圈进度条消失
        if (loadingLayout != null) {
             loadingLayout.setVisibility(View.GONE);
        }
        
    	promotionsLayout.setVisibility(View.VISIBLE);
	}

}
