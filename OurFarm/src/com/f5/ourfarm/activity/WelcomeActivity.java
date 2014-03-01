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
		//第一次运行本程序，进行介绍操作
		if(settings.getBoolean(Constants.FIRST_RUN, true)){
			setContentView(R.layout.navigation);
		    //初始化导航页面
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
	
	//欢迎页面Handler
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
	 * 点击画面上的Go图标，开始进入郊游客
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
	 * 页面发生变化时
	 */
	@Override
	public void OnViewChange(int position) {
		setcurrentPoint(position);
		/**
		 * 在导航时，判断是否进入到第五张页面，如果进入的话，就直接开始郊游客欢迎页面
		 * 这里欢迎页面有5张，下面的圆点只有4个，第五张的目的是：在从第四张直接向左滑动页面时，介绍会结束
		 * 第五张的目的是能捕获到该事件。
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
	 * 进入郊游客页面
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