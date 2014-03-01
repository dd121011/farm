package com.f5.ourfarm.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.f5.ourfarm.R;
import com.f5.ourfarm.util.Tools;
import com.umeng.analytics.MobclickAgent;


/**
 * ũ��Ʒ���ҳ��
 * 
 * @author lify
 */
public class FarmProduceActivity extends BaseActivity {
	//���ũ��Ʒͼ��
	OnClickListener farmProduce = null;
	
    private static final String TAG = "ũ��Ʒ������ҳ";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farm_produce);
        //׼��listeners
        this.prepareListeners();
        
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
    private void prepareListeners(){
    	
        //����ũ��Ʒ
        farmProduce = new OnClickListener() {
            public void onClick(View v) {
            	if(!Tools.checkNetworkStatus(FarmProduceActivity.this)) {
            		return;
            	}
        	    
                int tag = (Integer) v.getTag(); 
                Intent home2play = new Intent(FarmProduceActivity.this, NearbyActivity.class);
                home2play.putExtra("nearbyType", 4);
                home2play.putExtra("typeValue", tag);
                startActivity(home2play);
            }
        };
    }
    
    /**
     *  ��view�ͼ���
     */
    private void batchSetListeners() {
    	//���˰�ť
    	ImageView iback = (ImageView) this
				.findViewById(R.id.ImageView_button_back);
		iback.setOnClickListener(lback);
		
        //��ժ
        ImageView pick = (ImageView) findViewById(R.id.ImageView_farm_produce_pick);
        pick.setTag(41);
        pick.setOnClickListener(farmProduce);
        //�м�
        ImageView market = (ImageView) findViewById(R.id.ImageView_farm_produce_market);
        market.setTag(42);
        market.setOnClickListener(farmProduce);
        //����
        ImageView vegetable = (ImageView) findViewById(R.id.ImageView_farm_produce_vegetable);
        vegetable.setTag(43);
        vegetable.setOnClickListener(farmProduce);
        //����
        ImageView egg = (ImageView) findViewById(R.id.ImageView_farm_produce_egg);
        egg.setTag(44);
        egg.setOnClickListener(farmProduce);
        //��
        ImageView fish = (ImageView) findViewById(R.id.ImageView_farm_produce_fish);
        fish.setTag(45);
        fish.setOnClickListener(farmProduce);
        //����
        ImageView duck = (ImageView) findViewById(R.id.ImageView_farm_produce_duck);
        duck.setTag(46);
        duck.setOnClickListener(farmProduce);
    }
}
