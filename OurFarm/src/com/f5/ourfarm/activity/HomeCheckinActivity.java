package com.f5.ourfarm.activity;

import com.f5.ourfarm.R;
import com.f5.ourfarm.model.Destination;
import com.f5.ourfarm.model.Summary;
import com.f5.ourfarm.sqlite.DestinationDbAdpter;
import com.umeng.analytics.MobclickAgent;



import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;


/**
 * @author tianhao
 *
 */
public class HomeCheckinActivity extends Activity {
    
    
  //监听底部面板4个按钮
    OnClickListener lPanelBottom = null;
    
   

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 去除标题栏
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //准备listeners
        this.prepareListeners();
        setContentView(R.layout.activity_main_checkin);
        //设置标题
        TextView tvTitle = (TextView) this.findViewById(R.id.TextView_home_title);
        tvTitle.setText(R.string.common_checkin);
        //设置底部切换颜色
        ImageView ivCheckin = (ImageView) this.findViewById(R.id.ImageView_home_checkin);
        ivCheckin.setImageResource(R.drawable.home_checkin_active);
        //设置listeners
        this.batchSetListeners();
        
      
    }
    
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    /**
     * 监听到事件后的动作；
     */
    private void prepareListeners() {
      
      //底部面板4个按钮间切换
        lPanelBottom =  new OnClickListener() {
            public void onClick(View v) {
                //
                int tag = (Integer) v.getTag(); 
                switch (tag ){
                case 1 :
                Intent i2home = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(i2home);
                finish();
                break;
                case 2 :
                Intent i2checkin = new Intent(getApplicationContext(),HomeCheckinActivity.class);
                startActivity(i2checkin);
                finish();
                break;
                case 3:
                Intent i2my = new Intent(getApplicationContext(),HomeMyActivity.class);
                startActivity(i2my);
                finish();
                break;
                case 4:
                Intent i2more = new Intent(getApplicationContext(),HomeMoreActivity.class);
                startActivity(i2more);
                finish();
                break;
                }
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
    }

   
    
}
