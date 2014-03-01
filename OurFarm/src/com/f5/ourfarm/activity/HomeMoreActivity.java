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
 * ����ҳ��
 * 
 * @author tianhao
 *
 */
public class HomeMoreActivity extends BaseActivity {
    
    private static final String TAG = "����ҳ��:";
    private static final String errMsg = "����ҳ�����ʧ��";
    
    private  UpdateService updateService;
    
    private Context mContext = HomeMoreActivity.this; 
    
    //���ݿ�����
    DestinationDbAdpter destinationDbAdpter;

    //������ػ���
    OnClickListener ldelete;
    //���±�������
    OnClickListener lupdate;
    //��ȡ���°汾
    OnClickListener lgetNewVersion;
    //��������
    OnClickListener ldisclaimer;
    //��ϵ����
    OnClickListener lcontactUs;
    //�������
    OnClickListener lfeedbackUs;
    //��������
    OnClickListener laboutUs;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // ȥ��������
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main_more);
        
        //׼��listeners
        this.prepareListeners();
        
        //���õײ��л���ɫ
        ImageView ivMore = (ImageView) this.findViewById(R.id.ImageView_home_more);
        ivMore.setImageResource(R.drawable.home_more_active);
        
        //����listeners
        this.batchSetListeners();
        
        //���ݿ��ʼ��
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
    
  //������Ҫ�õ�ServiceConnection��Context.bindService��context.unBindService()���õ�  
    private ServiceConnection mServiceConnection = new ServiceConnection() {  
        //����bindServiceʱ����TextView��ʾMyService��getSystemTime()�����ķ���ֵ   
        public void onServiceConnected(ComponentName name, IBinder service) {  
            // TODO Auto-generated method stub  
            updateService = ((UpdateService.MyBinder)service).getService(); 
        }  
          
        public void onServiceDisconnected(ComponentName name) {  
            // TODO Auto-generated method stub  
              
        }
 
    };

    /**
     * �������¼���Ķ�����
     */
    private void prepareListeners() {
      
        // ��ջ���
        ldelete = new OnClickListener() {
            public void onClick(View v) {
                destinationDbAdpter.deleteCache();
                Tools.showToastLong(HomeMoreActivity.this, Constants.MORE_CLEAN_DATA);
            }
        };
        // ���±�������
        lupdate = new OnClickListener() {
            public void onClick(View v) {
                Tools.showToastLong(HomeMoreActivity.this, "ϵͳ����Ϊ���������ݣ�����Ľ�������������");
                // TODO ���£��ֽ׶θ��¼�get clsssic 
                Intent i  = new Intent();  
                i.setClass(HomeMoreActivity.this, UpdateService.class); 
                mContext.startService(i);
            }
        };
        //��ȡ���°汾
        lgetNewVersion = new OnClickListener() {
            public void onClick(View v) {
            	//TODO �汾���»�û��ʵ�֣���ʱ����ʾ����
            	Tools.showToastLong(HomeMoreActivity.this, "����ǰ�Ѿ������°汾�ˡ�");
            }
        };
        //��������
        ldisclaimer = new OnClickListener() {
            public void onClick(View v) {
            	new Tip(HomeMoreActivity.this).show();
            }
        };
        //��ϵ����
        lcontactUs = new OnClickListener() {
            public void onClick(View v) {
            	new Tip(HomeMoreActivity.this,1).show();
            }
        };
        //�������
        lfeedbackUs = new OnClickListener() {
            public void onClick(View v) {
            	//���������ҳ��
            	Intent i2feedback = new Intent(getApplicationContext(), MoreFeedbackActivity.class);
                startActivity(i2feedback);
            }
        };
        //��������
        laboutUs = new OnClickListener() {
        	public void onClick(View v) {
        		//�ػص�����ҳ
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
     * ��view�ͼ���
     */
    private void batchSetListeners() {
        //�ײ����4����ť
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
        
        // ���±�������
        RelativeLayout moreUpdateData = (RelativeLayout) this.findViewById(R.id.Layout_more_update_data);
        moreUpdateData.setOnClickListener(lupdate);
        // ��ջ���
        RelativeLayout moreCleanData = (RelativeLayout) this.findViewById(R.id.Layout_more_clear_data);
        moreCleanData.setOnClickListener(ldelete);
        //��ȡ���°汾
        RelativeLayout getNewVersion = (RelativeLayout) this.findViewById(R.id.Layout_more_update_version);
        getNewVersion.setOnClickListener(lgetNewVersion);
        //��������
        RelativeLayout disclaimer = (RelativeLayout) this.findViewById(R.id.Layout_more_disclaimer);
        disclaimer.setOnClickListener(ldisclaimer);
        //��������
        RelativeLayout contactUs = (RelativeLayout) this.findViewById(R.id.Layout_more_contact_us);
        contactUs.setOnClickListener(lcontactUs);
        // �������
        RelativeLayout feedbackUs = (RelativeLayout) this.findViewById(R.id.Layout_more_feedback_to_us);
        feedbackUs.setOnClickListener(lfeedbackUs);
        // ��������
        RelativeLayout aboutUs = (RelativeLayout) this.findViewById(R.id.Layout_more_about_us);
        aboutUs.setOnClickListener(laboutUs);
        
    }
}
