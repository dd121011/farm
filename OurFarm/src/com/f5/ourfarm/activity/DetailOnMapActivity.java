package com.f5.ourfarm.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.ItemizedOverlay;
import com.baidu.mapapi.MKAddrInfo;
import com.baidu.mapapi.MKBusLineResult;
import com.baidu.mapapi.MKDrivingRouteResult;
import com.baidu.mapapi.MKPlanNode;
import com.baidu.mapapi.MKPoiResult;
import com.baidu.mapapi.MKSearch;
import com.baidu.mapapi.MKSearchListener;
import com.baidu.mapapi.MKSuggestionResult;
import com.baidu.mapapi.MKTransitRouteResult;
import com.baidu.mapapi.MKWalkingRouteResult;
import com.baidu.mapapi.MapActivity;
import com.baidu.mapapi.MapController;
import com.baidu.mapapi.MapView;
import com.baidu.mapapi.MyLocationOverlay;
import com.baidu.mapapi.OverlayItem;
import com.baidu.mapapi.RouteOverlay;
import com.baidu.mapapi.TransitOverlay;
import com.f5.ourfarm.R;
import com.f5.ourfarm.model.Destination;
import com.f5.ourfarm.model.Summary;
import com.f5.ourfarm.util.Constants;
import com.f5.ourfarm.util.OurfarmApp;
import com.f5.ourfarm.util.Tools;
import com.f5.ourfarm.widget.QuickAction;
import com.f5.ourfarm.widget.QuickActionBar;
import com.f5.ourfarm.widget.QuickActionWidget;
import com.f5.ourfarm.widget.QuickActionWidget.OnQuickActionClickListener;

/**
 * 通过地图查看当前景点
 * 
 * @author lfy
 *
 */
public class DetailOnMapActivity extends MapActivity {
	
	//后退按钮
	OnClickListener lback = null;
	//线路按钮
	OnClickListener lineBtn = null;
	
	// 点击定位时弹出的气泡View
	static View mapSpotPopView = null;	
	
	static MapView mMapView;
	MKSearch mSearch = null;	// 搜索模块，也可去掉地图模块独立使用
	
//	LocationListener mLocationListener = null;//onResume时注册此listener，onPause时需要Remove
	MyLocationOverlay mLocationOverlay = null;//定位图层
	
	OverItemT overitem = null;
	
    //明细信息
    Destination destination = null;
    //景点经纬度
	int lat;
	int lng;
    
