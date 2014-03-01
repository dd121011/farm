package com.f5.ourfarm.activity;

import static com.f5.ourfarm.util.URLConstants.PLAN_URL;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.f5.ourfarm.R;
import com.f5.ourfarm.activity.AroundFarmActivity.RunnableImp;
import com.f5.ourfarm.layout.PullToRefreshView;
import com.f5.ourfarm.layout.PullToRefreshView.OnFooterRefreshListener;
import com.f5.ourfarm.model.LoadWay;
import com.f5.ourfarm.model.Summary;
import com.f5.ourfarm.util.Constants;
import com.f5.ourfarm.util.HttpUtil;
import com.f5.ourfarm.util.Tools;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.umeng.analytics.MobclickAgent;

/**
 * 计划列表
 * 
 * @author lify
 *
 */
public class PlanActivity extends BaseActivity implements OnFooterRefreshListener{
    OnClickListener details = null;
    OnClickListener showMap = null;
    
    //loadingbar
    private LinearLayout loadingLayout;
    //概要信息
    private LinearLayout summaryLayout;
    //上拉刷新
    PullToRefreshView mPullToRefreshView;
    
    //计划出行列表内容
    private List<Summary> planList = new ArrayList<Summary>();
    //记录第几次请求，每请求一次，返回10条记录
    private int queryCounts = 1;
    
