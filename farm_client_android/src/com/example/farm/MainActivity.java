package com.example.farm;

import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Menu;
import android.view.Window;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class MainActivity extends MapActivity  {
	GeoPoint mGeoPoint ;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,   
       // 	    WindowManager.LayoutParams.FLAG_FULLSCREEN);   
        	 // 去除标题栏  
        setContentView(R.layout.l_near_map);
        MapView mapview = (MapView)this.findViewById(R.id.MapView01);
        MapController mc = mapview.getController();
        mapview.setSatellite(true);   
        //设置为街景模式  
        //mMapView.setStreetView(false);  
        //取得MapController对象(控制MapView)  
 
        mapview.setEnabled(true);  
        mapview.setClickable(true);  
        //设置地图支持缩放  
        mapview.setBuiltInZoomControls(true);  
        mc.setZoom(12);  
      //设置起点为成都
        mGeoPoint = new GeoPoint((int) (30.659259 * 1000000), (int) (104.065762 * 1000000));
      //定位到成都
        mc.animateTo(mGeoPoint);
        MyLocationOverlay myLocationOverlay = new MyLocationOverlay();  
        List<Overlay> list = mapview.getOverlays();  
        list.add(myLocationOverlay);  
    
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        
        //tab_host.r
        return true;
    }

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
	class MyLocationOverlay extends Overlay  
	{  
	    public boolean draw(Canvas canvas,MapView mapView,boolean shadow,long when)  
	    {  
	        super.draw(canvas, mapView, shadow);  
	        Paint paint = new Paint();  
	        Point myScreenCoords = new Point();  
	        // 将经纬度转换成实际屏幕坐标  
	        mapView.getProjection().toPixels(mGeoPoint,myScreenCoords);  
	        paint.setStrokeWidth(1);  
	        paint.setARGB(255, 255, 0, 0);  
	        paint.setStyle(Paint.Style.STROKE);  
	        Bitmap bmp = BitmapFactory.decodeResource(getResources(),R.drawable.ic_launcher);  
	        canvas.drawBitmap(bmp,myScreenCoords.x,myScreenCoords.y,paint);  
	        //canvas.drawText("天府广场",myScreenCoords.x, myScreenCoords.y,paint);  
	        return true;  
	    }  
	}  
}
