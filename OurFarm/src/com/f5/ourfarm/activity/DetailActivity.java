package com.f5.ourfarm.activity;

import static com.f5.ourfarm.util.URLConstants.NEARBY_DETAIL_URL;
import static com.f5.ourfarm.util.URLConstants.GET_AROUND_FARM_HOME;
import static com.f5.ourfarm.util.URLConstants.GET_COMMENT;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.f5.ourfarm.R;
import com.f5.ourfarm.model.Comment;
import com.f5.ourfarm.model.Destination;
import com.f5.ourfarm.model.MoreAroundFarm;
import com.f5.ourfarm.model.SinaWeiboErrorMsg;
import com.f5.ourfarm.sqlite.DestinationDbAdpter;
import com.f5.ourfarm.third.weibo.OWeibo;
import com.f5.ourfarm.third.weibo.OWeiboMessageListener;
import com.f5.ourfarm.util.AccessTokenKeeper;
import com.f5.ourfarm.util.Constants;
import com.f5.ourfarm.util.HttpUtil;
import com.f5.ourfarm.util.Tools;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.umeng.analytics.MobclickAgent;
import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.Weibo;
import com.weibo.sdk.android.WeiboAuthListener;
import com.weibo.sdk.android.WeiboDialogError;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.api.StatusesAPI;
import com.weibo.sdk.android.net.RequestListener;

public class DetailActivity extends BaseActivity implements RequestListener{
	
	//显示商家图片
	OnClickListener showDetailPic = null;
	// 监听底部面板4个按钮
	OnClickListener lPanelBottom = null;
	// 地图上显示
	OnClickListener showOnMap = null;
	// 重试获取周边农家乐
	OnClickListener retryGetAroundFarm = null;
	// 周边农家乐
	OnClickListener aroundFarm = null;
	// 更多农家乐
	OnClickListener getMoreFarm = null;
	// 更多评论
	OnClickListener getMoreComment = null;
	// 重试获取周边农家乐
    OnClickListener retryGetComment = null;
    // 写评论
    OnClickListener writeComment = null;
	
	String picUrl;
	Bitmap pic ;
	HashMap<String, Bitmap> pics = new HashMap<String ,Bitmap>();
    ArrayList<String> urls = new ArrayList<String>();
    
    //明细信息
    Destination destination = null;
    DestinationDbAdpter destinationDbAdpter;
    private long destinationId;
    
    //loadingbar
    private LinearLayout loadingLayout;
    private LinearLayout moreFramLoadingbar;
    private LinearLayout commentLoadingbar;
    //详细信息
    private LinearLayout detailSummaryLayout;
    //周边农家乐
    private RelativeLayout moreAroundFarmContent;
    //评论内容
    private LinearLayout commentContent;
    //下方菜单栏
    private LinearLayout panelBottom;
    //书写评论
    private ImageView writeCommentImageView;
    
    //景点周边的农家乐
    private ArrayList<MoreAroundFarm> farmList = null;
    //点评
    private ArrayList<Comment> commentList = null;
    //进入到的类型信息
    private int detailType;
    
    //分享到新浪微博
    private Weibo mWeibo;
    public static Oauth2AccessToken accessToken ;
    private static final String REDIRECT_URL = "http://www.sina.com";
	// 注意！！此处必须设置appkey及appsecret，如何获取新浪微博appkey和appsecret请另外查询相关信息，此处不作介绍
	private static final String CONSUMER_KEY = "2913772243";// 替换为开发者的appkey，例如"1646212860";
	public static final String EXTRA_ACCESS_TOKEN = "com.weibo.android.accesstoken";
	public static final String EXTRA_EXPIRES_IN = "com.weibo.android.token.expires";
	public static final String WEIBO_TEXT = "weibo_text";
	private static final String TAG = "详细信息页面:";
	
	private static final String errMsg = "获取景点详细信息失败";
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 去除标题栏
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        //准备listeners
        this.prepareListeners();
        setContentView(R.layout.activity_destination_detail);
        //设置listeners
        this.batchSetListeners();
        initStatus();
        
        new Thread(runnable4detail).start();
        
