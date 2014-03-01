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
 * ͨ����ͼ�鿴��ǰ����
 * 
 * @author lfy
 *
 */
public class DetailOnMapActivity extends MapActivity {
	
	//���˰�ť
	OnClickListener lback = null;
	//��·��ť
	OnClickListener lineBtn = null;
	
	// �����λʱ����������View
	static View mapSpotPopView = null;	
	
	static MapView mMapView;
	MKSearch mSearch = null;	// ����ģ�飬Ҳ��ȥ����ͼģ�����ʹ��
	
//	LocationListener mLocationListener = null;//onResumeʱע���listener��onPauseʱ��ҪRemove
	MyLocationOverlay mLocationOverlay = null;//��λͼ��
	
	OverItemT overitem = null;
	
    //��ϸ��Ϣ
    Destination destination = null;
    //���㾭γ��
	int lat;
	int lng;
    
    //��ѯ��·
    private QuickActionWidget mBar;
    private OnQuickActionClickListener mActionListener;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// ȥ��������
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.activity_destination_detail_on_map);
		
		//��þ�����Ϣ
        Intent in = this.getIntent();
        destination = (Destination)in.getSerializableExtra(Constants.MAP_SHOW_SPOT);
        
    	lat = (int)(destination.getScenerySummary().getLat()*1e6);
    	lng = (int)(destination.getScenerySummary().getLng()*1e6);
        
		initMap();
		
		initSearch();
		
		showScenicSpotInit();
		
        //׼��listeners
        this.prepareListeners();
		batchSetListeners();
		
		prepareQuickActionBar(); 
		
	}
	
	/**
	 * ��ͼ��Ϣ��ʼ��
	 */
	private void initMap() {
		OurfarmApp app = (OurfarmApp)this.getApplication();
		//��ȡ��ͼManager
		if (app.mBMapMan == null) {
			app.mBMapMan = new BMapManager(getApplication());
			app.mBMapMan.init(app.mStrKey, new OurfarmApp.MyGeneralListener());
		}
		app.mBMapMan.start();
		
		super.initMapActivity(app.mBMapMan);

		mMapView = (MapView) findViewById(R.id.bmapsView);
		mMapView.setBuiltInZoomControls(true); // �����������õ����ſؼ�
		//���������Ŷ���������Ҳ��ʾoverlay,Ĭ��Ϊ������
        mMapView.setDrawOverlayWhenZooming(true);
        
		// ��Ӷ�λͼ��
        mLocationOverlay = new MyLocationOverlay(this, mMapView);
		mMapView.getOverlays().add(mLocationOverlay);
	}
	
	/**
	 * ������Ϣ��ʼ��
	 */
	private void initSearch() {
		OurfarmApp app = (OurfarmApp)this.getApplication();
		
		// ��ʼ������ģ�飬ע���¼�����
        mSearch = new MKSearch();
        mSearch.init(app.mBMapMan, new MKSearchListener(){

            @Override
            public void onGetPoiDetailSearchResult(int type, int error) {
            }
            //�Լ�
			public void onGetDrivingRouteResult(MKDrivingRouteResult res,
					int error) {
				// ����ſɲο�MKEvent�еĶ���
				if (error != 0 || res == null) {
					cannotFindLine();
					return;
				}
				RouteOverlay routeOverlay = new RouteOverlay(DetailOnMapActivity.this, mMapView);
			    // �˴���չʾһ��������Ϊʾ��
			    routeOverlay.setData(res.getPlan(0).getRoute(0));
			    mMapView.getOverlays().clear();
			    mMapView.getOverlays().add(routeOverlay);
			    mMapView.invalidate();
			    
			    mMapView.getController().animateTo(res.getStart().pt);
			}
			//������ͨ
			public void onGetTransitRouteResult(MKTransitRouteResult res,
					int error) {
				if (error != 0 || res == null) {
					cannotFindLine();
					return;
				}
				TransitOverlay routeOverlay = new TransitOverlay (DetailOnMapActivity.this, mMapView);
			    // �˴���չʾһ��������Ϊʾ��
			    routeOverlay.setData(res.getPlan(0));
			    mMapView.getOverlays().clear();
			    mMapView.getOverlays().add(routeOverlay);
			    mMapView.invalidate();
			    
			    mMapView.getController().animateTo(res.getStart().pt);
			}
            //����
			public void onGetWalkingRouteResult(MKWalkingRouteResult res,
					int error) {
				if (error != 0 || res == null) {
					cannotFindLine();
					return;
				}
				RouteOverlay routeOverlay = new RouteOverlay(DetailOnMapActivity.this, mMapView);
			    // �˴���չʾһ��������Ϊʾ��
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
	 * ��ͼ��һ�μ��ص�ʱ����ʾ����
	 */
	private void showScenicSpotInit() {
		// �õ�mMapView�Ŀ���Ȩ,�����������ƺ�����ƽ�ƺ�����
		MapController mMapController = mMapView.getController(); 
        
		GeoPoint point = new GeoPoint(lat, lng); 
		mMapController.setCenter(point); // ���õ�ͼ���ĵ�
		//���ݾ�����ȷ��,ȡֵ��Χ[3,18]
		mMapController.setZoom(12); // ���õ�ͼzoom����
		
		//��ӵ�ͼ������
		Drawable marker = getResources().getDrawable(R.drawable.home_checkin_active);  //�õ���Ҫ���ڵ�ͼ�ϵ���Դ
		marker.setBounds(0, 0, marker.getIntrinsicWidth(), marker.getIntrinsicHeight());   //Ϊmaker����λ�úͱ߽�
		
		//�õ���Ҫ���ڵ�ͼ�ϵ���Դ
		overitem = new OverItemT(marker, this);
		mMapView.getOverlays().add(overitem); //��ӵ�ͼ������ʵ����mMapView
		
		setScenicMark(point);
	}
	
	/**
	 * ��ͼ�ػ����ʾ����
	 */
	private void showScenicSpotAfterReDraw() {
		//ȥ��ԭ��������
		mMapView.removeView(mapSpotPopView);
		// �õ�mMapView�Ŀ���Ȩ,�����������ƺ�����ƽ�ƺ�����
		MapController mMapController = mMapView.getController(); 
		
		GeoPoint point = new GeoPoint(lat, lng); 
		//TODO Ҫͨ��������������㣬���ݾ�����ȷ��,ȡֵ��Χ[3,18] 
		mMapController.setZoom(12); // ���õ�ͼzoom����
		
		setScenicMark(point);
	}
	
	/**
	 * ���þ����Ǻ͵�������
	 * 
	 * @param point ��������
	 */
	private void setScenicMark(GeoPoint point) {
		//��������λ��markʱ�ĵ�������
		mapSpotPopView = super.getLayoutInflater().inflate(R.layout.map_my_loc_popview, null);
		mMapView.addView( mapSpotPopView,
                new MapView.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
                		point, MapView.LayoutParams.BOTTOM_CENTER));
		
		//TODO �����������ݣ�����destinationIdȥ������ľ�����Ϣ
		//���þ�������
		TextView textView = (TextView)DetailOnMapActivity.this.findViewById(R.id.TextView_map_spot_loc);
		textView.setText(destination.getScenerySummary().getName());
		
	    mapSpotPopView.setVisibility(View.VISIBLE);
	}
	
    /**
     * �������¼���Ķ�����
     */
    private void prepareListeners() {
        // ���˰�ťdetail_grid_picture->detail
        lback = new OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        };
        
        //��·��ť���£�����ѡ��ݳ�������������
        lineBtn = new OnClickListener() {
            public void onClick(View v) {
                //��·ѡ����
            	mBar.show(v);
            }
        };
        
        //���ѡ�����·��ʽ
        mActionListener = new OnQuickActionClickListener() {

			@Override
			public void onQuickActionClicked(QuickActionWidget paramQuickActionWidget, int paramInt) {
				Tools.showToastLong(DetailOnMapActivity.this, "���ڲ���·��...");
				switch (paramInt) {
				case 0://�Լ�
					carLine();
					break;
				case 1://������ͨ
					busLine();
					break;
				case 2://����
					walkLine();
					break;
				default:
					break;
				}
			}
        };
    }
    
    /**
     * �Լ�·��
     */
    private void carLine() {
    	//��ǰλ��
        MKPlanNode start = new MKPlanNode();
        start.pt = getLocationLatLng();
        //����λ��
        MKPlanNode end = new MKPlanNode();
        end.pt = new GeoPoint(lat, lng);
        
        // ���üݳ�·���������ԣ�ʱ�����ȡ��������ٻ������̣�������ʱ������
        mSearch.setDrivingPolicy(MKSearch.ECAR_TIME_FIRST);
        mSearch.drivingSearch(null, start, null, end);
        
        //ȥ��ԭ��������
        mMapView.removeView(mapSpotPopView);
        showScenicSpotAfterReDraw();
    }
    
    /**
     * ������ͨ·��
     */
    private void busLine() {
    	//��ǰλ��
    	MKPlanNode start = new MKPlanNode();
    	start.pt = getLocationLatLng();
    	//����λ��
    	MKPlanNode end = new MKPlanNode();
    	end.pt = new GeoPoint(lat, lng);
    	
    	// ���ó˳�·���������ԣ�ʱ�����ȡ����ٻ��ˡ����ٲ��о���򲻺�����
    	mSearch.setTransitPolicy(MKSearch.EBUS_TRANSFER_FIRST);
    	mSearch.transitSearch("����", start, end); // �������ó�����
    	
    	showScenicSpotAfterReDraw();
    }
    
    /**
     * ����·��
     */
    private void walkLine() {
    	//��ǰλ��
    	MKPlanNode start = new MKPlanNode();
    	start.pt = getLocationLatLng();
    	//����λ��
    	MKPlanNode end = new MKPlanNode();
    	end.pt = new GeoPoint(lat, lng);
    	
    	// ���ò���·���������ԣ�ʱ�����ȡ��������ٻ������̣�������ʱ������
    	mSearch.setDrivingPolicy(MKSearch.ECAR_DIS_FIRST);
    	mSearch.walkingSearch(null, start, null, end);
    	
    	showScenicSpotAfterReDraw();
    }
    
    /**
     * ��view�ͼ���
     */
    private void batchSetListeners() {
    	//lineBtn����
    	ImageView iback2home = (ImageView) this.findViewById(R.id.ImageView_button_back);
        iback2home.setOnClickListener(lback);
        //·��
        TextView showLine = (TextView) this.findViewById(R.id.TextView_map_line);
        showLine.setOnClickListener(lineBtn);
    }
    
    /**
     * ��ʾ���徰��ı��
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
	    	// �ø����ľ�γ�ȹ���GeoPoint����λ��΢�� (�� * 1E6)
	    	GeoPoint geoPoint = new GeoPoint((int) (sum.getLat() * 1E6), (int) (sum.getLng() * 1E6));
	    	// ��ž������ƺ�����id
	    	mGeoList.add(new OverlayItem(geoPoint, sum.getName(), String.valueOf(sum.getDestinationId())));
	    	
		    //��һ����ItemizedOverlay��ִ�����в����Ĺ��߷����� 
		    //����ͨ��createItem(int)�����ṩitem��һ���������ݣ������ڵ�����������ǰ�����ȵ������������
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
			//����һ��drawable�߽磬ʹ�ã�0��0�������drawable�ײ����һ�����ĵ�һ������
			boundCenterBottom(marker);
		}
	     
	    @Override
	    // ��������¼�
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
			mLocationOverlay.disableMyLocation();//�رն�λ
	        mLocationOverlay.disableCompass(); // �ر�ָ����
	        app.mBMapMan.stop();
		}
		super.onPause();
	}

	@Override
	protected void onResume() {
		OurfarmApp app = (OurfarmApp)this.getApplication();
		if (app.mBMapMan != null) {
	        mLocationOverlay.enableMyLocation();//���ö�λ
	        mLocationOverlay.enableCompass(); // ��ָ����
	        app.mBMapMan.start();
		}
		super.onResume();
	}
	
	private void prepareQuickActionBar() {
		this.mBar = new QuickActionBar(this);
		this.mBar.addQuickAction(new MyQuickAction(this, R.drawable.near_map_icon_car,
				"�Լ�"));
		this.mBar.addQuickAction(new MyQuickAction(this, R.drawable.near_map_icon_bus,
				"������ͨ"));
		this.mBar.addQuickAction(new MyQuickAction(this, R.drawable.near_map_icon_walk,
				"����"));
		
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
	 * δ�ҵ����
	 */
	private void cannotFindLine() {
		Tools.showToastShort(DetailOnMapActivity.this, "��Ǹ��δ�ҵ����");
	}
	
	/**
	 * ��õ�ǰ��γ��
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
