package com.f5.ourfarm.activity;

import static com.f5.ourfarm.util.URLConstants.GET_TOP;

import java.io.IOException;
import java.util.ArrayList;
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
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.f5.ourfarm.R;
import com.f5.ourfarm.layout.PullToRefreshView;
import com.f5.ourfarm.layout.PullToRefreshView.OnFooterRefreshListener;
import com.f5.ourfarm.model.DestinationType;
import com.f5.ourfarm.model.LoadWay;
import com.f5.ourfarm.model.RankingParams;
import com.f5.ourfarm.model.Summary;
import com.f5.ourfarm.util.HttpUtil;
import com.f5.ourfarm.util.Tools;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.umeng.analytics.MobclickAgent;

/**
 * 显示排名页面
 * 
 * @author lify
 */
public class RankingShowActivity extends BaseActivity implements OnFooterRefreshListener{
	
    OnClickListener details = null;
    
    //切换不同类型的排名
    OnClickListener rankingChange = null;
    
    //loadingbar
    private LinearLayout loadingLayout;
    //概要信息
    private LinearLayout summaryLayout;
    //上拉刷新
    PullToRefreshView mPullToRefreshView;
    
    private TextView rankingTitle;
    
    //存放排名List
    private List<Summary> rankingList = new ArrayList<Summary>();
    
    //记录第几次请求，每请求一次，返回10条记录
    private int queryCounts = 1;
    
    //弹出窗口显示的内容
    private List<String> groups = DestinationType.getGroupList();
    
    //附近,计划出行
    private int type = 1;
    //具体类型
    private int type_value = 1;
    