    //查询线路
    private QuickActionWidget mBar;
    private OnQuickActionClickListener mActionListener;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 去除标题栏
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.activity_destination_detail_on_map);
		
		//获得景点信息
        Intent in = this.getIntent();
        destination = (Destination)in.getSerializableExtra(Constants.MAP_SHOW_SPOT);
        
    	lat = (int)(destination.getScenerySummary().getLat()*1e6);
    	lng = (int)(destination.getScenerySummary().getLng()*1e6);
        
		initMap();
		
		initSearch();
		
		showScenicSpotInit();
		
        //准备listeners
        this.prepareListeners();
		batchSetListeners();
		
		prepareQuickActionBar(); 
		
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
	 * 搜索信息初始化
	 */
	private void initSearch() {
		OurfarmApp app = (OurfarmApp)this.getApplication();
		
		// 初始化搜索模块，注册事件监听
        mSearch = new MKSearch();
        mSearch.init(app.mBMapMan, new MKSearchListener(){

            @Override
            public void onGetPoiDetailSearchResult(int type, int error) {
            }
            //自驾
			public void onGetDrivingRouteResult(MKDrivingRouteResult res,
					int error) {
				// 错误号可参考MKEvent中的定义
				if (error != 0 || res == null) {
					cannotFindLine();
					return;
				}
				RouteOverlay routeOverlay = new RouteOverlay(DetailOnMapActivity.this, mMapView);
			    // 此处仅展示一个方案作为示例
			    routeOverlay.setData(res.getPlan(0).getRoute(0));
			    mMapView.getOverlays().clear();
			    mMapView.getOverlays().add(routeOverlay);
			    mMapView.invalidate();
			    
			    mMapView.getController().animateTo(res.getStart().pt);
			}
			//公共交通
			public void onGetTransitRouteResult(MKTransitRouteResult res,
					int error) {
				if (error != 0 || res == null) {
					cannotFindLine();
					return;
				}
				TransitOverlay routeOverlay = new TransitOverlay (DetailOnMapActivity.this, mMapView);
			    // 此处仅展示一个方案作为示例
			    routeOverlay.setData(res.getPlan(0));
			    mMapView.getOverlays().clear();
			    mMapView.getOverlays().add(routeOverlay);
			    mMapView.invalidate();
			    
			    mMapView.getController().animateTo(res.getStart().pt);
			}
            //步行
			public void onGetWalkingRouteResult(MKWalkingRouteResult res,
					int error) {
				if (error != 0 || res == null) {
					cannotFindLine();
					return;
				}
				RouteOverlay routeOverlay = new RouteOverlay(DetailOnMapActivity.this, mMapView);
			    // 此处仅展示一个方案作为示例
			    routeOverlay.setData(res.getPlan(0).getRoute(0));
			    mMapView.getOverlays().clear();
			    mMapView.getOverlays().add(routeOverlay);
			    mMapView.invalidate();
			    
			    mMapView.getController().animateTo(res.getStart().pt);
			    
			}
			public void onGetAddrResult(MKAddrInfo res, int error) {
			}
			public void onGetPoiResult(MKPoiResult res, int arg1, int arg2) {
			}
			public void onGetBusDetailResult(MKBusLineResult result, int iError) {
			}

			@Override
			public void onGetSuggestionResult(MKSuggestionResult res, int arg1) {
				// TODO Auto-generated method stub
			}

            @Override
            public void onGetRGCShareUrlResult(String arg0, int arg1) {
                // TODO Auto-generated method stub
            }
        });
	}
	
	/**
	 * 地图第一次加载的时候，显示景点
	 */
	private void showScenicSpotInit() {
		// 得到mMapView的控制权,可以用它控制和驱动平移和缩放
		MapController mMapController = mMapView.getController(); 
        
		GeoPoint point = new GeoPoint(lat, lng); 
		mMapController.setCenter(point); // 设置地图中心点
		//根据距离来确定,取值范围[3,18]
		mMapController.setZoom(12); // 设置地图zoom级别
		
		//添加地图覆盖物
		Drawable marker = getResources().getDrawable(R.drawable.home_checkin_active);  //得到需要标在地图上的资源
		marker.setBounds(0, 0, marker.getIntrinsicWidth(), marker.getIntrinsicHeight());   //为maker定义位置和边界
		
		//得到需要标在地图上的资源
		overitem = new OverItemT(marker, this);
		mMapView.getOverlays().add(overitem); //添加地图覆盖物实例到mMapView
		
		setScenicMark(point);
	}
	
	/**
	 * 地图重绘后，显示景点
	 */
	private void showScenicSpotAfterReDraw() {
		//去掉原来的气泡
		mMapView.removeView(mapSpotPopView);
		// 得到mMapView的控制权,可以用它控制和驱动平移和缩放
		MapController mMapController = mMapView.getController(); 
		
		GeoPoint point = new GeoPoint(lat, lng); 
		//TODO 要通过两点距离来计算，根据距离来确定,取值范围[3,18] 
		mMapController.setZoom(12); // 设置地图zoom级别
		
		setScenicMark(point);
	}
	
	/**
	 * 设置景点标记和弹出泡泡
	 * 
	 * @param point 景点坐标
	 */
	private void setScenicMark(GeoPoint point) {
		//创建景点位置mark时的弹出泡泡
		mapSpotPopView = super.getLayoutInflater().inflate(R.layout.map_my_loc_popview, null);
		mMapView.addView( mapSpotPopView,
                new MapView.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
                		point, MapView.LayoutParams.BOTTOM_CENTER));
		
		//TODO 美化弹出气泡，根据destinationId去掉景点的具体信息
		//设置景点名称
		TextView textView = (TextView)DetailOnMapActivity.this.findViewById(R.id.TextView_map_spot_loc);
		textView.setText(destination.getScenerySummary().getName());
		
	    mapSpotPopView.setVisibility(View.VISIBLE);
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
        
        //线路按钮按下，弹出选择驾车、公交、步行
        lineBtn = new OnClickListener() {
            public void onClick(View v) {
                //线路选择处理
            	mBar.show(v);
            }
        };
        
        //点击选择的线路方式
        mActionListener = new OnQuickActionClickListener() {

			@Override
			public void onQuickActionClicked(QuickActionWidget paramQuickActionWidget, int paramInt) {
				Tools.showToastLong(DetailOnMapActivity.this, "正在查找路线...");
				switch (paramInt) {
				case 0://自驾
					carLine();
					break;
				case 1://公共交通
					busLine();
					break;
				case 2://步行
					walkLine();
					break;
				default:
					break;
				}
			}
        };
    }
    
    /**
     * 自驾路线
     */
    private void carLine() {
    	//当前位置
        MKPlanNode start = new MKPlanNode();
        start.pt = getLocationLatLng();
        //景点位置
        MKPlanNode end = new MKPlanNode();
        end.pt = new GeoPoint(lat, lng);
        
        // 设置驾车路线搜索策略，时间优先、费用最少或距离最短，这里是时间优先
        mSearch.setDrivingPolicy(MKSearch.ECAR_TIME_FIRST);
        mSearch.drivingSearch(null, start, null, end);
        
        //去掉原来的气泡
        mMapView.removeView(mapSpotPopView);
        showScenicSpotAfterReDraw();
    }
    
    /**
     * 公共交通路线
     */
    private void busLine() {
    	//当前位置
    	MKPlanNode start = new MKPlanNode();
    	start.pt = getLocationLatLng();
    	//景点位置
    	MKPlanNode end = new MKPlanNode();
    	end.pt = new GeoPoint(lat, lng);
    	
    	// 设置乘车路线搜索策略，时间优先、最少换乘、最少步行距离或不含地铁
    	mSearch.setTransitPolicy(MKSearch.EBUS_TRANSFER_FIRST);
    	mSearch.transitSearch("北京", start, end); // 必须设置城市名
    	
    	showScenicSpotAfterReDraw();
    }
    
    /**
     * 步行路线
     */
    private void walkLine() {
    	//当前位置
    	MKPlanNode start = new MKPlanNode();
    	start.pt = getLocationLatLng();
    	//景点位置
    	MKPlanNode end = new MKPlanNode();
    	end.pt = new GeoPoint(lat, lng);
    	
    	// 设置步行路线搜索策略，时间优先、费用最少或距离最短，这里是时间优先
    	mSearch.setDrivingPolicy(MKSearch.ECAR_DIS_FIRST);
    	mSearch.walkingSearch(null, start, null, end);
    	
    	showScenicSpotAfterReDraw();
    }
    
    /**
     * 绑定view和监听
     */
    private void batchSetListeners() {
    	//lineBtn后退
    	ImageView iback2home = (ImageView) this.findViewById(R.id.ImageView_button_back);
        iback2home.setOnClickListener(lback);
        //路线
        TextView showLine = (TextView) this.findViewById(R.id.TextView_map_line);
        showLine.setOnClickListener(lineBtn);
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
		     
	    	Summary sum = destination.getScenerySummary();
	    	// 用给定的经纬度构造GeoPoint，单位是微度 (度 * 1E6)
	    	GeoPoint geoPoint = new GeoPoint((int) (sum.getLat() * 1E6), (int) (sum.getLng() * 1E6));
	    	// 存放景点名称和内容id
	    	mGeoList.add(new OverlayItem(geoPoint, sum.getName(), String.valueOf(sum.getDestinationId())));
	    	
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
			super.draw(canvas, mapView, shadow);
			//调整一个drawable边界，使得（0，0）是这个drawable底部最后一行中心的一个像素
			boundCenterBottom(marker);
		}
	     
	    @Override
	    // 处理当点击事件
	    protected boolean onTap(int i) {
	    	setFocus(mGeoList.get(i));
		    return true;
		}
	    
		@Override
		public boolean onTap(GeoPoint arg0, MapView arg1) {
			return super.onTap(arg0, arg1);
		}
    }
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	@Override
	protected void onPause() {
		OurfarmApp app = (OurfarmApp)this.getApplication();
		if (app.mBMapMan != null) {
			mLocationOverlay.disableMyLocation();//关闭定位
	        mLocationOverlay.disableCompass(); // 关闭指南针
	        app.mBMapMan.stop();
		}
		super.onPause();
	}

	@Override
	protected void onResume() {
		OurfarmApp app = (OurfarmApp)this.getApplication();
		if (app.mBMapMan != null) {
	        mLocationOverlay.enableMyLocation();//启用定位
	        mLocationOverlay.enableCompass(); // 打开指南针
	        app.mBMapMan.start();
		}
		super.onResume();
	}
	
	private void prepareQuickActionBar() {
		this.mBar = new QuickActionBar(this);
		this.mBar.addQuickAction(new MyQuickAction(this, R.drawable.near_map_icon_car,
				"自驾"));
		this.mBar.addQuickAction(new MyQuickAction(this, R.drawable.near_map_icon_bus,
				"公共交通"));
		this.mBar.addQuickAction(new MyQuickAction(this, R.drawable.near_map_icon_walk,
				"步行"));
		
		this.mBar.setOnQuickActionClickListener(this.mActionListener);
		
	}
	
	private static class MyQuickAction extends QuickAction {
		private static final ColorFilter BLACK_CF = new LightingColorFilter(
				-16777216, -16777216);

		// public MyQuickAction(Context paramContext, int paramInt1, int
		// paramInt2)
		// {
		// super(buildDrawable(paramContext, paramInt1),
		// String.valueOf(paramInt2));
		// }

		public MyQuickAction(Context paramContext, int paramInt,
				CharSequence paramCharSequence) {
			super(paramContext, paramInt, paramCharSequence);
		}

		// private static Drawable buildDrawable(Context paramContext, int
		// paramInt)
		// {
		// Drawable localDrawable =
		// paramContext.getResources().getDrawable(paramInt);
		// localDrawable.setColorFilter(BLACK_CF);
		// return localDrawable;
		// }
	}
	
	/**
	 * 未找到结果
	 */
	private void cannotFindLine() {
		Tools.showToastShort(DetailOnMapActivity.this, "抱歉，未找到结果");
	}
	
	/**
	 * 获得当前经纬度
	 * 
	 * @return
	 */
	private GeoPoint getLocationLatLng() {
	  	Map<String, Object> localMap = ((OurfarmApp)getApplication()).localMap;
        double lat = (Double)localMap.get(Constants.LOC_LAT);
        double lng = (Double)localMap.get(Constants.LOC_LNG);
        
		return new GeoPoint((int)(lat*1e6), (int)(lng*1e6)); 
	}
}
