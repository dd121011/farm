package com.f5.ourfarm.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Application;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.BDNotifyListener;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKEvent;
import com.baidu.mapapi.MKGeneralListener;
import com.f5.ourfarm.model.Summary;

public class OurfarmApp extends Application {

	static OurfarmApp ourfarmApp;
	
	/* 百度sdk定位  */
	public LocationClient mLocationClient = null;
	public MyLocationListenner myListener = new MyLocationListenner();
	public NotifyLister mNotifyer=null;
	public Vibrator mVibrator01;
	//用于存放定位信息
	public Map<String, Object> localMap = new HashMap<String, Object>();
	
	/* 百度sdk地图 */
	//百度MapAPI的管理类
	public BMapManager mBMapMan = null;
	// 授权Key
	// 申请地址：http://developer.baidu.com/map/android-mobile-apply-key.htm
	public String mStrKey = "CB5EFB673940B9B41260FFCE47614F4AF9B6F4A6";
	boolean keyRight = true; // 授权Key正确，验证通过
	
	public List<Summary> recommentSummary = null;
	
	private static final String TAG = "OurfarmApp"; 
			
	@Override
	public void onCreate() {
		//调用百度定位
		mLocationClient = new LocationClient( getApplicationContext() );
		mLocationClient.registerLocationListener( myListener );
		/**
		//如下3行代码为 位置提醒相关代码
		mNotifyer = new NotifyLister();
		mNotifyer.SetNotifyLocation(42.03249652949337,113.3129895882556,3000,"gps");//4个参数代表要位置提醒的点的坐标，具体含义依次为：纬度，经度，距离范围，坐标系类型(gcj02,gps,bd09,bd09ll)
		mLocationClient.registerNotify(mNotifyer);
		*/
		//调用百度地图
		ourfarmApp = this;
		mBMapMan = new BMapManager(this);
		boolean isSuccess = mBMapMan.init(this.mStrKey, new MyGeneralListener());
		// 初始化地图sdk成功，设置定位监听时间
		if (isSuccess) {
			//设置通知间隔(单位:秒) iMaxSecond - 最大通知间隔,  iMinSecond - 最小通知间隔
		    mBMapMan.getLocationManager().setNotifyInternal(120, 60);
		} else {
		    // TODO 地图sdk初始化失败，不能使用sdk
		}
		super.onCreate();
		
		Log.d(TAG, "OurFarm create application...");
	}
	
	/**
	 * 监听函数，有新位置的时候，将定位信息保存到localMap中
	 */
	public class MyLocationListenner implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null){
				return ;
			}
			//清空原来定位信息
			localMap.clear();
			
			/**
			    getLocType返回值：
			    61 ： GPS定位结果
			    62 ： 扫描整合定位依据失败。此时定位结果无效。
			    63 ： 网络异常，没有成功向服务器发起请求。此时定位结果无效。
			    65 ： 定位缓存的结果。
			    66 ： 离线定位结果。通过requestOfflineLocaiton调用时对应的返回结果
			    67 ： 离线定位失败。通过requestOfflineLocaiton调用时对应的返回结果
			    68 ： 网络连接失败时，查找本地离线定位时对应的返回结果
			    161： 表示网络定位结果
			    162~167： 服务端定位失败。 
			 */
			localMap.put(Constants.LOC_TYPE_CODE, location.getLocType());
			localMap.put(Constants.LOC_LAT, location.getLatitude());//纬度
			localMap.put(Constants.LOC_LNG, location.getLongitude());//经度
			localMap.put(Constants.LOC_GET_TIME, System.currentTimeMillis());//经度
			//网络连接情况下，获得详细地址
			if (location.getLocType() == BDLocation.TypeNetWorkLocation){
				localMap.put(Constants.LOC_ADDR, location.getAddrStr());//地址
			}
			Log.d("request loc", "Res: " + localMap.get(Constants.LOC_TYPE_CODE) +" " + localMap.get(Constants.LOC_LAT) ); 
		}
		
		public void onReceivePoi(BDLocation poiLocation) {
			if (poiLocation == null){
				return ;
			}
		}
	}
	
	/**
	 * 位置提醒回调函数
	 */
	public class NotifyLister extends BDNotifyListener{
		public void onNotify(BDLocation mlocation, float distance){
			mVibrator01.vibrate(1000);
		}
	}
	
	// 常用事件监听，用来处理通常的网络错误，授权验证错误等
	public static class MyGeneralListener implements MKGeneralListener {
		@Override
		public void onGetNetworkState(int iError) {
			Log.d("MyGeneralListener", "onGetNetworkState error is "+ iError);
			Toast.makeText(OurfarmApp.ourfarmApp.getApplicationContext(), "您的网络出错啦！",
					Toast.LENGTH_LONG).show();
		}

		@Override
		public void onGetPermissionState(int iError) {
			Log.d("MyGeneralListener", "onGetPermissionState error is "+ iError);
			if (iError ==  MKEvent.ERROR_PERMISSION_DENIED) {
				// 授权Key错误
				Toast.makeText(OurfarmApp.ourfarmApp.getApplicationContext(), "地图模式显示失败！",
						Toast.LENGTH_LONG).show();
				OurfarmApp.ourfarmApp.keyRight = false;
			}
		}
	}
	
	@Override
	//建议在您app的退出之前调用mapadpi的destroy()函数，避免重复初始化带来的时间消耗
	public void onTerminate() {
		// TODO Auto-generated method stub
		if (mBMapMan != null) {
			mBMapMan.destroy();
			mBMapMan = null;
		}
		super.onTerminate();
	}
}