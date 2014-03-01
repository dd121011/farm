package com.f5.ourfarm.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.ItemizedOverlay;
import com.baidu.mapapi.LocationListener;
import com.baidu.mapapi.MapActivity;
import com.baidu.mapapi.MapController;
import com.baidu.mapapi.MapView;
import com.baidu.mapapi.MyLocationOverlay;
import com.baidu.mapapi.OverlayItem;
import com.baidu.mapapi.Projection;
import com.f5.ourfarm.R;
import com.f5.ourfarm.model.Summary;
import com.f5.ourfarm.util.Constants;
import com.f5.ourfarm.util.OurfarmApp;

/**
 * 通过地图查看附近的景点
 * 
 * @author zb
 *
 */
public class NearbyMapActivity extends MapActivity {
	
	//后退按钮
	OnClickListener lback = null;
	
	// 点击定位时弹出的气泡View
	static View mapSpotPopView = null;	
	
	static MapView mMapView;
	
	LocationListener mLocationListener = null;//onResume时注册此listener，onPause时需要Remove
	MyLocationOverlay mLocationOverlay = null;//定位图层
	
	OverItemT overitem = null;
	
    //存储选择的距离
    private int selectDistance;
    //显示内容
    private HashMap<Long, Summary> showSpotsMap = new HashMap<Long ,Summary>();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 去除标题栏
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.activity_nearby_map);
		
		//获得景点信息
        Intent in = this.getIntent();
		selectDistance = in.getIntExtra(Constants.MAP_SHOW_DISTANCE, 1);
		showSpotsMap = (HashMap<Long, Summary>)in.getSerializableExtra(Constants.MAP_SHOW_SPOT);	
		
		initMap();
		controlMap();
		showNearBySpot();
		
        //准备listeners
        this.prepareListeners();
		batchSetListeners();
		
	}
	
	/**
	 * 地图信息初始化
	 */
	private void initMap() {
		OurfarmApp app = (OurfarmApp)this.getApplication();
		//获取地图Manager
		if (app.mBMapMan == null) {
			app.mBMapMan = new BMapManager(getApplication());
			app.mBMapMan.init(app.mStrKey, new OurfarmApp.MyGeneralListener());
		}
		app.mBMapMan.start();
		
		super.initMapActivity(app.mBMapMan);

		mMapView = (MapView) findViewById(R.id.bmapsView);
		mMapView.setBuiltInZoomControls(true); // 设置启用内置的缩放控件
		//设置在缩放动画过程中也显示overlay,默认为不绘制
        mMapView.setDrawOverlayWhenZooming(true);
        
		// 添加定位图层
        mLocationOverlay = new MyLocationOverlay(this, mMapView);
		mMapView.getOverlays().add(mLocationOverlay);
	}
	
	/**
	 * 控制地图的绘制
	 */
	private void controlMap() {
		// 得到mMapView的控制权,可以用它控制和驱动平移和缩放
		MapController mMapController = mMapView.getController(); 
		//用于存放定位信息
	  	Map<String, Object> localMap = ((OurfarmApp)getApplication()).localMap;
	  	
		// 用给定的经纬度构造一个GeoPoint，单位是微度 (度 * 1E6)，取原来的定位点
//        GeoPoint point =new GeoPoint((int)(39.90923*1e6), (int)(116.397428*1e6));
        
        double lat = (Double)localMap.get(Constants.LOC_LAT);
        double lng = (Double)localMap.get(Constants.LOC_LNG);
        
        Log.d("to center lat and lng", String.valueOf(lat) + " " + String.valueOf(lng));
        
		GeoPoint point = new GeoPoint((int)(lat*1e6), (int)(lng*1e6)); 
		mMapController.setCenter(point); // 设置地图中心点
		// 根据距离来确定
		mMapController.setZoom(getZoomFromDistance()); // 设置地图zoom级别
	}
	
	/**
	 * 地图显示附近景点
	 */
	private void showNearBySpot() {
		//添加ItemizedOverlay
		Drawable marker = getResources().getDrawable(R.drawable.home_checkin_active);  //得到需要标在地图上的资源
		marker.setBounds(0, 0, marker.getIntrinsicWidth(), marker
				.getIntrinsicHeight());   //为maker定义位置和边界
		
		//得到需要标在地图上的资源
		overitem = new OverItemT(marker, this);
		mMapView.getOverlays().add(overitem); //添加ItemizedOverlay实例到mMapView
				
		//创建点击景点位置mark时的弹出泡泡
		mapSpotPopView = super.getLayoutInflater().inflate(R.layout.map_my_loc_popview, null);
		mMapView.addView( mapSpotPopView,
                new MapView.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
                		null, MapView.LayoutParams.TOP_LEFT));
		mapSpotPopView.setVisibility(View.GONE);
	}
	
	/**
	 * 获取地图缩放级别
	 * 
	 * @return 级别值,取值范围[3,18]
	 */
	private int getZoomFromDistance() {
		//根据选择的距离选择级别
		switch (selectDistance) {
			case 1:
				return 14;
			case 5:	
				return 13;
			case 10:	
				return 12;
			case 20:	
				return 11;
			case 50:	
				return 10;
			default: return 12;
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
     * 监听到事件后的动作；
     */
    private void prepareListeners() {
        // 后退按钮detail_grid_picture->detail
        lback = new OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        };
        
        // 注册定位事件
        mLocationListener = new LocationListener(){

			@Override
			public void onLocationChanged(Location location) {
				if (location != null){
					GeoPoint pt = new GeoPoint((int)(location.getLatitude()*1e6),
							(int)(location.getLongitude()*1e6));
					mMapView.getController().animateTo(pt);
				}
			}
        };
    }
    
    /**
     * 绑定view和监听
     */
    private void batchSetListeners() {
        ImageView iback2home = (ImageView) this.findViewById(R.id.ImageView_button_back);
        iback2home.setOnClickListener(lback);
    }
    
    /**
     * 显示具体景点的标记
     * 
     * @author lify
     *
     */
    class OverItemT extends ItemizedOverlay<OverlayItem> {
	    private List<OverlayItem> mGeoList = new ArrayList<OverlayItem>();
	    private Drawable marker;
	          
	    public OverItemT(Drawable marker, Context context) {
		    super(boundCenterBottom(marker));
		     
		    //按照热度进行排序
		    List<Map.Entry<Long, Summary>> sortByHotspot = sortByHotspot(showSpotsMap);
		    //TODO 默认只显示前10位，以后要做出分页的形式
		    int showCountInPage = sortByHotspot.size();
		    if(showCountInPage > 10){
		    	showCountInPage = 10;
		    }
		    for(int i = 0; i < showCountInPage; i++) {
		    	Summary sum = sortByHotspot.get(i).getValue();
		    	// 用给定的经纬度构造GeoPoint，单位是微度 (度 * 1E6)
		    	GeoPoint geoPoint = new GeoPoint((int) (sum.getLat() * 1E6), (int) (sum.getLng() * 1E6));
		    	// 存放景点名称和内容id
		    	mGeoList.add(new OverlayItem(geoPoint, sum.getName(), String.valueOf(sum.getDestinationId())));
		    }
		    //在一个新ItemizedOverlay上执行所有操作的工具方法。 
		    //子类通过createItem(int)方法提供item。一旦有了数据，子类在调用其它方法前，首先调用这个方法。
		    populate();
	    }
     
	    @Override
	    protected OverlayItem createItem(int i) {
	    	return mGeoList.get(i);
	    }
	     
	    @Override
	    public int size() {
	    	return mGeoList.size();
	    }
	    
		@Override
		public void draw(Canvas canvas, MapView mapView, boolean shadow) {

			// Projection接口用于屏幕像素坐标和经纬度坐标之间的变换
			Projection projection = mapView.getProjection(); 
			for (int index = size() - 1; index >= 0; index--) { // 遍历mGeoList
				OverlayItem overLayItem = getItem(index); // 得到给定索引的item

				String title = overLayItem.getTitle();
				// 把经纬度变换到相对于MapView左上角的屏幕像素坐标
				Point point = projection.toPixels(overLayItem.getPoint(), null); 

				// 可在此处添加您的绘制代码
				/*
				Paint paintText = new Paint();
				paintText.setColor(Color.BLUE);
				paintText.setTextSize(15);
				canvas.drawText(title, point.x-30, point.y, paintText); // 绘制文本
				*/
			}

			super.draw(canvas, mapView, shadow);
			//调整一个drawable边界，使得（0，0）是这个drawable底部最后一行中心的一个像素
			boundCenterBottom(marker);
		}
	     
	    @Override
	    // 处理当点击事件
	    protected boolean onTap(int i) {
	    	setFocus(mGeoList.get(i));
			// 更新气泡位置,并使之显示
			GeoPoint pt = mGeoList.get(i).getPoint();
			NearbyMapActivity.mMapView.updateViewLayout( NearbyMapActivity.mapSpotPopView,
	                new MapView.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
	                		pt, MapView.LayoutParams.BOTTOM_CENTER));
			//获取景点ID
			final long destinationId = Long.valueOf(mGeoList.get(i).getSnippet());
			//TODO 美化弹出气泡，根据destinationId去掉景点的具体信息
			
			NearbyMapActivity.mapSpotPopView.setVisibility(View.VISIBLE);
			//显示
			TextView textView = (TextView)NearbyMapActivity.this.findViewById(R.id.TextView_map_spot_loc);
			textView.setText(mGeoList.get(i).getTitle());
			
			//跳转到具体的明细
			textView.setOnClickListener(new OnClickListener() {
	            public void onClick(View v) {
	            	Intent nearby2detail = new Intent(NearbyMapActivity.this,DetailActivity.class);
	                //将服务器返回数据保存入Intent，确保数据可在activity间传递
	                nearby2detail.putExtra("destinationId", destinationId);
	                startActivity(nearby2detail);
	            }
	        });
			
		    return true;
		}
	    
		@Override
		public boolean onTap(GeoPoint arg0, MapView arg1) {
			// TODO Auto-generated method stub
			// 消去弹出的气泡
			NearbyMapActivity.mapSpotPopView.setVisibility(View.GONE);
			return super.onTap(arg0, arg1);
		}
    }
    
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//getMenuInflater().inflate(R.menu.activity_nearby_map, menu);
		return true;
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

//	@Override
//	protected void onDestroy() {
//		OurfarmApp app = (OurfarmApp)this.getApplication();
//		if (app.mBMapMan != null) {
//			app.mBMapMan.destroy();
//			app.mBMapMan = null;
//		}
//		super.onDestroy();
//	}

	@Override
	protected void onPause() {
		OurfarmApp app = (OurfarmApp)this.getApplication();
		if (app.mBMapMan != null) {
			app.mBMapMan.getLocationManager().removeUpdates(mLocationListener);
			mLocationOverlay.disableMyLocation();
	        mLocationOverlay.disableCompass(); // 关闭指南针
	        app.mBMapMan.stop();
		}
		super.onPause();
	}

	@Override
	protected void onResume() {
		OurfarmApp app = (OurfarmApp)this.getApplication();
		if (app.mBMapMan != null) {
			app.mBMapMan.getLocationManager().requestLocationUpdates(mLocationListener);
	        mLocationOverlay.enableMyLocation();
	        mLocationOverlay.enableCompass(); // 打开指南针
	        app.mBMapMan.start();
		}
		super.onResume();
	}
}