        //新浪微博
        mWeibo = Weibo.getInstance(CONSUMER_KEY, REDIRECT_URL);
        DetailActivity.accessToken=AccessTokenKeeper.readAccessToken(this);
        
    }
	
	/**
	 * 初始页面的状态
	 */
	private void initStatus() {
		detailType = getIntent().getExtras().getInt("nearbyType");
		//loadingbar
        loadingLayout = (LinearLayout)findViewById(R.id.loadingbar);
        loadingLayout.setVisibility(View.VISIBLE);
        moreFramLoadingbar = (LinearLayout)findViewById(R.id.more_fram_loadingbar);
        moreFramLoadingbar.setVisibility(View.VISIBLE);
        commentLoadingbar = (LinearLayout)findViewById(R.id.comment_loadingbar);
        commentLoadingbar.setVisibility(View.VISIBLE);
        //详细信息
        detailSummaryLayout = (LinearLayout)findViewById(R.id.LinearLayout_detail_summary);
        detailSummaryLayout.setVisibility(View.GONE);
        //周边农家院内容
        moreAroundFarmContent = (RelativeLayout)findViewById(R.id.more_around_farm);
        moreAroundFarmContent.setVisibility(View.GONE);
        //评论
        commentContent.setVisibility(View.GONE);
        //底部菜单栏
        panelBottom = (LinearLayout)findViewById(R.id.Layout_detail_panelBottom);
        panelBottom.setVisibility(View.GONE);
        
        //景点情况下
        if(detailType == 1) {
        	getAroundFarmInit();
        } else if(detailType == 2) {//农家乐情况下
        	getCommentInit();
        } else if(detailType == 4) {//农产品情况下
        	getCommentInit();
        } else {//其他情况下
        	getCommentInit();
        }
        
        //
        destinationDbAdpter = new DestinationDbAdpter(this);
        destinationDbAdpter.open();
	}
	
	/**
	 * 获取农家乐
	 */
	private void getAroundFarmInit() {
		//显示周边农家乐模块(错误消息+周边农家院内容)
    	LinearLayout detailAroundFarm = (LinearLayout)findViewById(R.id.LinearLayout_detail_around_farm);
    	detailAroundFarm.setVisibility(View.VISIBLE);
    	findViewById(R.id.LinearLayout_comment).setVisibility(View.GONE);
    	writeCommentImageView.setVisibility(View.GONE);
    	//获取周边农家院
        new Thread(runnable4MoreAroundFarm).start();
	}
	
	/**
	 * 获取评论
	 */
	private void getCommentInit() {
		//评论显示模块(错误消息+评论内容)
		LinearLayout detailComment = (LinearLayout)findViewById(R.id.LinearLayout_comment_block);
		detailComment.setVisibility(View.VISIBLE);
		//获取评论
		new Thread(runnable4Comment).start();
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
     * 获取详细信息
     */
    Runnable runnable4detail = new Runnable(){
        @Override
        public void run() {
            String res = "";
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                destinationId = extras.getLong("destinationId");
                List<NameValuePair> param = new ArrayList<NameValuePair>();
                BasicNameValuePair id = new BasicNameValuePair("destinationId", Long.toString(destinationId));
                param.add(id);
                try {
                    res = HttpUtil.postUrl(NEARBY_DETAIL_URL, param);
                } catch (ClientProtocolException e1) {
                    Log.e(TAG, errMsg, e1);
                } catch (IOException e1) {
                    Log.e(TAG, errMsg, e1);
                }
            }
           
            Message msg = new Message();
            Bundle data = new Bundle();
            data.putString("detail",res);
            msg.setData(data);
            DetailActivity.this.handler4detail.sendMessage(msg);
        }
    };
    
    /**
     * 获取周边农家院信息
     */
    Runnable runnable4MoreAroundFarm = new Runnable(){
    	@Override
    	public void run() {
    		String res = "";
    		Bundle extras = getIntent().getExtras();
    		if (extras != null) {
    			destinationId = extras.getLong("destinationId");
    			List<NameValuePair> param = new ArrayList<NameValuePair>();
    			BasicNameValuePair id = new BasicNameValuePair("view_id", Long.toString(destinationId));
    			param.add(id);
    			//第1次请求
    	        BasicNameValuePair count = new BasicNameValuePair("count", "1");
    	        param.add(count);
    			try {
    				res = HttpUtil.postUrl(GET_AROUND_FARM_HOME, param);
    			} catch (ClientProtocolException e1) {
    				Log.e(TAG, errMsg, e1);
    			} catch (IOException e1) {
    				Log.e(TAG, errMsg, e1);
    			}
    		}
    		
    		Message msg = new Message();
    		Bundle data = new Bundle();
    		data.putString("moreAroundFarm",res);
    		msg.setData(data);
    		DetailActivity.this.handler4MoreAroundFarm.sendMessage(msg);
    	}
    };
    /**
     * 获取农家院的评论信息
     */
    Runnable runnable4Comment = new Runnable(){
    	@Override
    	public void run() {
    		String res = "";
    		Bundle extras = getIntent().getExtras();
    		if (extras != null) {
    			destinationId = extras.getLong("destinationId");
    			List<NameValuePair> param = new ArrayList<NameValuePair>();
    			BasicNameValuePair id = new BasicNameValuePair("destination_id", Long.toString(destinationId));
    			param.add(id);
    			//第1次请求
    			BasicNameValuePair count = new BasicNameValuePair("count", "1");
    			param.add(count);
    			try {
    				res = HttpUtil.postUrl(GET_COMMENT, param);
    			} catch (ClientProtocolException e1) {
    				Log.e(TAG, errMsg, e1);
    				showGetCommentError();
    				return;
    			} catch (IOException e1) {
    				Log.e(TAG, errMsg, e1);
    				showGetCommentError();
    				return;
    			}
    		}
    		
    		Message msg = new Message();
    		Bundle data = new Bundle();
    		data.putString("comment",res);
    		msg.setData(data);
    		DetailActivity.this.handler4Comment.sendMessage(msg);
    	}
    };
    
    /**
     * 获取详细信息
     */
    Handler handler4detail = new  Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String res4detail = data.getString("detail");
            DetailActivity.this.showDetail(res4detail);
            Log.i("mylog","请求结果为-->" + res4detail);
        }
    };
    
    /**
     * 获取周边农家乐
     */
    Handler handler4MoreAroundFarm = new  Handler() {
    	@Override
    	public void handleMessage(Message msg) {
    		super.handleMessage(msg);
    		Bundle data = msg.getData();
    		String res4MoreAroundFarm = data.getString("moreAroundFarm");
    		DetailActivity.this.showMoreAroundFarm(res4MoreAroundFarm);
    		Log.d(TAG,"周边农家乐-->" + res4MoreAroundFarm);
    	}
    };
    
    /**
     * 获取点评
     */
    Handler handler4Comment = new  Handler() {
    	@Override
    	public void handleMessage(Message msg) {
    		super.handleMessage(msg);
    		Bundle data = msg.getData();
    		String res4Comment = data.getString("comment");
    		DetailActivity.this.showComment(res4Comment);
    		Log.d(TAG,"点评内容-->" + res4Comment);
    	}
    };
    
	/**
     * 展示明细信息
     */
	private void showDetail(String res4detail) throws JsonSyntaxException{
		if (res4detail != null) {
			try {
				Gson gson = new Gson();
				destination = gson.fromJson(res4detail,
						new TypeToken<Destination>() {
						}.getType());
			} catch (JsonSyntaxException e) {
				Log.d(TAG, "解析明细信息失败", e);
                Tools.showToastShort(DetailActivity.this, errMsg);
                DetailActivity.this.finish();
			}
			
			if (destination != null) {
				// name
				TextView tvName = (TextView) this
						.findViewById(R.id.ListView_destination_name);
				tvName.setText(destination.getScenerySummary().getName());
				// sroce
				RatingBar rbSroce = (RatingBar) this
						.findViewById(R.id.ratingBar_destination_sroce);
				rbSroce.setRating(destination.getScenerySummary().getScore());
				// price info
				TextView tvPrice = (TextView) this
						.findViewById(R.id.ListView_destination_price);
				tvPrice.setText(destination.getScenerySummary().getPriceInfo());
				// address
				TextView tvAdress = (TextView) this
						.findViewById(R.id.TextView_detail_address);
				tvAdress.setText(destination.getScenerySummary().getAddress());
				// tel
				TextView tvTel = (TextView) this
						.findViewById(R.id.TextView_detail_tel);
				tvTel.setText(destination.getScenerySummary().getTel());
				//获取电话显示的一行
        		LinearLayout phoneLayout = (LinearLayout) this
        				.findViewById(R.id.LinearLayout_detail_tel);
        		phoneLayout.setTag(destination.getScenerySummary().getTel());
        		phoneLayout.setOnClickListener(phoneCall);
        		
				// introduction_name
				TextView tvName4introduction = (TextView) this
						.findViewById(R.id.TextView_introduction_name);
				tvName4introduction.setText(destination.getScenerySummary()
						.getName());
				// introduction
				TextView tvIntroduction = (TextView) this
						.findViewById(R.id.TextView_introduction_detail);
				tvIntroduction.setText(destination.getIntroduction());
				// preferentialInfo
				TextView tvPreferentialInfo = (TextView) this
						.findViewById(R.id.TextView_preferentialInfo_detail);
				tvPreferentialInfo.setText(destination.getPreferentialInfo());
				//pic
				picUrl = destination.getScenerySummary().getPic();
                new Thread(runnable4pic).start();
                
                //显示画面，加载bar去掉
                loadingLayout.setVisibility(View.GONE);
                detailSummaryLayout.setVisibility(View.VISIBLE);
                panelBottom.setVisibility(View.VISIBLE);
			}
		}

	}
	
	/**
     * 展示周边农家乐信息
     */
	private void showMoreAroundFarm(String res4MoreAroundFarm) throws JsonSyntaxException{
		if (res4MoreAroundFarm != null) {
			try {
				Gson gson = new Gson();
				farmList = gson.fromJson(res4MoreAroundFarm, 
	            		new TypeToken<List<MoreAroundFarm>>() {}.getType());
			} catch (JsonSyntaxException e) {
				Log.e(TAG, "解析获取周边农家乐信息失败", e);
                showGetFarmError();
                return;
			}
		}
		//没有找到农家乐时
		if(farmList == null || farmList.size() <= 0) {
			showCanNotGetFarm();
			return;
		} else if (farmList.size() < 4) {
			//小于4条时，不显示更多按钮
			this.findViewById(R.id.get_more_farm).setVisibility(View.GONE);
		}
		
		//TODO 这种写法感觉不舒服，要重构
        for(int i = 0; i < farmList.size(); i++){
        	//只显示前3个
        	if(i == 3) break;
        	if(i == 0) {
        		setecommendAroundFarm(farmList.get(i), 
        				(ImageView) this.findViewById(R.id.ImageView_recommend_farm_pic1),
        				(TextView) this.findViewById(R.id.TextView_detail_recommend_farm1));
        		this.findViewById(R.id.RelativeLayout_recommend_farm1).setVisibility(View.VISIBLE);
        	} else if(i == 1) {
        		setecommendAroundFarm(farmList.get(i), 
        				(ImageView) this.findViewById(R.id.ImageView_recommend_farm_pic2),
        				(TextView) this.findViewById(R.id.TextView_detail_recommend_farm2));
        		this.findViewById(R.id.RelativeLayout_recommend_farm2).setVisibility(View.VISIBLE);
        	} else if(i == 2) {
        		setecommendAroundFarm(farmList.get(i), 
        				(ImageView) this.findViewById(R.id.ImageView_recommend_farm_pic3),
        				(TextView) this.findViewById(R.id.TextView_detail_recommend_farm3));
        		this.findViewById(R.id.RelativeLayout_recommend_farm3).setVisibility(View.VISIBLE);
        	}
        	
        }
        
        //显示画面，加载bar去掉
        moreFramLoadingbar.setVisibility(View.GONE);
        moreAroundFarmContent.setVisibility(View.VISIBLE);
	}
	
	/**
	 * 展示品论信息
	 */
	private void showComment(String res4Comment) throws JsonSyntaxException{
		if (res4Comment != null) {
			try {
				Gson gson = new Gson();
				commentList = gson.fromJson(res4Comment, 
						new TypeToken<List<Comment>>() {}.getType());
			} catch (JsonSyntaxException e) {
				Log.e(TAG, "解析获取点评信息失败", e);
				showGetCommentError();
				return;
			}
		}
		//没有找到评论时
		if(commentList == null || commentList.size() <= 0) {
			commentLoadingbar.setVisibility(View.GONE);
			findViewById(R.id.LinearLayout_comment_block).setVisibility(View.GONE);
			return;
		}
		
		//展示最近一条评论
		Comment comment = commentList.get(0);
		//评分
		RatingBar ratingBar = (RatingBar)findViewById(R.id.ratingBar_comment_sroce);
		ratingBar.setRating(comment.getComment_score());
		//内容
		TextView text = (TextView)findViewById(R.id.TextView_comment_text);
		text.setText(comment.getContent());
		//时间
		TextView time = (TextView)findViewById(R.id.TextView_comment_time);
		time.setText(comment.getComment_time());
		
		//显示画面，加载bar去掉
		commentLoadingbar.setVisibility(View.GONE);
		commentContent.setVisibility(View.VISIBLE);
	}
	
	/**
	 * 设置推荐的周边农家乐
	 * 
	 * @param moreAroundFarm 获取到的 moreAroundFarm对象
	 * @param farmPic 设置图片
	 * @param farmName 设置名字
	 */
	private void setecommendAroundFarm(MoreAroundFarm moreAroundFarm, ImageView farmPic, TextView farmName) {
		
		String picUrl = moreAroundFarm.getSummary().getPic();
        Bitmap bitmap = Tools.getBitmapFromUrl(picUrl);
        
        if(bitmap != null){
            farmPic.setImageBitmap(bitmap);
        }
        farmName.setText(moreAroundFarm.getSummary().getName());
        
        farmPic.setTag(moreAroundFarm.getFarmhome_id());
        farmPic.setOnClickListener(aroundFarm);
	}
	
	/**
	 * 景点周边没有农家乐
	 */
	private void showCanNotGetFarm() {
		this.findViewById(R.id.cannot_find_fram).setVisibility(View.VISIBLE);
		moreFramLoadingbar.setVisibility(View.GONE);
	}
	
	/**
	 * 获取周边农家乐失败时，页面如何显示
	 */
	private void showGetFarmError() {
		this.findViewById(R.id.find_around_farm_error).setVisibility(View.VISIBLE);
		moreFramLoadingbar.setVisibility(View.GONE);
	}
	
	/**
	 * 获取点评失败时，页面如何显示
	 */
	private void showGetCommentError() {
		this.findViewById(R.id.find_comment_error).setVisibility(View.VISIBLE);
		commentLoadingbar.setVisibility(View.GONE);
	}
	
    private void setPic(Bitmap pic) {
        ImageView ivPic = (ImageView) this
                .findViewById(R.id.ImageView_destination_pic);
        ivPic.setImageBitmap(pic);
        ivPic.invalidate();
    }

	/**
	 * 监听到事件后的动作；
	 */
	private void prepareListeners() {
		
		// 商家图片
		showDetailPic = new OnClickListener() {
			public void onClick(View v) {
				if(destination.getPics() == null || destination.getPics().length <=0) {
					Tools.showToastLong(DetailActivity.this, Constants.NO_DETAIL_PICTURE);
					return;
				}
				Intent intent=new Intent();
				intent.setClass(DetailActivity.this, DetailGridPicActivity.class);
				//传递商家详细信息
				intent.putExtra(Constants.DETAIL_PICTURE_SHOW, destination.getPics());
		        
				startActivity(intent);
			}
		};
		
        //在地图上显示单个景点的位置
        showOnMap = new OnClickListener(){
            public void onClick(View v) {
            	// 判断网络是否可用，若不可用则提示用户，并不进行跳转
            	if(!Tools.checkNetworkStatus(DetailActivity.this)) {
            		return;
            	}
            	//将要显示的内容传到地图页面
                Intent toMapIntent = new Intent(DetailActivity.this, DetailOnMapActivity.class);
                toMapIntent.putExtra(Constants.MAP_SHOW_SPOT, destination);
                startActivity(toMapIntent);
            } 
        };
        
        //重试获取周边农家乐
        retryGetAroundFarm = new OnClickListener(){
            public void onClick(View v) {
            	// 判断网络是否可用，若不可用则提示用户，并不进行跳转
            	if(!Tools.checkNetworkStatus(DetailActivity.this)) {
            		return;
            	}
            	DetailActivity.this.findViewById(R.id.find_around_farm_error).setVisibility(View.GONE);
            	moreFramLoadingbar.setVisibility(View.VISIBLE);
            	//获取周边农家院
                new Thread(runnable4MoreAroundFarm).start();
            } 
        };

        //details
        aroundFarm = new OnClickListener(){
            public void onClick(View v) {
            	// 判断网络是否可用，若不可用则提示用户，并不进行跳转
            	if(!Tools.checkNetworkStatus(DetailActivity.this)) {
            		return;
            	}
                long destinationId =  (Long) v.getTag(); 
                Intent nearby2detail = new Intent(DetailActivity.this,DetailActivity.class);
                //将服务器返回数据保存入Intent，确保数据可在activity间传递
                nearby2detail.putExtra("destinationId", destinationId);
                //用于判断点击的是那种类型
                nearby2detail.putExtra("nearbyType", 2);
                
                startActivity(nearby2detail);
            } 
        };
        //更多农家乐
        getMoreFarm = new OnClickListener(){
            public void onClick(View v) {
            	// 判断网络是否可用，若不可用则提示用户，并不进行跳转
            	if(!Tools.checkNetworkStatus(DetailActivity.this)) {
            		return;
            	}
            	Intent toAroundFarmIntent = new Intent(DetailActivity.this, AroundFarmActivity.class);
            	toAroundFarmIntent.putExtra(Constants.AROUND_FARM, farmList);
                startActivity(toAroundFarmIntent);
            } 
        };
        
        //更多评论
        getMoreComment = new OnClickListener(){
            public void onClick(View v) {
            	// 判断网络是否可用，若不可用则提示用户，并不进行跳转
            	if(!Tools.checkNetworkStatus(DetailActivity.this)) {
            		return;
            	}
            	Intent toMoreCommentIntent = new Intent(DetailActivity.this, CommentShowActivity.class);
//            	toMoreCommentIntent.putExtra(Constants.MORE_COMMENT, commentList);
            	toMoreCommentIntent.putExtra(Constants.DESTINATION_ID, getIntent().getExtras().getLong("destinationId"));
            	toMoreCommentIntent.putExtra("destinationName", destination.getScenerySummary().getName());
            	toMoreCommentIntent.putExtra("nearbyType", detailType);
                startActivity(toMoreCommentIntent);
            } 
        };
        
        //重试获取评论
        retryGetComment = new OnClickListener(){
        	public void onClick(View v) {
        		// 判断网络是否可用，若不可用则提示用户，并不进行跳转
        		if(!Tools.checkNetworkStatus(DetailActivity.this)) {
        			return;
        		}
        		DetailActivity.this.findViewById(R.id.find_comment_error).setVisibility(View.GONE);
        		commentLoadingbar.setVisibility(View.VISIBLE);
        		//获取评论
        		new Thread(runnable4Comment).start();
        	} 
        };
        
        //写评论
        writeComment = new OnClickListener(){
            public void onClick(View v) {
            	// 判断网络是否可用，若不可用则提示用户，并不进行跳转
            	if(!Tools.checkNetworkStatus(DetailActivity.this)) {
            		return;
            	}
            	Intent toWriteCommentIntent = new Intent(DetailActivity.this, CommentWriteActivity.class);
            	toWriteCommentIntent.putExtra(Constants.DESTINATION_ID, getIntent().getExtras().getLong("destinationId"));
            	toWriteCommentIntent.putExtra("destinationName", destination.getScenerySummary().getName());
            	toWriteCommentIntent.putExtra("nearbyType", detailType);
            	toWriteCommentIntent.putExtra("fromActivity", "detail");
                startActivity(toWriteCommentIntent);
            } 
        };
		
		// 底部面板4个按钮间切换
		lPanelBottom = new OnClickListener() {
			public void onClick(View v) {
				//
				int tag = (Integer) v.getTag();
				switch (tag) {
				case 1:
				    destinationDbAdpter.addFavorite(destinationId);
				    //判断本地是否存储
				    Cursor cursor = destinationDbAdpter.getDestination(destinationId);
				    if(cursor.getCount() == 0 && destination != null){  
				        destinationDbAdpter.insertDestination(destination, false);
				    }
				    //添加成功
				    Tools.showToastShort(DetailActivity.this, Constants.ADD_FAVORITE);
					break;
				case 2:
					addToThisTrip();
					break;
				case 3:
					break;
				case 4:
                	// 判断网络是否可用，若不可用则提示用户，并不进行跳转
                	if(!Tools.checkNetworkStatus(DetailActivity.this)) {
                		return;
                	}
					//分享到新浪微博
                	//生成微博信息
            		if(destination != null) {
            			StringBuffer weiboText = new StringBuffer(Constants.WEIBO_TEXT_COMMON);
            			weiboText.append(destination.getScenerySummary().getName()).append("\r\n").
            			    append("地址: ").append(destination.getScenerySummary().getAddress()).append("\r\n").
            			    append("电话: ").append(destination.getScenerySummary().getTel());
            			OWeibo.sendToWeibo(DetailActivity.this,weiboText.toString(),new OWeiboMessageListener(DetailActivity.this));
            		}
                	
					//shearToSinaWeibo();
					break;
				}
			}
		};
	}
	
	/**
	 * 绑定view和监听
	 */
	private void batchSetListeners() {
		ImageView iback = (ImageView) this
				.findViewById(R.id.ImageView_button_back);
		iback.setOnClickListener(lback);

		//图片点击
        ImageView ivPic = (ImageView) this
                .findViewById(R.id.ImageView_destination_pic);
        ivPic.setOnClickListener(showDetailPic);
        
		//获取地图显示的一行
		LinearLayout showOnMapLayout = (LinearLayout) this
				.findViewById(R.id.LinearLayout_detail_map);
		showOnMapLayout.setOnClickListener(showOnMap);
		//获取重试获取周边农家乐
		Button retryFarmBtn = (Button) this.findViewById(R.id.retry_around_farm_btn);
		retryFarmBtn.setOnClickListener(retryGetAroundFarm);
		//获取更多周边农家乐
		RelativeLayout moreFarm = (RelativeLayout) this.findViewById(R.id.get_more_farm);
		moreFarm.setOnClickListener(getMoreFarm);
		//获取重试评论内容
		Button retryCommentBtn = (Button) this.findViewById(R.id.retry_comment_btn);
		retryCommentBtn.setOnClickListener(retryGetComment);
		
		//更多评论
		commentContent = (LinearLayout)findViewById(R.id.LinearLayout_comment);
		commentContent.setOnClickListener(getMoreComment);
		
		//书写评论按钮
        writeCommentImageView = (ImageView)findViewById(R.id.ImageView_write_comment);
        writeCommentImageView.setOnClickListener(writeComment);
		
		// 底部面板4个按钮
		TextView tvfavorites = (TextView) this
				.findViewById(R.id.TextView_panelBottom_detail_favorites);
		tvfavorites.setTag(1);
		tvfavorites.setOnClickListener(lPanelBottom);

		TextView tvcheckin = (TextView) this
				.findViewById(R.id.TextView_panelBottom_detail_addto_this_trip);
		tvcheckin.setTag(2);
		tvcheckin.setOnClickListener(lPanelBottom);

		TextView tvshare = (TextView) this
				.findViewById(R.id.TextView_panelBottom_detail_share);
		tvshare.setTag(4);
		tvshare.setOnClickListener(lPanelBottom);
	}
	
	Handler handler4pic = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String res = data.getString("value");
            Log.d("mylog","请求结果为-->" + res);
            setPic(pic);
        }
        
    };

    Runnable runnable4pic = new Runnable(){
        @Override
        public void run() {
            Log.d("a", "a");
            pic = Tools.getBitmapFromUrl(picUrl);
            Message msg = new Message();
            Bundle data = new Bundle();
            data.putString("value","ok");
            msg.setData(data);
            handler4pic.sendMessage(msg);
        }
        
    };
	
	Handler handler4loadpic = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String res = data.getString("value");
            Log.i("mylog","请求结果为-->" + res);
        }
    };

    Runnable runnable4loadpic = new Runnable(){
        @Override
        public void run() {
            Log.d("a", "a");
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
	 * 分享到新浪微博
	 */
	private void shearToSinaWeibo() {
		//令牌还没有过期，可以继续使用
		if(DetailActivity.accessToken.isSessionValid()){
			// 直接去发微博
			toSharePage();
		} else {//重新获得令牌
			mWeibo.authorize(DetailActivity.this, new AuthDialogListener());
		}
		
	}
	
	/**
	 * 添加到本次行程
	 */
	private void addToThisTrip() {
		//判断是否已经添加到了本次行程中
		Cursor mCursor = destinationDbAdpter.getTrisTripWithId(destinationId);
		if(mCursor != null && mCursor.getCount() > 0) {
			//添加成功
		    Tools.showToastShort(DetailActivity.this, Constants.ADD_THIS_TRIP);
		    return;
		}
		
		int maxSortValue = destinationDbAdpter.getMaxSortThisTrip();
		
		destinationDbAdpter.addThisTrip(destinationId, maxSortValue + 1);
	    //判断本地是否存储，如果没有，就将该记录添加到本地
	    Cursor cursor = destinationDbAdpter.getDestination(destinationId);
	    if(cursor.getCount() == 0 && destination != null){  
	        destinationDbAdpter.insertDestination(destination, false);
	    }
	    //添加成功
	    Tools.showToastShort(DetailActivity.this, Constants.ADD_THIS_TRIP);
	}
	
	/**
	 * 签到到新浪微博
	 */
	private void checkInToSinaWeibo() {
//		mWeibo.authorize(DetailActivity.this, new AuthDialogListener());
		//令牌还没有过期，可以继续使用
		if(DetailActivity.accessToken.isSessionValid()){
			// 直接去发微博
			StatusesAPI api = new StatusesAPI(DetailActivity.accessToken);
			if (!TextUtils.isEmpty(DetailActivity.EXTRA_ACCESS_TOKEN)) {
				String mContent = "@郊游客微博 我现在" + destination.getScenerySummary().getAddress();
				
				// Just update a text weibo!
				api.update(mContent, String.valueOf(destination.getScenerySummary().getLat()), 
						String.valueOf(destination.getScenerySummary().getLng()), this);
			} else {
				Toast.makeText(this, this.getString(R.string.weibosdk_please_login),
						Toast.LENGTH_LONG).show();
			}
		} else {//重新获得令牌
			mWeibo.authorize(DetailActivity.this, new AuthDialogListener());
		}
	}
    
	class AuthDialogListener implements WeiboAuthListener {

		@Override
		public void onComplete(Bundle values) {
			String token = values.getString("access_token");
			String expires_in = values.getString("expires_in");
			//重新加载token
			DetailActivity.accessToken = new Oauth2AccessToken(token, expires_in);
			if (DetailActivity.accessToken.isSessionValid()) {
				String date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new java.util.Date(DetailActivity.accessToken.getExpiresTime()));
				Log.d(TAG, "Token到期时间：" + date);
				AccessTokenKeeper.keepAccessToken(DetailActivity.this, accessToken);
				Toast.makeText(DetailActivity.this, "认证成功", Toast.LENGTH_SHORT).show();
				//跳转到分享页面
				toSharePage();
			} else {
				Toast.makeText(DetailActivity.this, "新浪微博认证失败", Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		public void onError(WeiboDialogError e) {
			Toast.makeText(getApplicationContext(), "Auth error : " + e.getMessage(),
					Toast.LENGTH_LONG).show();
		}

		@Override
		public void onCancel() {
			Toast.makeText(getApplicationContext(), "Auth cancel", Toast.LENGTH_LONG).show();
		}

		@Override
		public void onWeiboException(WeiboException e) {
			Toast.makeText(getApplicationContext(), "Auth exception : " + e.getMessage(),
					Toast.LENGTH_LONG).show();
		}

	}
	
	/**
	 * 跳转到分享页面
	 */
	private void toSharePage() {
		Intent intent=new Intent();
		intent.setClass(DetailActivity.this, DetailShareActivity.class);
		intent.putExtra(DetailActivity.EXTRA_ACCESS_TOKEN, DetailActivity.accessToken.getToken());
		intent.putExtra(DetailActivity.EXTRA_EXPIRES_IN, DetailActivity.accessToken.getExpiresTime());
        
		//生成微博信息
		if(destination != null) {
			StringBuffer weiboText = new StringBuffer(Constants.WEIBO_TEXT_COMMON);
			weiboText.append(destination.getScenerySummary().getName()).append("\r\n").
			    append("地址: ").append(destination.getScenerySummary().getAddress()).append("\r\n").
			    append("电话: ").append(destination.getScenerySummary().getTel());
			intent.putExtra(DetailActivity.WEIBO_TEXT, weiboText.toString());
			
			intent.putExtra(Constants.LOCATION_LNG, destination.getScenerySummary().getLat());//纬度
			intent.putExtra(Constants.LOCATION_LAT, destination.getScenerySummary().getLng());//经度
		}
		
		startActivity(intent);
	}
	
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

	@Override
	public void onComplete(String arg0) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(DetailActivity.this, R.string.weibosdk_checkin_sucess, Toast.LENGTH_LONG)
						.show();
			}
		});
	}

	@Override
	public void onError(final WeiboException e) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				try {
					Gson gson = new Gson();
					SinaWeiboErrorMsg errmsg = gson.fromJson(e.getMessage(),
							new TypeToken<SinaWeiboErrorMsg>() {}.getType());
					//连续发相同的微博，这里处理成发布成功
					if(errmsg.getError_code() == 20019) {
						Tools.showToastShort(DetailActivity.this, 
								DetailActivity.this.getString(R.string.weibosdk_checkin_sucess) );
					} else {
						Tools.showToastShort(DetailActivity.this, DetailActivity.this.getString(R.string.weibosdk_checkin_failed));
						Log.e(TAG, e.getMessage());
					}
				} catch (JsonSyntaxException e) {
	                Tools.showToastShort(DetailActivity.this, DetailActivity.this.getString(R.string.weibosdk_checkin_failed));
	                Log.e(TAG, e.getMessage());
				}
			}
		});
	}

	@Override
	public void onIOException(IOException arg0) {
		// TODO Auto-generated method stub
		
	}
}
