package com.f5.ourfarm.activity;

import static com.f5.ourfarm.util.URLConstants.GET_COMMENT;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Intent;
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
import com.f5.ourfarm.model.Comment;
import com.f5.ourfarm.model.LoadWay;
import com.f5.ourfarm.util.Constants;
import com.f5.ourfarm.util.HttpUtil;
import com.f5.ourfarm.util.Tools;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.umeng.analytics.MobclickAgent;

/**
 * 显示评论页面
 * 
 * @author lify
 */
public class CommentShowActivity extends Activity implements OnFooterRefreshListener{
    OnClickListener lback = null;
//    OnClickListener details = null;
    // 写评论
    OnClickListener writeComment = null;
    
    //loadingbar
    private LinearLayout loadingLayout;
    //概要信息
    private LinearLayout summaryLayout;
    //上拉刷新
    PullToRefreshView mPullToRefreshView;
    
    //存放评论List
    private List<Comment> commentList = new ArrayList<Comment>();
    
    //记录第几次请求，每请求一次，返回10条记录
    private int queryCounts = 1;
    
  	private static String TAG = "周边农家乐页面";
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); 
        // 去除标题栏
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //准备listeners
        this.prepareListeners();
        setContentView(R.layout.activity_show_comment);
        
        initStatus();
        
        //设置listeners
        this.batchSetListeners();
    }
    
    /**
     * 初始页面时的控件状态设定
     */
    private void initStatus() {
    	//Titie上面显示评论的景点名称
    	TextView commentName = (TextView) this.findViewById(R.id.comment_name);
    	commentName.setText(getIntent().getExtras().getString("destinationName"));
    	
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
        
        queryCounts = 1;
    	new Thread(new RunnableImp(LoadWay.INIT_LOAD)).start();
        
    	/*
        //获取上页请求到的评论
        commentList = (List<Comment>)getIntent().getSerializableExtra(Constants.MORE_COMMENT);
        if(commentList != null && commentList.size() > 0) {
        	//初始化，表示请求是第2次
            queryCounts = 2;
        	showList(commentList);
        	requestDataSuccess();
        } else {
        	queryCounts = 1;
        	new Thread(new RunnableImp(LoadWay.INIT_LOAD)).start();
        }
        */
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
//                Intent nearby2detail = new Intent(CommentShowActivity.this,DetailActivity.class);
//                //将服务器返回数据保存入Intent，确保数据可在activity间传递
//                nearby2detail.putExtra(Constants.DESTINATION_ID, getIntent().getExtras().getLong("destinationId"));
//                //用于判断点击的是那种类型
//                nearby2detail.putExtra("nearbyType", "2");
//                
//                startActivity(nearby2detail);
//            	getIntent().getExtras().putInt("nearbyType", getIntent().getExtras().getInt("nearbyType"));
                finish();
            }
        };
        
        //写评论
        writeComment = new OnClickListener(){
            public void onClick(View v) {
            	// 判断网络是否可用，若不可用则提示用户，并不进行跳转
            	if(!Tools.checkNetworkStatus(CommentShowActivity.this)) {
            		return;
            	}
            	Intent toWriteCommentIntent = new Intent(CommentShowActivity.this, CommentWriteActivity.class);
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
        
        mPullToRefreshView.setOnFooterRefreshListener(this);
        
        //书写评论按钮
        ImageView writeCommentImageView = (ImageView)findViewById(R.id.ImageView_write_comment);
        writeCommentImageView.setOnClickListener(writeComment);
    }
    
    
    /**
     * 构造评论列表
     * 根据查询参数评论内容并显示出来。
     */
    private void showList(List<Comment> aroundFarm) {
        LayoutInflater flater = LayoutInflater.from(this);
        LinearLayout list = (LinearLayout) this.findViewById(R.id.ListView_main);
        
        // 获取查询结果，循环构造概要列表
        for(Comment comment: aroundFarm){
        	View v = flater.inflate(R.layout.listview_child_comment, null);
        	TextView name = (TextView)v.findViewById(R.id.TextView_comment_name);
        	//用户名称
        	if(comment.getUser_id() != 0) {
        		name.setText(String.valueOf(comment.getUser_id()));
        	} else {
        		name.setText("匿名用户");
        	}
        	//评论星星
        	RatingBar ratingBar = (RatingBar)v.findViewById(R.id.ratingBar_comment_sroce);
        	ratingBar.setRating(comment.getComment_score());
        	
        	//评论内容
        	TextView content = (TextView)v.findViewById(R.id.TextView_comment_text);
        	content.setText(comment.getContent());
        	//评论时间
        	TextView commenTime = (TextView)v.findViewById(R.id.TextView_comment_time);
        	commenTime.setText(comment.getComment_time());
        	
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
            Log.d(TAG,"请求评论结果为-->" + res);
            
        	//后台线程完成后更新显示结果
            showList(commentList);
            ++queryCounts;
            
            requestDataSuccess();
        }
    };
    
    Runnable runnable4nearby = new RunnableImp(LoadWay.INIT_LOAD);
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
            
            String Tag = "NearbyService";
            String errMsg = "读取评论信息出错";
            String res4Comment = null;
            try {
            	res4Comment = HttpUtil.postUrl(GET_COMMENT, getParameters());
    			try {
    				Gson gson = new Gson();
    				commentList = gson.fromJson(res4Comment, 
    	            		new TypeToken<List<Comment>>() {}.getType());
    			} catch (JsonSyntaxException e) {
    				Log.e(TAG, "解析获取评论信息是失败", e);
    				Tools.showToastLong(CommentShowActivity.this, "数据加载失败。");
                    return;
    			}
    			
    			//没有找到评论时
    			if(commentList == null || commentList.size() <= 0) {
    				Tools.showToastLong(CommentShowActivity.this, "没有更多的评论了。");
    				requestDataSuccess();
                	return;
    			} 
                
            } catch (ClientProtocolException e1) {
                Log.e(Tag, errMsg, e1);
                getDataError();
                return;
            } catch (IOException e1) {
                Log.e(Tag, errMsg, e1);
                getDataError();
                return;
            } catch (Exception e1) {
            	Log.e(Tag, errMsg, e1);
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
        
        //景点ID
        BasicNameValuePair view_id = new BasicNameValuePair("destination_id", 
        		String.valueOf(getIntent().getExtras().getLong("destinationId")));
        param.add(view_id);
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

	/**
	 * 请求数据失败
	 */
	private void getDataError() {
		Tools.showToastLong(CommentShowActivity.this, "数据加载失败。");
	}
}