  	private static String TAG = "排行页面";
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); 
        // 去除标题栏
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_ranking);
        
        //准备listeners
        this.prepareListeners();
        //设置listeners
        this.batchSetListeners();
        initStatus();
        
    }
    
    /**
     * 初始页面时的控件状态设定
     */
    private void initStatus() {
    	//Titie上面默认为景点
    	rankingTitle.setText("景点");
        //不能上拉页面
        mPullToRefreshView.lock();
        
        //loadingbar
        loadingLayout = (LinearLayout)findViewById(R.id.nearby_loadingbar);
        //概要信息
        summaryLayout = (LinearLayout)findViewById(R.id.ListView_main);
        
        //设置显示情况
        loadingLayout.setVisibility(View.VISIBLE);
        summaryLayout.setVisibility(View.GONE);
        
        queryCounts = 1;
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
    	
    	//切换不同类型的排名
    	rankingChange = new OnClickListener(){
            public void onClick(View v) {
            	showPopupWindow(v, groups, new OnItemClickListener() {  
          		  
                    @Override  
                    public void onItemClick(AdapterView<?> adapterView, View view,  
                            int position, long id) {  
                    	//选择的排名类型
                    	RankingParams rankingParams = DestinationType.getKeyFromTypeName(groups.get(position));
                    	if(rankingParams == null) return;
                    	
                    	type = rankingParams.getType();
                    	type_value = rankingParams.getType_value();
                    	//Titie上面默认为景点
                    	rankingTitle.setText(rankingParams.getName());
                    	
                    	//不能上拉页面
                        mPullToRefreshView.lock();
                        //设置显示情况
                        loadingLayout.setVisibility(View.VISIBLE);
                        summaryLayout.setVisibility(View.GONE);
                    	queryCounts = 1;
                    	//清除原来的记录
                    	LinearLayout list = (LinearLayout) RankingShowActivity.this.findViewById(R.id.ListView_main);
                    	list.removeAllViews();
                    	new Thread(new RunnableImp(LoadWay.INIT_LOAD)).start();
                    	
                        if (popupWindow != null) {  
                            popupWindow.dismiss();  
                        }  
                    }  
                });
            }
        };
        
        //details
        details = new OnClickListener(){
            public void onClick(View v) {
            	// 判断网络是否可用，若不可用则提示用户，并不进行跳转
            	if(!Tools.checkNetworkStatus(RankingShowActivity.this)) {
            		return;
            	}
                long destinationId =  (Long)v.getTag(); 
                
                //休闲山庄
                if(type == 1 && type_value == 3) {
                	Intent nearby2mountainVilla = new Intent(RankingShowActivity.this,MountainVillaActivity.class);
                	//将服务器返回数据保存入Intent，确保数据可在activity间传递
                	nearby2mountainVilla.putExtra("destinationId", destinationId);
                	
                	startActivity(nearby2mountainVilla);
                } else {//其他类型
                	Intent nearby2detail = new Intent(RankingShowActivity.this,DetailActivity.class);
                	//将服务器返回数据保存入Intent，确保数据可在activity间传递
                	nearby2detail.putExtra("destinationId", destinationId);
                	//用于判断点击的是那种类型
                	nearby2detail.putExtra("nearbyType", type_value);
                	
                	startActivity(nearby2detail);
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
        
        //页面头部的title
        rankingTitle = (TextView) this.findViewById(R.id.TextView_ranking_title);
        rankingTitle.setOnClickListener(rankingChange);
        
        mPullToRefreshView = (PullToRefreshView) findViewById(R.id.main_pull_refresh_view);
        mPullToRefreshView.setOnFooterRefreshListener(this);
        
    }
    
    
    /**
     * 构造评论列表
     * 根据查询参数评论内容并显示出来。
     */
    private void showList(List<Summary> summaryList) {
        
        LayoutInflater flater = LayoutInflater.from(this);
        LinearLayout list = (LinearLayout) this.findViewById(R.id.ListView_main);
        
        // 获取查询结果，循环构造概要列表
        for(Summary sum: summaryList){
            Long destinationId = sum.getDestinationId();
            View v = flater.inflate(R.layout.listview_child_plan, null);
            // pic
            ImageView ivPic = (ImageView) v.findViewById(R.id.ImageView_plan_pic);
            Bitmap bm = Tools.getBitmapFromUrl(sum.getPic());
            if(bm != null){
                ivPic.setImageBitmap(bm);
            }
            // name
            TextView tvName = (TextView) v
                    .findViewById(R.id.ListView_plan_name);
            tvName.setText(sum.getName());
            // sroce
            RatingBar rbSroce = (RatingBar) v
                    .findViewById(R.id.ratingBar_plan_sroce);
            rbSroce.setRating(sum.getScore());
            // price info
            TextView tvPrice = (TextView) v
                    .findViewById(R.id.ListView_plan_price);
            tvPrice.setText(sum.getPriceInfo());
            // Characteristic
            TextView tvCharacteristic = (TextView) v
                    .findViewById(R.id.ListView_plan_characteristic);
            tvCharacteristic.setText("特色:" + sum.getCharacteristic());

            // add view
            list.addView(v);
            // 设置监听
            v.setTag(destinationId);
            v.setOnClickListener(details);
        
        }
        list.invalidate();
    }

    Handler handler4nearby = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String res = data.getString("value");
            Log.d(TAG,"请求评论结果为-->" + res);
            
        	//后台线程完成后更新显示结果
            showList(rankingList);
            ++queryCounts;
            
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
            
            String errMsg = "读取排行信息出错";
            String res4Ranking = null;
            try {
            	res4Ranking = HttpUtil.postUrl(GET_TOP, getParameters());
    			try {
    				Gson gson = new Gson();
    				rankingList = gson.fromJson(res4Ranking, new TypeToken<List<Summary>>() {}.getType());
    			} catch (JsonSyntaxException e) {
    				Log.e(TAG, "解析获取排行信息失败", e);
    				Tools.showToastLong(RankingShowActivity.this, "数据加载失败。");
                    return;
    			}
    			
    			//没有找到评论时
    			if(rankingList == null || rankingList.size() <= 0) {
    				Tools.showToastLong(RankingShowActivity.this, "没有更多的信息了。");
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
        //附近,计划出行
        BasicNameValuePair typeBNV = new BasicNameValuePair("type", String.valueOf(type));
        param.add(typeBNV);
        //具体类型
        BasicNameValuePair type_valueBNV = new BasicNameValuePair("type_value", String.valueOf(type_value));
        param.add(type_valueBNV);
        //第几次请求
        BasicNameValuePair count = new BasicNameValuePair("count", String.valueOf(queryCounts));
        param.add(count);
        
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
