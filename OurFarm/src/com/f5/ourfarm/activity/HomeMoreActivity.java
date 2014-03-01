package com.f5.ourfarm.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.f5.ourfarm.R;
import com.f5.ourfarm.service.UpdateService;
import com.f5.ourfarm.sqlite.DestinationDbAdpter;
import com.f5.ourfarm.util.Constants;
import com.f5.ourfarm.util.Tools;
import com.f5.ourfarm.widget.Tip;
import com.umeng.analytics.MobclickAgent;

/**
 * 更多页面
 * 
 * @author tianhao
 *
 */
public class HomeMoreActivity extends BaseActivity {
    
    private static final String TAG = "更多页面:";
    private static final String errMsg = "更多页面操作失败";
    
    private  UpdateService updateService;
    
    private Context mContext = HomeMoreActivity.this; 
    
    //数据库连接
    DestinationDbAdpter destinationDbAdpter;

    //情况本地缓存
    OnClickListener ldelete;
    //更新本地数据
    OnClickListener lupdate;
    //获取最新版本
    OnClickListener lgetNewVersion;
    //免责声明
    OnClickListener ldisclaimer;
    //联系我们
    OnClickListener lcontactUs;
    //意见反馈
    OnClickListener lfeedbackUs;
    //关于我们
    OnClickListener laboutUs;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 去除标题栏
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main_more);
        
        //准备listeners
        this.prepareListeners();
        
        //设置底部切换颜色
        ImageView ivMore = (ImageView) this.findViewById(R.id.ImageView_home_more);
        ivMore.setImageResource(R.drawable.home_more_active);
        
        //设置listeners
        this.batchSetListeners();
        
        //数据库初始化
        destinationDbAdpter = new DestinationDbAdpter(this);
        destinationDbAdpter.open();

    }
    
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
    
  //这里需要用到ServiceConnection在Context.bindService和context.unBindService()里用到  
    private ServiceConnection mServiceConnection = new ServiceConnection() {  
        //当我bindService时，让TextView显示MyService里getSystemTime()方法的返回值   
        public void onServiceConnected(ComponentName name, IBinder service) {  
            // TODO Auto-generated method stub  
            updateService = ((UpdateService.MyBinder)service).getService(); 
        }  
          
        public void onServiceDisconnected(ComponentName name) {  
            // TODO Auto-generated method stub  
              
        }
 
    };

    /**
     * 监听到事件后的动作；
     */
    private void prepareListeners() {
      
        // 清空缓存
        ldelete = new OnClickListener() {
            public void onClick(View v) {
                destinationDbAdpter.deleteCache();
                Tools.showToastLong(HomeMoreActivity.this, Constants.MORE_CLEAN_DATA);
            }
        };
        // 更新本地数据
        lupdate = new OnClickListener() {
            public void onClick(View v) {
                Tools.showToastLong(HomeMoreActivity.this, "系统正在为您更新数据，请放心进行其他操作。");
                // TODO 更新，现阶段更新即get clsssic 
                Intent i  = new Intent();  
                i.setClass(HomeMoreActivity.this, UpdateService.class); 
                mContext.startService(i);
            }
        };
        //获取最新版本
        lgetNewVersion = new OnClickListener() {
            public void onClick(View v) {
            	//TODO 版本更新还没有实现，暂时用提示代替
            	Tools.showToastLong(HomeMoreActivity.this, "您当前已经是最新版本了。");
            }
        };
        //免责声明
        ldisclaimer = new OnClickListener() {
            public void onClick(View v) {
            	new Tip(HomeMoreActivity.this).show();
            }
        };
        //联系我们
        lcontactUs = new OnClickListener() {
            public void onClick(View v) {
            	new Tip(HomeMoreActivity.this,1).show();
            }
        };
        //意见反馈
        lfeedbackUs = new OnClickListener() {
            public void onClick(View v) {
            	//到意见反馈页面
            	Intent i2feedback = new Intent(getApplicationContext(), MoreFeedbackActivity.class);
                startActivity(i2feedback);
            }
        };
        //关于我们
        laboutUs = new OnClickListener() {
        	public void onClick(View v) {
        		//重回到导航页
        		SharedPreferences settings = getSharedPreferences(Constants.PREFERENCES_NAME, MODE_PRIVATE);
        		SharedPreferences.Editor editor = settings.edit();
        		editor.putBoolean(Constants.FIRST_RUN, true);
        		editor.commit();        
        		
        		Intent i2my = new Intent(getApplicationContext(),WelcomeActivity.class);
        		startActivity(i2my);
        		finish();
        	}
        };
           
    }
    
    /**
     * 绑定view和监听
     */
    private void batchSetListeners() {
        //底部面板4个按钮
        RelativeLayout rHome= (RelativeLayout) this.findViewById(R.id.Layout_home_panelBottom_home);
        rHome.setTag(1);
        rHome.setOnClickListener(lPanelBottom);
        
        RelativeLayout rCheckin= (RelativeLayout) this.findViewById(R.id.Layout_home_panelBottom_checkin);
        rCheckin.setTag(2);
        rCheckin.setOnClickListener(lPanelBottom);
        
        RelativeLayout rMy= (RelativeLayout) this.findViewById(R.id.Layout_home_panelBottom_my);
        rMy.setTag(3);
        rMy.setOnClickListener(lPanelBottom);
        
        RelativeLayout rMore= (RelativeLayout) this.findViewById(R.id.Layout_home_panelBottom_more);
        rMore.setTag(4);
        rMore.setOnClickListener(lPanelBottom);
        RelativeLayout rHomeMain = (RelativeLayout) this
				.findViewById(R.id.Layout_home_main);
		rHomeMain.setTag(5);
		rHomeMain.setOnClickListener(lPanelBottom);
        
        // 更新本地数据
        RelativeLayout moreUpdateData = (RelativeLayout) this.findViewById(R.id.Layout_more_update_data);
        moreUpdateData.setOnClickListener(lupdate);
        // 清空缓存
        RelativeLayout moreCleanData = (RelativeLayout) this.findViewById(R.id.Layout_more_clear_data);
        moreCleanData.setOnClickListener(ldelete);
        //获取最新版本
        RelativeLayout getNewVersion = (RelativeLayout) this.findViewById(R.id.Layout_more_update_version);
        getNewVersion.setOnClickListener(lgetNewVersion);
        //免责声明
        RelativeLayout disclaimer = (RelativeLayout) this.findViewById(R.id.Layout_more_disclaimer);
        disclaimer.setOnClickListener(ldisclaimer);
        //免责声明
        RelativeLayout contactUs = (RelativeLayout) this.findViewById(R.id.Layout_more_contact_us);
        contactUs.setOnClickListener(lcontactUs);
        // 意见反馈
        RelativeLayout feedbackUs = (RelativeLayout) this.findViewById(R.id.Layout_more_feedback_to_us);
        feedbackUs.setOnClickListener(lfeedbackUs);
        // 关于我们
        RelativeLayout aboutUs = (RelativeLayout) this.findViewById(R.id.Layout_more_about_us);
        aboutUs.setOnClickListener(laboutUs);
        
    }
}
