package com.f5.ourfarm.activity;

import static com.f5.ourfarm.util.URLConstants.COMMON_SEARCH_URL;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.baidu.location.LocationClient;
import com.f5.ourfarm.R;
import com.f5.ourfarm.layout.PullToRefreshView;
import com.f5.ourfarm.layout.PullToRefreshView.OnFooterRefreshListener;
import com.f5.ourfarm.model.LoadWay;
import com.f5.ourfarm.model.Summary;
import com.f5.ourfarm.util.Constants;
import com.f5.ourfarm.util.HttpUtil;
import com.f5.ourfarm.util.OurfarmApp;
import com.f5.ourfarm.util.Tools;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.umeng.analytics.MobclickAgent;


/**
 * 普通搜索功能
 * 
 * @author lfy
 */
public class SearchActivity extends Activity implements OnFooterRefreshListener {
    
	//后退
    OnClickListener lback = null;
    //搜索
    OnClickListener lSearch = null;
    //清空搜索框内容
    OnClickListener lClearButton = null;
    //查看明细
    OnClickListener details = null;
    //搜索内容输入框
    private EditText searchValue = null;
    //搜索清空按钮
    private Button clearButton = null;
    
    public static final String TAG = "SearchActivity";
    public static final String ERRMSG = "搜索时发生出错";
    
    //本地存图片信息
    HashMap<Long, Bitmap> pics = new HashMap<Long ,Bitmap>();
    
    //用户地理位置信息
    private double lat;
    private double lng;
    //用于存放定位信息
  	private Map<String, Object> localMap = new HashMap<String, Object>();
    //获取当前位置
    private LocationClient mLocClient;
    
    //记录第几次请求，每请求一次，返回10条记录
    private int queryCounts = 1;
    //上拉刷新
    PullToRefreshView mPullToRefreshView;
    
