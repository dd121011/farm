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
	//�����̷߳��͵���Ϣ
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
      //�����¼�����
        
        new Thread(){
        	public void run()
        	{
        		
        		//todo 
        		//�жϻ���״̬�����硢GPS��״̬
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
        		//�жϸ���״̬
        		
        		//������Դ
        		
        		try {
					sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        		//�ж��Ƿ�Ϊ��һ�ε�½
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
		//���ΰ������� 
		return false;
	}
    
}
