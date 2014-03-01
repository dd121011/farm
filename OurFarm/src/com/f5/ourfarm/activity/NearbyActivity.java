package com.f5.ourfarm.activity;

import static com.f5.ourfarm.util.URLConstants.NEARBY_URL;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
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
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.baidu.location.LocationClient;
import com.f5.ourfarm.R;
import com.f5.ourfarm.layout.PullToRefreshView;
import com.f5.ourfarm.layout.PullToRefreshView.OnFooterRefreshListener;
import com.f5.ourfarm.model.Destination;
import com.f5.ourfarm.model.LoadWay;
import com.f5.ourfarm.model.Summary;
import com.f5.ourfarm.sqlite.DestinationDbAdpter;
import com.f5.ourfarm.util.Constants;
import com.f5.ourfarm.util.HttpUtil;
import com.f5.ourfarm.util.OurfarmApp;
import com.f5.ourfarm.util.Tools;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.umeng.analytics.MobclickAgent;

/**
 * @author tianhao
 *
 */
public class NearbyActivity extends Activity implements OnFooterRefreshListener{
    double scale = 111.31949079327;//1维度的距离差；
    /*计算经纬度步长*/
   // $half_distance =($distance)/($scale);
    OnClickListener lback = null;
    OnClickListener details = null;
    OnClickListener showMap = null;
    OnClickListener lrefresh = null;
    
    //loadingbar
    private LinearLayout loadingLayout;
    //概要信息
    private LinearLayout summaryLayout;
    //上方下拉框
    private RelativeLayout spinner;
    private Spinner spinnerDistance;
    private Spinner spinnerSort;
    //上拉刷新
    PullToRefreshView mPullToRefreshView;
    
    //自定义进度条
    ProgressBar progressBar;
    //显示当前位置
    TextView localAddress;
    
    //用户地理位置信息
    private double lat;
    private double lng;
    //存储选择的距离
    private int selectDistance = 1;
    //本地存图片信息
    HashMap<Long, Bitmap> pics = new HashMap<Long ,Bitmap>();
    //显示内容
    HashMap<Long, Summary> lds = new HashMap<Long ,Summary>();
   
    //sqlite 
    DestinationDbAdpter destinationDbAdpter;
    //TODO 默认距离是5km 100只是测试用
    private static String DEFAULE_DISTANCE = "100";
    //记录第几次请求，每请求一次，返回10条记录
    private int queryCounts = 1;
    
    //获取当前位置
    private LocationClient mLocClient;
    //用于存放定位信息
  	private Map<String, Object> localMap = new HashMap<String, Object>();
  	
  	private static String TAG = "附近页面";
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); 
        // 去除标题栏
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //准备listeners
        this.prepareListeners();
        setContentView(R.layout.activity_nearby);
        
        initStatus();
        //获得当前位置
        initLocalAddress();
        
        //设置listeners
        this.batchSetListeners();
        //设置spinner
        this.createSpinners();
        //打开数据库
        destinationDbAdpter = new DestinationDbAdpter(this);
        destinationDbAdpter.open();
        
        Log.d("再次加载", String.valueOf(lds.entrySet().size()));
         //先加载本地数据
        this.showLocalList();
        //TODO 还要本地的数据不？
