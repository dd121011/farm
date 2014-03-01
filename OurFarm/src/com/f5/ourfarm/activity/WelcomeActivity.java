package com.f5.ourfarm.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.f5.ourfarm.R;
import com.f5.ourfarm.layout.MyScrollLayout;
import com.f5.ourfarm.listener.OnViewChangeListener;
import com.f5.ourfarm.util.Constants;
import com.umeng.analytics.MobclickAgent;

public class WelcomeActivity extends Activity implements OnViewChangeListener{
	private final int TIME_UP = 1;
	
	private MyScrollLayout mScrollLayout;
	private ImageView[] imgs;
	private int count;
	private int currentItem;
	private ImageView startBtn;
	private RelativeLayout mainRLayout;
	private LinearLayout pointLLayout;
	
	public void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		
		
		SharedPreferences settings = getSharedPreferences(Constants.PREFERENCES_NAME, MODE_PRIVATE);
		//��һ�����б����򣬽��н��ܲ���
		if(settings.getBoolean(Constants.FIRST_RUN, true)){
			setContentView(R.layout.navigation);
		    //��ʼ������ҳ��
			initView();    
		    SharedPreferences.Editor editor = settings.edit();
		    editor.putBoolean(Constants.FIRST_RUN, false);
		    editor.commit();        
		} else {
//			setContentView(R.layout.welcome_screen);
			new Thread() {
				public void run() {
					try {
						Thread.sleep(2000);
					} catch (Exception e) {

					}
					Message msg = new Message();
					msg.what = TIME_UP;
					handler.sendMessage(msg);
				}
			}.start();
		}
	}
	
	 public void onResume() {
	        super.onResume();
	        MobclickAgent.onResume(this);
	    }
	    public void onPause() {
	        super.onPause();
	        MobclickAgent.onPause(this);
	    }
	
	//��ӭҳ��Handler
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if(msg.what == TIME_UP) {
				Intent intent = new Intent();
				intent.setClass(WelcomeActivity.this, MainActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.welcome_screen_fade, R.anim.welcome_screen_hold);
				WelcomeActivity.this.finish();
			}
		}
	};
	
	private void initView() {
		mScrollLayout  = (MyScrollLayout) findViewById(R.id.ScrollLayout);
		pointLLayout = (LinearLayout) findViewById(R.id.llayout);
		mainRLayout = (RelativeLayout) findViewById(R.id.mainRLayout);
		startBtn = (ImageView) findViewById(R.id.startBtn);
		startBtn.setOnClickListener(onClick);
		count = mScrollLayout.getChildCount() - 1;
		imgs = new ImageView[count];
		for(int i = 0; i< count;i++) {
			imgs[i] = (ImageView) pointLLayout.getChildAt(i);
			imgs[i].setEnabled(true);
			imgs[i].setTag(i);
		}
		currentItem = 0;
		imgs[currentItem].setEnabled(false);
		mScrollLayout.SetOnViewChangeListener(this);
	}
	
	/**
	 * ��������ϵ�Goͼ�꣬��ʼ���뽼�ο�
	 */
	private View.OnClickListener onClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			    case R.id.startBtn:
			    	beginOurFarm();
				    break;
			}
		}
	};

	/**
	 * ҳ�淢���仯ʱ
	 */
	@Override
	public void OnViewChange(int position) {
		setcurrentPoint(position);
		/**
		 * �ڵ���ʱ���ж��Ƿ���뵽������ҳ�棬�������Ļ�����ֱ�ӿ�ʼ���οͻ�ӭҳ��
		 * ���ﻶӭҳ����5�ţ������Բ��ֻ��4���������ŵ�Ŀ���ǣ��ڴӵ�����ֱ�����󻬶�ҳ��ʱ�����ܻ����
		 * �����ŵ�Ŀ�����ܲ��񵽸��¼���
		 * 
		 */
		if(position == 4) {
			beginOurFarm();
		}
	}

	private void setcurrentPoint(int position) {
		Log.d("moveright", String.valueOf(position));
		
		if(position < 0 || position > count -1 || currentItem == position) {
			return;
		}
		imgs[currentItem].setEnabled(true);
		imgs[position].setEnabled(false);
		currentItem = position;
	}
	
	/**
	 * ���뽼�ο�ҳ��
	 */
	private void beginOurFarm() {
		mScrollLayout.setVisibility(View.GONE);
	    pointLLayout.setVisibility(View.GONE);
	    mainRLayout.setBackgroundResource(R.drawable.welcome_bg);
	    
		Intent intent = new Intent(WelcomeActivity.this,MainActivity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.welcome_screen_fade, R.anim.welcome_screen_hold);
	}
}