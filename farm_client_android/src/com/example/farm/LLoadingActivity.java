package com.example.farm;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;

public class LLoadingActivity extends BaseAvtivity implements OnClickListener{
	//接收线程发送的消息
	private static final String TAG = "LLoadingActivity";
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			changeView(1);
		}
	};
	private void changeView(int id)
	{
		setContentView(R.layout.l_welcom);
		findViewById(R.id.button1).setOnClickListener(this);
	}
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.l_loading);
      //增加事件监听
        
        new Thread(){
        	public void run()
        	{
        		
        		//todo 
        		//判断机器状态，网络、GPS等状态
        		boolean isConnect = isConnectInternet();
        		if(isConnect)
        		{
        			Log.i(TAG, "net status is "+isConnect);
        		}
        		boolean isGps = isGpsEnabled();
        		if(isGps)
        		{
        			Log.i(TAG, "gps status is "+isGps);
        		}
        		//判断更新状态
        		
        		//加载资源
        		
        		try {
					sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        		//判断是否为第一次登陆
        		if(isFirstOpen())
        			handler.sendMessage(handler.obtainMessage());
        		else
        			setContentView(R.layout.activity_main);
        	}
        }.start();
        
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.l_loading, menu);
        return true;
    }
	@Override
	public void onClick(View v) {
		//
		if(v.getId() == R.id.button1)
			setContentView(R.layout.activity_main);
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		//屏蔽按键操作 
		return false;
	}
    
}
