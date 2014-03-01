package com.f5.ourfarm.activity;

import com.f5.ourfarm.R;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class DetailFavoritesActivity extends Activity {
    
    OnClickListener lback = null;
    //�����ײ����4����ť
    OnClickListener lPanelBottom = null;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
     // ȥ��������
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //׼��listeners
        this.prepareListeners();
        setContentView(R.layout.activity_destination_detail_frame);
        //���ñ���
        TextView tvTitle = (TextView) this.findViewById(R.id.TextView_toppanel_title);
        tvTitle.setText(R.string.detail_favorites);
      //���ĵײ���������ɫ
        TextView tvname = (TextView) this.findViewById(R.id.TextView_panelBottom_detail_favorites);
        tvname.setTextColor(getResources().getColor(R.color.background_green));
      //����listeners
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
     * �������¼���Ķ�����
     */
    private void prepareListeners() {
        // nearby->home
        lback = new OnClickListener() {
            public void onClick(View v) {
                // TODO back
                finish();
            }
        };
      //�ײ����4����ť���л�
        lPanelBottom = new OnClickListener() {
            public void onClick(View v) {
                //
                int tag = (Integer) v.getTag();
                switch (tag) {
                case 1:
                    Intent i2home = new Intent(getApplicationContext(),
                            DetailFavoritesActivity.class);
                    startActivity(i2home);
                    break;
                case 2:
                    Intent i2checkin = new Intent(getApplicationContext(),
                            DetailCheckinActivity.class);
                    startActivity(i2checkin);
                    break;
                case 3:
                    Intent i2my = new Intent(getApplicationContext(),
                            DetailCorrectionActivity.class);
                    startActivity(i2my);
                    break;
                case 4:
                    Intent i2more = new Intent(getApplicationContext(),
                            DetailShareActivity.class);
                    startActivity(i2more);
                }
            }
        };
    }
    
    /**
     * ��view�ͼ���
     */
    private void batchSetListeners() {
        ImageView iback2home = (ImageView) this.findViewById(R.id.ImageView_button_back);
        iback2home.setOnClickListener(lback);
        
        //�ײ����4����ť
        TextView tvfavorites= (TextView) this.findViewById(R.id.TextView_panelBottom_detail_favorites);
        tvfavorites.setTag(1);
        tvfavorites.setOnClickListener(lPanelBottom);
        
        TextView tvcheckin= (TextView) this.findViewById(R.id.TextView_panelBottom_detail_checkin);
        tvcheckin.setTag(2);
        tvcheckin.setOnClickListener(lPanelBottom);
        
        TextView tvcorrection= (TextView) this.findViewById(R.id.TextView_panelBottom_detail_correction);
        tvcorrection.setTag(3);
        tvcorrection.setOnClickListener(lPanelBottom);
        
        TextView tvshare= (TextView) this.findViewById(R.id.TextView_panelBottom_detail_share);
        tvshare.setTag(4);
        tvshare.setOnClickListener(lPanelBottom);
    }
    
}


