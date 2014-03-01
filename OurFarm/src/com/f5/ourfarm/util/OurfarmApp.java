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
	
	/* �ٶ�sdk��λ  */
	public LocationClient mLocationClient = null;
	public MyLocationListenner myListener = new MyLocationListenner();
	public NotifyLister mNotifyer=null;
	public Vibrator mVibrator01;
	//���ڴ�Ŷ�λ��Ϣ
	public Map<String, Object> localMap = new HashMap<String, Object>();
	
	/* �ٶ�sdk��ͼ */
	//�ٶ�MapAPI�Ĺ�����
	public BMapManager mBMapMan = null;
	// ��ȨKey
	// �����ַ��http://developer.baidu.com/map/android-mobile-apply-key.htm
	public String mStrKey = "CB5EFB673940B9B41260FFCE47614F4AF9B6F4A6";
	boolean keyRight = true; // ��ȨKey��ȷ����֤ͨ��
	
	public List<Summary> recommentSummary = null;
	
	private static final String TAG = "OurfarmApp"; 
			
	@Override
	public void onCreate() {
		//���ðٶȶ�λ
		mLocationClient = new LocationClient( getApplicationContext() );
		mLocationClient.registerLocationListener( myListener );
		/**
		//����3�д���Ϊ λ��������ش���
		mNotifyer = new NotifyLister();
		mNotifyer.SetNotifyLocation(42.03249652949337,113.3129895882556,3000,"gps");//4����������Ҫλ�����ѵĵ�����꣬���庬������Ϊ��γ�ȣ����ȣ����뷶Χ������ϵ����(gcj02,gps,bd09,bd09ll)
		mLocationClient.registerNotify(mNotifyer);
		*/
		//���ðٶȵ�ͼ
		ourfarmApp = this;
		mBMapMan = new BMapManager(this);
		boolean isSuccess = mBMapMan.init(this.mStrKey, new MyGeneralListener());
		// ��ʼ����ͼsdk�ɹ������ö�λ����ʱ��
		if (isSuccess) {
			//����֪ͨ���(��λ:��) iMaxSecond - ���֪ͨ���,  iMinSecond - ��С֪ͨ���
		    mBMapMan.getLocationManager().setNotifyInternal(120, 60);
		} else {
		    // TODO ��ͼsdk��ʼ��ʧ�ܣ�����ʹ��sdk
		}
		super.onCreate();
		
		Log.d(TAG, "OurFarm create application...");
	}
	
	/**
	 * ��������������λ�õ�ʱ�򣬽���λ��Ϣ���浽localMap��
	 */
	public class MyLocationListenner implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null){
				return ;
			}
			//���ԭ����λ��Ϣ
			localMap.clear();
			
			/**
			    getLocType����ֵ��
			    61 �� GPS��λ���
			    62 �� ɨ�����϶�λ����ʧ�ܡ���ʱ��λ�����Ч��
			    63 �� �����쳣��û�гɹ���������������󡣴�ʱ��λ�����Ч��
			    65 �� ��λ����Ľ����
			    66 �� ���߶�λ�����ͨ��requestOfflineLocaiton����ʱ��Ӧ�ķ��ؽ��
			    67 �� ���߶�λʧ�ܡ�ͨ��requestOfflineLocaiton����ʱ��Ӧ�ķ��ؽ��
			    68 �� ��������ʧ��ʱ�����ұ������߶�λʱ��Ӧ�ķ��ؽ��
			    161�� ��ʾ���綨λ���
			    162~167�� ����˶�λʧ�ܡ� 
			 */
			localMap.put(Constants.LOC_TYPE_CODE, location.getLocType());
			localMap.put(Constants.LOC_LAT, location.getLatitude());//γ��
			localMap.put(Constants.LOC_LNG, location.getLongitude());//����
			localMap.put(Constants.LOC_GET_TIME, System.currentTimeMillis());//����
			//������������£������ϸ��ַ
			if (location.getLocType() == BDLocation.TypeNetWorkLocation){
				localMap.put(Constants.LOC_ADDR, location.getAddrStr());//��ַ
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
	 * λ�����ѻص�����
	 */
	public class NotifyLister extends BDNotifyListener{
		public void onNotify(BDLocation mlocation, float distance){
			mVibrator01.vibrate(1000);
		}
	}
	
	// �����¼���������������ͨ�������������Ȩ��֤�����
	public static class MyGeneralListener implements MKGeneralListener {
		@Override
		public void onGetNetworkState(int iError) {
			Log.d("MyGeneralListener", "onGetNetworkState error is "+ iError);
			Toast.makeText(OurfarmApp.ourfarmApp.getApplicationContext(), "���������������",
					Toast.LENGTH_LONG).show();
		}

		@Override
		public void onGetPermissionState(int iError) {
			Log.d("MyGeneralListener", "onGetPermissionState error is "+ iError);
			if (iError ==  MKEvent.ERROR_PERMISSION_DENIED) {
				// ��ȨKey����
				Toast.makeText(OurfarmApp.ourfarmApp.getApplicationContext(), "��ͼģʽ��ʾʧ�ܣ�",
						Toast.LENGTH_LONG).show();
				OurfarmApp.ourfarmApp.keyRight = false;
			}
		}
	}
	
	@Override
	//��������app���˳�֮ǰ����mapadpi��destroy()�����������ظ���ʼ��������ʱ������
	public void onTerminate() {
		// TODO Auto-generated method stub
		if (mBMapMan != null) {
			mBMapMan.destroy();
			mBMapMan = null;
		}
		super.onTerminate();
	}
}