  	private static String TAG = "计划列表页面";
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); 
        setContentView(R.layout.activity_plan);
        
        //准备listeners
        this.prepareListeners();
        initStatus();
        //设置listeners
        this.batchSetListeners();
        
        new Thread(new RunnableImp(LoadWay.INIT_LOAD)).start();
    }
    
    /**
     * 初始页面时的控件状态设定
     */
    private void initStatus() {
        mPullToRefreshView = (PullToRefreshView) findViewById(R.id.main_pull_refresh_view);
        //不能上拉页面
        mPullToRefreshView.lock();
        
        //loadingbar
        loadingLayout = (LinearLayout)findViewById(R.id.nearby_loadingbar);
        //概要信息
        summaryLayout = (LinearLayout)findViewById(R.id.ListView_main);
        
        //设置显示情况
        loadingLayout.setVisibility(View.VISIBLE);
        summaryLayout.setVisibility(View.GONE);
        //初始化，表示请求是第一次
        queryCounts = 1;
        //更改title值
        TextView planName = (TextView) this.findViewById(R.id.TextView_toppanel_title);
        planName.setText(getIntent().getExtras().getString("planName"));
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
        //details
        details = new OnClickListener(){
            public void onClick(View v) {
            	// 判断网络是否可用，若不可用则提示用户，并不进行跳转
            	if(!Tools.checkNetworkStatus(PlanActivity.this)) {
            		return;
            	}
                long destinationId =  (Long) v.getTag(); 
                Log.d("res", ""+destinationId);
                Intent nearby2detail = new Intent(PlanActivity.this,DetailActivity.class);
                //将服务器返回数据保存入Intent，确保数据可在activity间传递
                nearby2detail.putExtra("destinationId", destinationId);
                //用于判断点击的是那种类型
                nearby2detail.putExtra("planType", getIntent().getExtras().getInt("planType"));
                
                startActivity(nearby2detail);
            }
        };
        //map
        showMap = new OnClickListener(){
            public void onClick(View v) {
            	// 判断网络是否可用，若不可用则提示用户，并不进行跳转
            	if(!Tools.checkNetworkStatus(PlanActivity.this)) {
            		return;
            	}
            	
            	//为了给地图显示用,沿用原来的做法，将
                HashMap<Long, Summary> lds = new HashMap<Long ,Summary>();
                if(planList != null){
	                for(Summary summary : planList) {
	                	lds.put(summary.getDestinationId(), summary);
	                }
                }
                
            	//将要显示的内容传到地图页面
                Intent toMapIntent = new Intent(PlanActivity.this, NearbyMapActivity.class);
                toMapIntent.putExtra(Constants.MAP_SHOW_SPOT, lds);
                toMapIntent.putExtra(Constants.MAP_SHOW_DISTANCE, 50);//选择的距离，用来判断地图级别
                startActivity(toMapIntent);
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
        //map
        ImageView i2map = (ImageView) this.findViewById(R.id.ImageView_button_map);
        i2map.setOnClickListener(showMap);
        
        mPullToRefreshView.setOnFooterRefreshListener(this);
    }
    
    /**
     * 构造附近列表
     * 根据查询参数查找附近内容，然后将内容概要信息显示出来。
     */
    private void showList(List<Summary> summaryList) {
        LayoutInflater flater = LayoutInflater.from(this);
        LinearLayout list = (LinearLayout) this
                .findViewById(R.id.ListView_main);
        
        // 获取查询结果，循环构造概要列表
        for(Summary summary: summaryList){
            Long destinationId = summary.getDestinationId();
            View v = flater.inflate(R.layout.listview_child_plan, null);
            // pic
            ImageView ivPic = (ImageView) v.findViewById(R.id.ImageView_plan_pic);
            Bitmap bm = Tools.getBitmapFromUrl(summary.getPic());
            if(bm != null){
                ivPic.setImageBitmap(bm);
            }
            
            // name
            TextView tvName = (TextView) v.findViewById(R.id.ListView_plan_name);
            tvName.setText(summary.getName());
            // sroce
            RatingBar rbSroce = (RatingBar) v.findViewById(R.id.ratingBar_plan_sroce);
            rbSroce.setRating(summary.getScore());
            // price info
            TextView tvPrice = (TextView) v.findViewById(R.id.ListView_plan_price);
            tvPrice.setText(summary.getPriceInfo());
            // Characteristic
            TextView tvCharacteristic = (TextView) v.findViewById(R.id.ListView_plan_characteristic);
            tvCharacteristic.setText("特色:" + summary.getCharacteristic());
            // add view
            list.addView(v);
            // 设置监听
            v.setTag(destinationId);
            v.setOnClickListener(details);
        }
        list.invalidate();
    }

    Handler handler4Plan = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String res = data.getString("value");
            Log.i("TAG","计划出行请求结果为-->" + res);
            
            //后台线程完成后更新显示结果
            showList(planList);
            ++queryCounts;
            
            requestDataSuccess();
        }
    };
    
    Runnable runnable4plan = new RunnableImp(LoadWay.INIT_LOAD);
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
        	Message msg = new Message();
            Bundle data = new Bundle();
            data.putString("value","ok");
            msg.setData(data);
            
            String errMsg = "读取景点概要信息出错";
            String res = null;
            try {
                res = HttpUtil.postUrl(PLAN_URL, getParameters());
                try {
    				Gson gson = new Gson();
    				planList = gson.fromJson(res, 
    	            		new TypeToken<List<Summary>>() {}.getType());
    			} catch (JsonSyntaxException e) {
    				Log.e(TAG, "解析获取出行计划信息是失败", e);
    				Tools.showToastLong(PlanActivity.this, "数据加载失败。");
                    return;
    			}
    			
    			//没有找到农家乐时
    			if(planList == null || planList.size() <= 0) {
    				Tools.showToastLong(PlanActivity.this, "没有更多的信息了。");
                	requestDataSuccess();
                	return;
    			} 
    			
            } catch (ClientProtocolException e1) {
                Log.e(TAG, errMsg, e1);
            } catch (IOException e1) {
                Log.e(TAG, errMsg, e1);
            } catch (Exception e1) {
            	Log.e(TAG, errMsg, e1);
            }
            
            handler4Plan.sendMessage(msg);
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
        //选择计划类型
        Bundle extras = getIntent().getExtras();
        int planType = extras.getInt("planType");
        //第几次请求
        BasicNameValuePair count = new BasicNameValuePair("count", String.valueOf(queryCounts));
        param.add(count);
        //选择类型
        BasicNameValuePair type = new BasicNameValuePair("type", String.valueOf(planType));
        param.add(type);
        
        return param;
    }

    /**
     * 上拉刷新处理
     */
	@Override
	public void onFooterRefresh(PullToRefreshView view) {
		Log.d("请求次数", queryCounts + "");
		new Thread(new RunnableImp(LoadWay.POLLUP_LOAD)).start();
	}
	
	/**
	 * 数据请求成功时画面的显示
	 */
	private void requestDataSuccess() {
		//上方圆圈进度条消失
        if (loadingLayout != null) {
             loadingLayout.setVisibility(View.GONE);
        }
        
    	summaryLayout.setVisibility(View.VISIBLE);
        //上拉完成加载
        mPullToRefreshView.onFooterRefreshComplete();
        //可以上拉刷新
        mPullToRefreshView.unlock();
	}
	
}