    //loadingbar
    private LinearLayout loadingLayout;
    //概要信息
    private LinearLayout summaryLayout;
    //没有搜索结果提示
    private LinearLayout searchNotFind;
    //搜索内容
    private String value;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 去除标题栏
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_search);
        //loadingbar
        loadingLayout = (LinearLayout)findViewById(R.id.common_search_loadingbar);
        //概要信息
        summaryLayout = (LinearLayout)findViewById(R.id.common_search_result);
        //搜索结果提示
        searchNotFind = (LinearLayout)findViewById(R.id.common_search_not_find);
        
        //初始化，表示请求是第一次
        queryCounts = 1;
        mPullToRefreshView = (PullToRefreshView) findViewById(R.id.main_pull_refresh_view);
        //不可以上拉刷新
        mPullToRefreshView.lock();
        
        //获得当前位置
        initLocalAddress();
        
        //准备listeners
        this.prepareListeners();
        
        //当用户按下键盘上的按键时，将会自动激活搜索框。
        setDefaultKeyMode(DEFAULT_KEYS_SEARCH_LOCAL);
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
     * 初始化当期位置
     */
    private void initLocalAddress() {
        //获得当前位置
        this.localMap = ((OurfarmApp)getApplication()).localMap;
        
        try{
        	lat = (Double)localMap.get(Constants.LOC_LAT);
        	lng = (Double)localMap.get(Constants.LOC_LNG);
        } catch(Exception e) {
        	Log.e(TAG, "获取当前位置失败，正进行下一次获取...");
        	// 失败后再进行一次位置获取
        	initLocation();
        }
    }
    
    /**
     * 初始化当前位置
     * 
     * @return
     */
    private void initLocation() {
    	mLocClient = ((OurfarmApp)getApplication()).mLocationClient;
    	((OurfarmApp)getApplication()).localMap = this.localMap;

    	//设置定位相关参数
    	mLocClient.setLocOption(Tools.getLocationOption());
    	
        if (mLocClient != null && mLocClient.isStarted()){
        	mLocClient.requestLocation();
        } else {
        	Log.d("LocSDK3", "locClient is null or not started");
        	mLocClient.start();
        	mLocClient.requestLocation();
        }
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
        //搜索按钮
        lSearch = new OnClickListener() {
            public void onClick(View v) {
            	//按下搜索按钮，认为第一次加载
            	queryCounts = 1;
            	//不可以上拉刷新
                mPullToRefreshView.lock();
            	//清除原来的搜索结果
            	LinearLayout list = (LinearLayout) SearchActivity.this.findViewById(R.id.common_search_result);
                list.removeAllViews();
                
            	value = searchValue.getText().toString();
            	//不输入时直接返回
            	if(value == null || value.trim().equals("")) {
            		return;
            	}
                value = value.trim();
                
                try{
                	lat = (Double)localMap.get(Constants.LOC_LAT);
                	lng = (Double)localMap.get(Constants.LOC_LNG);
                } catch(Exception e) {
                	Log.e(TAG, "获取当前位置失败");
                }
                //设置显示情况
                loadingLayout.setVisibility(View.VISIBLE);
                summaryLayout.setVisibility(View.GONE);
                searchNotFind.setVisibility(View.GONE);
                //情况原来的搜索结果
                pics.clear();
            	//搜索查询
                new Thread(runnable4search).start();
            }
        };
        //清除搜索框内容
        lClearButton = new OnClickListener() {
        	@Override
        	public void onClick(View v) {
        	    searchValue.setText("");
        	}
        };
        
        //details
        details = new OnClickListener(){
            public void onClick(View v) {
                long destinationId =  (Long) v.getTag(); 
                Log.i("res", ""+destinationId);
                Intent nearby2detail = new Intent(SearchActivity.this,DetailActivity.class);
                //将服务器返回数据保存入Intent，确保数据可在activity间传递
                nearby2detail.putExtra("destinationId", destinationId);
                nearby2detail.putExtra("nearbyType", (Integer)v.getTag(R.string.tag_first));//景点类型
                startActivity(nearby2detail);
            }
        };
       
    }
    
    /**
     * 绑定view和监听
     */
    private void batchSetListeners() {
    	//后退
        ImageView iback2home = (ImageView) this.findViewById(R.id.ImageView_button_back);
        iback2home.setOnClickListener(lback);
        //搜索
        Button commonSearch = (Button) this.findViewById(R.id.common_search_button);
        commonSearch.setOnClickListener(lSearch);
        
        //搜索框
        searchValue = (EditText)findViewById(R.id.common_search_input);
        searchValue.addTextChangedListener(mTextWatcher);
        //清除按钮
        clearButton = (Button)findViewById(R.id.common_search_button_clear);
        clearButton.setOnClickListener(lClearButton);
        
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
    		Tools.showToastLong(SearchActivity.this, "没有更多的景点了。");
    		mPullToRefreshView.onFooterRefreshComplete();
    	} else {
    		//点击搜索按钮，没有查询出数据，进行提示
    		searchNotFind.setVisibility(View.VISIBLE);
    	}
    }
    
    /**
     * 构造查询列表
     * 
     * 根据查询参数查找内容，然后将内容概要信息显示出来。
     */
    private void creatListView(String res, int loadWay) {
    	 //进度条消失
        if (loadingLayout != null) {
            loadingLayout.setVisibility(View.GONE);
        }
        
        //显示内容
        List<Summary> lds = null;
        try{
            Gson gson = new Gson();
            lds = gson.fromJson(res, new TypeToken<List<Summary>>() {}.getType());
        } catch(Exception e) {
        	loadNoData(loadWay);
            Log.e(TAG, "搜索时,概要信息json转换对象失败");
            return;
        }
        
        if(lds == null || lds.size() == 0){
        	loadNoData(loadWay);
        	return;
        } else{
        	summaryLayout.setVisibility(View.VISIBLE);
        	//隐藏软键盘
			((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
					.hideSoftInputFromWindow(SearchActivity.this.getCurrentFocus().getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);
        }
    	
        LayoutInflater flater = LayoutInflater.from(this);
        LinearLayout list = (LinearLayout) this.findViewById(R.id.common_search_result);
        
        // 获取查询结果，循环构造概要列表
        for(Summary ds: lds){
            View v = flater.inflate(R.layout.listview_child_nearby, null);
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
            // distance
            TextView tvDistance = (TextView) v
                    .findViewById(R.id.ListView_destination_distance);
            tvDistance.setText(Tools.getDistanceFormat(ds.getLng(), ds.getLat(), lng, lat));
            // add view
            list.addView(v);
            // 设置监听
            long destinationId = ds.getDestinationId();
            v.setTag(destinationId);
            v.setTag(R.string.tag_first, ds.getType());
            v.setOnClickListener(details);
        
        }
        list.invalidate();
        //上拉完成加载
        mPullToRefreshView.onFooterRefreshComplete();
        //可以上拉刷新
        mPullToRefreshView.unlock();
    }
    
    Runnable runnable4search = new RunnableImp(LoadWay.INIT_LOAD);
    
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
        	String res = null;
            try {
                res = HttpUtil.postUrl(COMMON_SEARCH_URL, getParameters());
            } catch (ClientProtocolException e1) {
                Log.e(TAG, ERRMSG, e1);
            } catch (IOException e1) {
                Log.e(TAG, ERRMSG, e1);
            } catch (Exception e) {
            	Log.e(TAG, ERRMSG, e);
            }
        
	        Message msg = new Message();
	        Bundle data = new Bundle();
	        data.putString("value",res);
	        //传输加载类型
            data.putInt("loadway", loadWay.ordinal());
	        msg.setData(data);
	        handler4Search.sendMessage(msg);
        }
    }
    
    Handler handler4Search = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String res = data.getString("value");
            Log.i(TAG, "请求结果为-->" + res);
            
            creatListView(res, data.getInt("loadway"));
        }
    };

    /**
     * 请求景点列表时，传入的参数
     * 
     * @param defaultDistance 搜索的距离
     * @return 包含参数对象的list
     */
    private List<NameValuePair> getParameters() {
        List<NameValuePair> param = new ArrayList<NameValuePair>();
        
        // 获取用户当前地址 lat和lng
        param.add(new BasicNameValuePair("lat", String.valueOf(this.lat)));
        param.add(new BasicNameValuePair("lng", String.valueOf(this.lng)));
        //搜索内容
        BasicNameValuePair searchValue = new BasicNameValuePair("name", value);
        param.add(searchValue);
        //请求加载的次数
        BasicNameValuePair count = new BasicNameValuePair("count", String.valueOf(queryCounts));
        param.add(count);
        
        return param;
    }
    
    /**
     * 检测搜索输入框中是否输入文字，来判断右边的清除按钮是否显示
     */
	TextWatcher mTextWatcher = new TextWatcher() {
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			// TODO Auto-generated method stub
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			// TODO Auto-generated method stub
		}

		@Override
		public void afterTextChanged(Editable s) {
			if (searchValue.getText().toString() != null && !searchValue.getText().toString().equals("")) {
				clearButton.setVisibility(View.VISIBLE);
			} else {
				clearButton.setVisibility(View.INVISIBLE);
			}
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