//        new Thread(runnable4nearby).start();
    }
    
    /**
     * 初始页面时的控件状态设定
     */
    private void initStatus() {
    	localAddress = (TextView)this.findViewById(R.id.TextView_nearby_local_address);
        progressBar = (ProgressBar) findViewById(R.id.textprogressbar);
        mPullToRefreshView = (PullToRefreshView) findViewById(R.id.main_pull_refresh_view);
        //不能上拉页面
        mPullToRefreshView.lock();
        
        //loadingbar
        loadingLayout = (LinearLayout)findViewById(R.id.nearby_loadingbar);
        //概要信息
        summaryLayout = (LinearLayout)findViewById(R.id.ListView_main);
        spinner = (RelativeLayout)findViewById(R.id.spinner);
        
        spinnerDistance = (Spinner)findViewById(R.id.spinner_nearby_distance);
        spinnerSort = (Spinner)findViewById(R.id.spinner_nearby_sort);
        
        //设置显示情况
        loadingLayout.setVisibility(View.VISIBLE);
        summaryLayout.setVisibility(View.GONE);
        spinner.setVisibility(View.GONE);
        //初始化，表示请求是第一次
        queryCounts = 1;
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
     * 设置距离和排序是否可点击的状态
     * 
     * @param clickable 
     */
    private void setSpinnerClickable(boolean clickable) {
    	spinnerDistance.setClickable(clickable);
        spinnerSort.setClickable(clickable);
    }
    
    /**
     * 初始化当期位置
     */
    private void initLocalAddress() {
        localAddress.setVisibility(View.VISIBLE);
        //获得当前位置
        this.localMap = ((OurfarmApp)getApplication()).localMap;
        
        try{
        	lat = (Double)localMap.get(Constants.LOC_LAT);
        	lng = (Double)localMap.get(Constants.LOC_LNG);
        } catch(Exception e) {
			Log.d(TAG, Constants.REFERESH_FAILD_GET_LOC, e);
			localAddress.setText(Constants.REFERESH_FAILD_GET_LOC);
			return;
        }
        showLocalAddress();
    }
    
    /**
     * 显示当期位置
     */
    private void showLocalAddress() {
        //如果有地址就显示，否则显示经纬度
        if(localMap.get(Constants.LOC_ADDR) != null) {
        	localAddress.setText(localMap.get(Constants.LOC_ADDR).toString());
        } else {
        	localAddress.setText(lat + "  " + lng);
        }
    }

    /**
     * 先加载本地数据
     */
    private void showLocalList() {
        // 选择附近的类型：1：游玩 2：美食 3：住宿 4：特产
        Bundle extras = getIntent().getExtras();
        int naerbyType = extras.getInt("nearbyType");
        try {
            //根据param参数 ，选取本地数据
            Cursor cursor = destinationDbAdpter.getNearbyDestination(lat, lng, Double.valueOf(DEFAULE_DISTANCE), naerbyType);
            if (cursor != null && cursor.getCount() > 0) {
                Gson gson = new Gson();
                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                    String json = cursor.getString(1);
                    Destination destination = gson.fromJson(json, new TypeToken<Destination>() {}.getType());
                    Summary summary = destination.getScenerySummary();
                    double distance = Tools.getDistance(summary.getLng(), summary.getLat(), lng, lat);
                    summary.setDistance(distance);
                    if (distance < Double.valueOf(DEFAULE_DISTANCE) && summary.getType() == naerbyType) {
                        lds.put(summary.getDestinationId(), summary);
                        // 获取本地图片
                        Bitmap bitmap = destinationDbAdpter
                                .getHeadBitmap(summary.getDestinationId());
                        if (bitmap != null) {
                            pics.put(summary.getDestinationId(), bitmap);
                        }
                    }

                }
            }
            // 显示列表  
            // TODO  判断本地数据是否为null，若为null则取后台数据
            if(lds.size() == 0){
            	new Thread(runnable4nearby).start();
            } else {
            	//进度条消失
                if (loadingLayout != null) {
                     loadingLayout.setVisibility(View.GONE);
                }
                summaryLayout.setVisibility(View.VISIBLE);
                spinner.setVisibility(View.VISIBLE);
                mPullToRefreshView.unlock();
            	//只显示本地数据
            	this.showList(sortByHotspot(lds));
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
        //nearby->home
        lback = new OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        };
        //details
        details = new OnClickListener(){
            public void onClick(View v) {
            	// 判断网络是否可用，若不可用则提示用户，并不进行跳转
            	if(!Tools.checkNetworkStatus(NearbyActivity.this)) {
            		return;
            	}
                long destinationId =  (Long) v.getTag(); 
                Log.i("res", ""+destinationId);
                int nearbyType = getIntent().getExtras().getInt("nearbyType");
                //休闲山庄
                if(nearbyType == 3) {
                	Intent nearby2mountainVilla = new Intent(NearbyActivity.this,MountainVillaActivity.class);
                	//将服务器返回数据保存入Intent，确保数据可在activity间传递
                	nearby2mountainVilla.putExtra("destinationId", destinationId);
                	
                	startActivity(nearby2mountainVilla);
                } else {//其他3中类型
                	Intent nearby2detail = new Intent(NearbyActivity.this,DetailActivity.class);
                	//将服务器返回数据保存入Intent，确保数据可在activity间传递
                	nearby2detail.putExtra("destinationId", destinationId);
                	//用于判断点击的是那种类型
                	nearby2detail.putExtra("nearbyType", nearbyType);
                	
                	startActivity(nearby2detail);
                }
            } 
        };
        //map
        showMap = new OnClickListener(){
            public void onClick(View v) {
            	// 判断网络是否可用，若不可用则提示用户，并不进行跳转
            	if(!Tools.checkNetworkStatus(NearbyActivity.this)) {
            		return;
            	}
            	//将要显示的内容传到地图页面
                Intent toMapIntent = new Intent(NearbyActivity.this, NearbyMapActivity.class);
                toMapIntent.putExtra(Constants.MAP_SHOW_SPOT, lds);
                toMapIntent.putExtra(Constants.MAP_SHOW_DISTANCE, selectDistance);//选择的距离，用来判断地图级别
                startActivity(toMapIntent);
            } 
        };
        //refresh
        lrefresh = new OnClickListener(){
            public void onClick(View v) {
            	// 判断网络是否可用，若不可用则提示用户，并不进行跳转
            	if(!Tools.checkNetworkStatus(NearbyActivity.this)) {
            		return;
            	}
            	//上方园进度圈加载的时候,不刷新
            	if(loadingLayout.getVisibility() == View.VISIBLE
            		|| mPullToRefreshView.getLock() == false) {
            		return;
            	}
            	
            	queryCounts = 1;
            	
//            	Toast.makeText(NearbyActivity.this, "正在确认你的位置...", Toast.LENGTH_LONG).show();
                //初始化当前位置
        	    try {
        	    	retryGetLocation();
        		} catch (Exception e) {
        			Log.d(TAG, Constants.REFERESH_FAILD_GET_LOC, e);
//        			Tools.showToastLong(NearbyActivity.this, "Exception");
        			localAddress.setText(Constants.REFERESH_FAILD_GET_LOC);
        			return;
        		}
            	
            	//加载进度条
            	progressBar.setVisibility(View.VISIBLE);
            	progressBar.setProgress(0);
            	localAddress.setVisibility(View.GONE);
            	mPullToRefreshView.lock();
            	
                //加载后台
                new Thread(runnable4nearby).start();
            }
        };
    }
    
    /**
     * 多次尝试获取当期位置
     */
    private void retryGetLocation() {
    	//纬度值
    	Object locTypeCode = null;
    	//等待定位信息的获取,做10次位置请求
    	int waint = 0;
    	int waintCount = 10;
    	for(;waint < waintCount; waint++) {
    		initLocation();
    		locTypeCode = localMap.get(Constants.LOC_TYPE_CODE);
    		if(locTypeCode != null) {
    			break;
    		}
    	}
    	if(waint == waintCount) {
    		Log.d(TAG, "wait"); 
    		//没有获得新位置，页面不刷新
//    		cannotGetLocShow();
    		return;
    	} else {
    		//定位成功
    		if("61".equals(locTypeCode.toString()) ||
    				"68".equals(locTypeCode.toString()) || "161".equals(locTypeCode.toString())) {
    	        lat = (Double)localMap.get(Constants.LOC_LAT);
    	        lng = (Double)localMap.get(Constants.LOC_LNG);
    	        //清空LOC_TYPE_CODE，用来判断下一次位置请求
    	        localMap.put(Constants.LOC_TYPE_CODE, null);
    	        Log.d(TAG, "lat:" + lat + " lng:" + lng );
    	        //显示当期位置
    	        showLocalAddress();
    		} else {
    			Log.d(TAG, "get_loc_false"); 
    			localAddress.setText(Constants.REFERESH_FAILD_GET_LOC);
    			return;
    		}
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
     * 绑定view和监听
     */
    private void batchSetListeners() {
        // back
        ImageView iback2home = (ImageView) this.findViewById(R.id.ImageView_button_back);
        iback2home.setOnClickListener(lback);
        //map
        ImageView i2map = (ImageView) this.findViewById(R.id.ImageView_button_map);
        i2map.setOnClickListener(showMap);
        //refresh
        ImageView irefresh = (ImageView) this.findViewById(R.id.ImageView_destination_refresh);
        irefresh.setOnClickListener(lrefresh);
        
        mPullToRefreshView.setOnFooterRefreshListener(this);
    }
    
    /**
     * 创建距离和排序的下拉框
     */
    private void createSpinners() {
        //距离Spinner
        Spinner spinnerDistance = (Spinner) this.findViewById(R.id.spinner_nearby_distance);
        ArrayAdapter<CharSequence> adapterDistance = ArrayAdapter.createFromResource(this, R.array.spinner_nearby_distance,
                R.drawable.near_distance_hover);
        adapterDistance.setDropDownViewResource(R.drawable.near_distance_items);
        spinnerDistance.setAdapter(adapterDistance);
        spinnerDistance.setOnItemSelectedListener(new OnItemSelectedListenerImpl(true));
        spinnerDistance.setSelection(1);//默认选中5千米
        
        //排序Spinner
        Spinner spinnerSort = (Spinner) this.findViewById(R.id.spinner_nearby_sort);
        ArrayAdapter<CharSequence> adapterSort = ArrayAdapter.createFromResource(this, R.array.spinner_nearby_sort,
                R.drawable.near_distance_hover);
        adapterSort.setDropDownViewResource(R.drawable.near_distance_items);
        spinnerSort.setAdapter(adapterSort);
        spinnerSort.setOnItemSelectedListener(new OnItemSortListenerImpl(true));
        
    }
    
    /**
     * 选中距离下来框中触发的事件
     * 
     * @author lify
     *
     */
    private class OnItemSelectedListenerImpl implements OnItemSelectedListener {

        //判断是不是页面初始化时调用的
        private boolean initLoad;
        OnItemSelectedListenerImpl(boolean initLoad) {
            this.initLoad = initLoad;
        }
        /**
         * 当选择的内容改变时，要对查询结果重新排序
         */
        @Override
        public void onItemSelected(AdapterView<?> diatance, View view, int position,
                long id) {
        	queryCounts = 1;
            //在页面加载的时候，会触发该事件，如果执行new Thread。。。语句，会出现线程冲突
            if(!initLoad) {
            	// 判断网络是否可用，若不可用则提示用户，并不进行跳转
            	if(!Tools.checkNetworkStatus(NearbyActivity.this)) {
            		return;
            	}
                String diatanceStr = (String) diatance.getItemAtPosition(position);// 得到选中的选项
                Log.i("select spinel: ", diatanceStr);
                Log.i("spinel id: ", String.valueOf(id));
                selectDistance = getDistance(diatanceStr);
                /** 现在分为本地存储和网络加载，每次排序都需要重新从网络加载，没法判断是否被刷新过 */
            	//加载进度条
            	progressBar.setVisibility(View.VISIBLE);
            	progressBar.setProgress(0);
            	localAddress.setVisibility(View.GONE);
            	
            	mPullToRefreshView.lock();
                new Thread(new RunnableImp(String.valueOf(selectDistance), LoadWay.SPINNER_LOAD)).start();
                
            } else {
                initLoad = false;
                Log.d("loadOrder", "init load ");
            }
        }
        
        /**
         * 获取选择的距离
         * 
         * @return 选择的距离值
         */
        private int getDistance(String selectDistance) {
            //默认为1
            int distance = 1;
            try {
                distance = Integer.valueOf(selectDistance.substring(2, selectDistance.indexOf("k")));
            } catch (Exception e) {
                Log.e("select distance", "选择的距离转换成数字错误", e);
            }
            return distance;
        }

        /**
         * 什么也不选时的处理
         */
        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub
            Log.d("selectSpinne", "没有选择距离");
        }

    }
    
    /**
     * 选中右边排序下来框中触发的事件，现在包括热度、价格
     * 
     * @author lify
     *
     */
    private class OnItemSortListenerImpl implements OnItemSelectedListener {

        //判断是不是页面初始化时调用的
        private boolean initLoad;
        OnItemSortListenerImpl(boolean initLoad) {
            this.initLoad = initLoad;
        }
        /**
         * 当选择的内容改变时，要对查询结果重新排序
         */
        @Override
        public void onItemSelected(AdapterView<?> diatance, View view, int position,
                long id) {
            //在页面加载的时候，会出发该事件，如果执行new Thread。。。语句，会出现线程冲突
            if(!initLoad) {
            	List<Map.Entry<Long, Summary>> list = new ArrayList<Map.Entry<Long, Summary>>();
                String diatanceStr = (String) diatance.getItemAtPosition(position);// 得到选中的选项
                if(diatanceStr.contains("热度")) {
                	list = sortByHotspot(lds);
                } else if(diatanceStr.contains("价格")) {
                	list = sortByPrice(lds);
                }
                showList(list);         
            } else {//初始加载，默认为按照热度排序
                initLoad = false;
            }
        }
        
        /**
         * 什么也不选时的处理
         */
        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub
            Log.d("selectSpinne", "没有选择距离");
        }

    }
    
    /**
     * 按照热度排序
     */
    private List<Map.Entry<Long, Summary>> sortByHotspot(HashMap<Long, Summary> sortMap) {
    	List<Map.Entry<Long, Summary>> infoIds = new ArrayList<Map.Entry<Long, Summary>>(sortMap.entrySet());
		
		Collections.sort(infoIds, new Comparator<Map.Entry<Long, Summary>>() {
			public int compare(Map.Entry<Long, Summary> arg0,
					Map.Entry<Long, Summary> arg1) {
				return arg0.getValue().getHot() > arg1.getValue().getHot() ? -1 : 1;
			}
		});
		
		return infoIds;
    }
    
    /**
     * 按照价格排序
     */
    private List<Map.Entry<Long, Summary>> sortByPrice(HashMap<Long, Summary> sortMap) {
    	List<Map.Entry<Long, Summary>> infoIds = new ArrayList<Map.Entry<Long, Summary>>(sortMap.entrySet());
    	
		Collections.sort(infoIds, new Comparator<Map.Entry<Long, Summary>>() {
			public int compare(Map.Entry<Long, Summary> arg0,
					Map.Entry<Long, Summary> arg1) {
				return arg0.getValue().getPrice() < arg1.getValue().getPrice() ? -1 : 1;
			}
		});
		
		return infoIds;
    }
    
    /**
     * 构造附近列表
     * 根据查询参数查找附近内容，然后将内容概要信息显示出来。
     */
    private void showList(List<Map.Entry<Long, Summary>> lds) {
        LayoutInflater flater = LayoutInflater.from(this);
        LinearLayout list = (LinearLayout) this
                .findViewById(R.id.ListView_main);
        list.removeAllViews();
        
        // 获取查询结果，循环构造概要列表
        for(Entry<Long, Summary> entry: lds){
            Long  destinationId = entry.getKey();
            Summary ds = entry.getValue();
            View v = flater.inflate(R.layout.listview_child_nearby, null);
//            // pic
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
            tvDistance.setText(Tools.getDistanceFormat(ds.getDistance()));
            // add view
            list.addView(v);
            // 设置监听
            v.setTag(destinationId);
            v.setOnClickListener(details);
        
        }
        //
        list.invalidate();
    }

    /**
     * 将附件概要信息json转换成对象,然后加入本地列表
     * 
     * @param res 概要信息json串
     * @return 新加载的个数
     */
    private int addSummarys(String res) {
        try {
            Gson gson = new Gson();
            List<Summary> listSummary =  gson.fromJson(res, 
            		new TypeToken<List<Summary>>() {}.getType());
            Iterator<Summary> iterator = listSummary.iterator();
            while(iterator.hasNext()) {
                Summary summary = iterator.next();
                lds.put(summary.getDestinationId(),summary);
            }
            return listSummary.size();
        } catch(Exception e) {
            Log.e(TAG, "概要信息json转换对象失败");
        }
        return 0;
    }
    
    Handler handler4nearby = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String res = data.getString("value");
            Log.i("mylog","请求结果为-->" + res);
            
            //横向进度条消失
            if (progressBar != null) {
            	progressBar.setVisibility(View.GONE);
            	localAddress.setVisibility(View.VISIBLE);
            }
            TextView tvTop = (TextView) NearbyActivity.this.findViewById(R.id.TextView_toppanel_title);
            //上方圆圈进度条消失
            if (loadingLayout != null) {
                 loadingLayout.setVisibility(View.GONE);
            }
            
            if(lds.size() == 0){
            	// 如果后台数据也为空，则提示用户更改距离属性
                tvTop.setText("未找到相关信息");
            }else{
            	summaryLayout.setVisibility(View.VISIBLE);
            	//后台线程完成后更新显示结果
                showList(sortByHotspot(lds));
                tvTop.setText(R.string.title_activity_nearby);
                //上拉完成加载
                mPullToRefreshView.onFooterRefreshComplete();
                ++queryCounts;
            }
            
            //可以上拉刷新
            mPullToRefreshView.unlock();
            spinner.setVisibility(View.VISIBLE);
            setSpinnerClickable(true);
        }
    };
    
    Runnable runnable4nearby = new RunnableImp(DEFAULE_DISTANCE, LoadWay.INIT_LOAD);
    /**
     * Runnable实现类，用来请求景点list
     */
    class RunnableImp implements Runnable {
    	//选择的距离
        String distance;
        //数据加载方式：1：初始加载, 2：选择spinner方式, 3：选择上拉加载方式
        LoadWay loadWay;
        RunnableImp(String distance, LoadWay loadWay) {
            this.distance = distance;
            this.loadWay = loadWay;
        }
        
        @Override
        public void run() {
        	Message msg = new Message();
            Bundle data = new Bundle();
            data.putString("value","ok");
            msg.setData(data);
            
            String Tag = "NearbyService";
            String errMsg = "读取景点概要信息出错";
            String res = null;
            try {
                res = HttpUtil.postUrl(NEARBY_URL, getParameters(distance));
                //上拉加载时没有数据，进行提示
                if(addSummarys(res) <= 0 && loadWay == LoadWay.POLLUP_LOAD) {
                	Tools.showToastLong(NearbyActivity.this, "没有更多的景点了。");
                	handler4nearby.sendMessage(msg);
                	return;
                }
                
                //计算进度
                int loadCount = 0;
                if(lds.size() <= 0) {//没有数据时，直接加载到100%
                	progressBar.setProgress(100);
                }
                for(Entry<Long, Summary> entry: lds.entrySet() ){
                    Long  destinationId = entry.getKey();
                    Summary summary = entry.getValue();
                    String picUrl = summary.getPic();
                    Bitmap bitmap = Tools.getBitmapFromUrl(picUrl);
                    pics.put(destinationId, bitmap);
                    //按照百分比显示进度
                    progressBar.setProgress(Tools.getProcessValue(++loadCount, lds.size()));
                }
            } catch (ClientProtocolException e1) {
                Log.e(Tag, errMsg, e1);
            } catch (IOException e1) {
                Log.e(Tag, errMsg, e1);
            } catch (Exception e1) {
            	Log.e(Tag, errMsg, e1);
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
    private List<NameValuePair> getParameters(String defaultDistance) {
        List<NameValuePair> param = new ArrayList<NameValuePair>();
        //选择附近的类型：1：景点 2：农家乐 3：山庄 4：农产品
        Bundle extras = getIntent().getExtras();
        int naerbyType = extras.getInt("nearbyType");
        int typeValue = extras.getInt("typeValue");
        
        // 获取用户当前地址 lat和lng
        param.add(new BasicNameValuePair("lat", String.valueOf(this.lat)));
        param.add(new BasicNameValuePair("lng", String.valueOf(this.lng)));
        //附近距离
        BasicNameValuePair distance = new BasicNameValuePair("distance", defaultDistance);
        param.add(distance);
        //第几次请求
        BasicNameValuePair count = new BasicNameValuePair("count", String.valueOf(queryCounts));
        param.add(count);
        //选择附近的类型
        BasicNameValuePair type = new BasicNameValuePair("type", String.valueOf(naerbyType));
        param.add(type);
        //选择具体的类别
        BasicNameValuePair value = new BasicNameValuePair("type_value", String.valueOf(typeValue));
        param.add(value);
        
        return param;
    }

    /**
     * 上拉刷新处理
     */
	@Override
	public void onFooterRefresh(PullToRefreshView view) {
		Log.d("请求次数", queryCounts + "");
		setSpinnerClickable(false);
		new Thread(new RunnableImp(String.valueOf(selectDistance), LoadWay.POLLUP_LOAD)).start();
	}
	
}
