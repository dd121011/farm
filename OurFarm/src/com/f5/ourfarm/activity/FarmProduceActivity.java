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
 * 农产品类别页面
 * 
 * @author lify
 */
public class FarmProduceActivity extends BaseActivity {
	//点击农产品图标
	OnClickListener farmProduce = null;
	
    private static final String TAG = "农产品分类首页";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farm_produce);
        //准备listeners
        this.prepareListeners();
        
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
    private void prepareListeners(){
    	
        //查找农产品
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
     *  绑定view和监听
     */
    private void batchSetListeners() {
    	//后退按钮
    	ImageView iback = (ImageView) this
				.findViewById(R.id.ImageView_button_back);
		iback.setOnClickListener(lback);
		
        //采摘
        ImageView pick = (ImageView) findViewById(R.id.ImageView_farm_produce_pick);
        pick.setTag(41);
        pick.setOnClickListener(farmProduce);
        //市集
        ImageView market = (ImageView) findViewById(R.id.ImageView_farm_produce_market);
        market.setTag(42);
        market.setOnClickListener(farmProduce);
        //谷疏
        ImageView vegetable = (ImageView) findViewById(R.id.ImageView_farm_produce_vegetable);
        vegetable.setTag(43);
        vegetable.setOnClickListener(farmProduce);
        //鸡蛋
        ImageView egg = (ImageView) findViewById(R.id.ImageView_farm_produce_egg);
        egg.setTag(44);
        egg.setOnClickListener(farmProduce);
        //鱼
        ImageView fish = (ImageView) findViewById(R.id.ImageView_farm_produce_fish);
        fish.setTag(45);
        fish.setOnClickListener(farmProduce);
        //禽类
        ImageView duck = (ImageView) findViewById(R.id.ImageView_farm_produce_duck);
        duck.setTag(46);
        duck.setOnClickListener(farmProduce);
    }
}
