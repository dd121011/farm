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
 * ͨ����ͼ�鿴�����ľ���
 * 
 * @author zb
 *
 */
public class NearbyMapActivity extends MapActivity {
	
	//���˰�ť
	OnClickListener lback = null;
	
	// �����λʱ����������View
	static View mapSpotPopView = null;	
	
	static MapView mMapView;
	
	LocationListener mLocationListener = null;//onResumeʱע���listener��onPauseʱ��ҪRemove
	MyLocationOverlay mLocationOverlay = null;//��λͼ��
	
	OverItemT overitem = null;
	
    //�洢ѡ��ľ���
    private int selectDistance;
    //��ʾ����
    private HashMap<Long, Summary> showSpotsMap = new HashMap<Long ,Summary>();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// ȥ��������
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.activity_nearby_map);
		
		//��þ�����Ϣ
        Intent in = this.getIntent();
		selectDistance = in.getIntExtra(Constants.MAP_SHOW_DISTANCE, 1);
		showSpotsMap = (HashMap<Long, Summary>)in.getSerializableExtra(Constants.MAP_SHOW_SPOT);	
		
		initMap();
		controlMap();
		showNearBySpot();
		
        //׼��listeners
        this.prepareListeners();
		batchSetListeners();
		
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
	 * ���Ƶ�ͼ�Ļ���
	 */
	private void controlMap() {
		// �õ�mMapView�Ŀ���Ȩ,�����������ƺ�����ƽ�ƺ�����
		MapController mMapController = mMapView.getController(); 
		//���ڴ�Ŷ�λ��Ϣ
	  	Map<String, Object> localMap = ((OurfarmApp)getApplication()).localMap;
	  	
		// �ø����ľ�γ�ȹ���һ��GeoPoint����λ��΢�� (�� * 1E6)��ȡԭ���Ķ�λ��
//        GeoPoint point =new GeoPoint((int)(39.90923*1e6), (int)(116.397428*1e6));
        
        double lat = (Double)localMap.get(Constants.LOC_LAT);
        double lng = (Double)localMap.get(Constants.LOC_LNG);
        
        Log.d("to center lat and lng", String.valueOf(lat) + " " + String.valueOf(lng));
        
		GeoPoint point = new GeoPoint((int)(lat*1e6), (int)(lng*1e6)); 
		mMapController.setCenter(point); // ���õ�ͼ���ĵ�
		// ���ݾ�����ȷ��
		mMapController.setZoom(getZoomFromDistance()); // ���õ�ͼzoom����
	}
	
	/**
	 * ��ͼ��ʾ��������
	 */
	private void showNearBySpot() {
		//���ItemizedOverlay
		Drawable marker = getResources().getDrawable(R.drawable.home_checkin_active);  //�õ���Ҫ���ڵ�ͼ�ϵ���Դ
		marker.setBounds(0, 0, marker.getIntrinsicWidth(), marker
				.getIntrinsicHeight());   //Ϊmaker����λ�úͱ߽�
		
		//�õ���Ҫ���ڵ�ͼ�ϵ���Դ
		overitem = new OverItemT(marker, this);
		mMapView.getOverlays().add(overitem); //���ItemizedOverlayʵ����mMapView
				
		//�����������λ��markʱ�ĵ�������
		mapSpotPopView = super.getLayoutInflater().inflate(R.layout.map_my_loc_popview, null);
		mMapView.addView( mapSpotPopView,
                new MapView.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
                		null, MapView.LayoutParams.TOP_LEFT));
		mapSpotPopView.setVisibility(View.GONE);
	}
	
	/**
	 * ��ȡ��ͼ���ż���
	 * 
	 * @return ����ֵ,ȡֵ��Χ[3,18]
	 */
	private int getZoomFromDistance() {
		//����ѡ��ľ���ѡ�񼶱�
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
     * �����ȶ�����
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
     * �������¼���Ķ�����
     */
    private void prepareListeners() {
        // ���˰�ťdetail_grid_picture->detail
        lback = new OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        };
        
        // ע�ᶨλ�¼�
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
     * ��view�ͼ���
     */
    private void batchSetListeners() {
        ImageView iback2home = (ImageView) this.findViewById(R.id.ImageView_button_back);
        iback2home.setOnClickListener(lback);
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
		     
		    //�����ȶȽ�������
		    List<Map.Entry<Long, Summary>> sortByHotspot = sortByHotspot(showSpotsMap);
		    //TODO Ĭ��ֻ��ʾǰ10λ���Ժ�Ҫ������ҳ����ʽ
		    int showCountInPage = sortByHotspot.size();
		    if(showCountInPage > 10){
		    	showCountInPage = 10;
		    }
		    for(int i = 0; i < showCountInPage; i++) {
		    	Summary sum = sortByHotspot.get(i).getValue();
		    	// �ø����ľ�γ�ȹ���GeoPoint����λ��΢�� (�� * 1E6)
		    	GeoPoint geoPoint = new GeoPoint((int) (sum.getLat() * 1E6), (int) (sum.getLng() * 1E6));
		    	// ��ž������ƺ�����id
		    	mGeoList.add(new OverlayItem(geoPoint, sum.getName(), String.valueOf(sum.getDestinationId())));
		    }
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

			// Projection�ӿ�������Ļ��������;�γ������֮��ı任
			Projection projection = mapView.getProjection(); 
			for (int index = size() - 1; index >= 0; index--) { // ����mGeoList
				OverlayItem overLayItem = getItem(index); // �õ�����������item

				String title = overLayItem.getTitle();
				// �Ѿ�γ�ȱ任�������MapView���Ͻǵ���Ļ��������
				Point point = projection.toPixels(overLayItem.getPoint(), null); 

				// ���ڴ˴�������Ļ��ƴ���
				/*
				Paint paintText = new Paint();
				paintText.setColor(Color.BLUE);
				paintText.setTextSize(15);
				canvas.drawText(title, point.x-30, point.y, paintText); // �����ı�
				*/
			}

			super.draw(canvas, mapView, shadow);
			//����һ��drawable�߽磬ʹ�ã�0��0�������drawable�ײ����һ�����ĵ�һ������
			boundCenterBottom(marker);
		}
	     
	    @Override
	    // ��������¼�
	    protected boolean onTap(int i) {
	    	setFocus(mGeoList.get(i));
			// ��������λ��,��ʹ֮��ʾ
			GeoPoint pt = mGeoList.get(i).getPoint();
			NearbyMapActivity.mMapView.updateViewLayout( NearbyMapActivity.mapSpotPopView,
	                new MapView.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
	                		pt, MapView.LayoutParams.BOTTOM_CENTER));
			//��ȡ����ID
			final long destinationId = Long.valueOf(mGeoList.get(i).getSnippet());
			//TODO �����������ݣ�����destinationIdȥ������ľ�����Ϣ
			
			NearbyMapActivity.mapSpotPopView.setVisibility(View.VISIBLE);
			//��ʾ
			TextView textView = (TextView)NearbyMapActivity.this.findViewById(R.id.TextView_map_spot_loc);
			textView.setText(mGeoList.get(i).getTitle());
			
			//��ת���������ϸ
			textView.setOnClickListener(new OnClickListener() {
	            public void onClick(View v) {
	            	Intent nearby2detail = new Intent(NearbyMapActivity.this,DetailActivity.class);
	                //���������������ݱ�����Intent��ȷ�����ݿ���activity�䴫��
	                nearby2detail.putExtra("destinationId", destinationId);
	                startActivity(nearby2detail);
	            }
	        });
			
		    return true;
		}
	    
		@Override
		public boolean onTap(GeoPoint arg0, MapView arg1) {
			// TODO Auto-generated method stub
			// ��ȥ����������
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
	        mLocationOverlay.disableCompass(); // �ر�ָ����
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
	        mLocationOverlay.enableCompass(); // ��ָ����
	        app.mBMapMan.start();
		}
		super.onResume();
	}
}
