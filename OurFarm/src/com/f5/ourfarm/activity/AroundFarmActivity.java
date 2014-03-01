package com.f5.ourfarm.activity;

import static com.f5.ourfarm.util.URLConstants.GET_AROUND_FARM_HOME;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.f5.ourfarm.R;
import com.f5.ourfarm.layout.PullToRefreshView;
import com.f5.ourfarm.layout.PullToRefreshView.OnFooterRefreshListener;
import com.f5.ourfarm.model.LoadWay;
import com.f5.ourfarm.model.MoreAroundFarm;
import com.f5.ourfarm.model.Summary;
import com.f5.ourfarm.util.Constants;
import com.f5.ourfarm.util.HttpUtil;
import com.f5.ourfarm.util.Tools;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.umeng.analytics.MobclickAgent;

/**
 * @author lify
 */
public class AroundFarmActivity extends Activity implements OnFooterRefreshListener{
    OnClickListener lback = null;
    OnClickListener details = null;
    
    //loadingbar
    private LinearLayout loadingLayout;
    //��Ҫ��Ϣ
    private LinearLayout summaryLayout;
    //����ˢ��
    PullToRefreshView mPullToRefreshView;
    
    //�����ܱߵ�ũ����
    private List<MoreAroundFarm> farmList = new ArrayList<MoreAroundFarm>();
    
    //��¼�ڼ�������ÿ����һ�Σ�����10����¼
    private int queryCounts = 1;
    
  	private static String TAG = "�ܱ�ũ����ҳ��";
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); 
        // ȥ��������
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //׼��listeners
        this.prepareListeners();
        setContentView(R.layout.activity_around_farm);
        
        initStatus();
        
        //����listeners
        this.batchSetListeners();
    }
    
    /**
     * ��ʼҳ��ʱ�Ŀؼ�״̬�趨
     */
    private void initStatus() {
        mPullToRefreshView = (PullToRefreshView) findViewById(R.id.main_pull_refresh_view);
        //��������ҳ��
        mPullToRefreshView.lock();
        
        //loadingbar
        loadingLayout = (LinearLayout)findViewById(R.id.nearby_loadingbar);
        //��Ҫ��Ϣ
        summaryLayout = (LinearLayout)findViewById(R.id.ListView_main);
        
        //������ʾ���
        loadingLayout.setVisibility(View.VISIBLE);
        summaryLayout.setVisibility(View.GONE);
        
        //��ȡ���󵽵��ܱ�ũ����
        farmList = (List<MoreAroundFarm>)getIntent().getSerializableExtra(Constants.AROUND_FARM);
        if(farmList != null && farmList.size() > 0) {
        	//��ʼ������ʾ�����ǵ�2��
            queryCounts = 2;
        	showList(farmList);
        	requestDataSuccess();
        } else {
        	queryCounts = 1;
        	new Thread(new RunnableImp(LoadWay.INIT_LOAD)).start();
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
    
    /**
     * �������¼���Ķ�����
     */
    private void prepareListeners() {
        //nearby->home
        lback = new OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        };
        //details
        details = new OnClickListener(){
            public void onClick(View v) {
            	// �ж������Ƿ���ã�������������ʾ�û�������������ת
            	if(!Tools.checkNetworkStatus(AroundFarmActivity.this)) {
            		return;
            	}
                long destinationId =  (Long) v.getTag(); 
                Log.i("res", ""+destinationId);
                Intent nearby2detail = new Intent(AroundFarmActivity.this,DetailActivity.class);
                //���������������ݱ�����Intent��ȷ�����ݿ���activity�䴫��
                nearby2detail.putExtra("destinationId", destinationId);
                //�����жϵ��������������
                nearby2detail.putExtra("nearbyType", 2);
                
                startActivity(nearby2detail);
            } 
        };
    }
    
    /**
     * ��view�ͼ���
     */
    private void batchSetListeners() {
        // back
        ImageView iback2home = (ImageView) this.findViewById(R.id.ImageView_button_back);
        iback2home.setOnClickListener(lback);
        
        mPullToRefreshView.setOnFooterRefreshListener(this);
    }
    
    
    /**
     * ���츽���б�
     * ���ݲ�ѯ�������Ҹ������ݣ�Ȼ�����ݸ�Ҫ��Ϣ��ʾ������
     */
    private void showList(List<MoreAroundFarm> aroundFarm) {
        LayoutInflater flater = LayoutInflater.from(this);
        LinearLayout list = (LinearLayout) this.findViewById(R.id.ListView_main);
//        list.removeAllViews();
        
        // ��ȡ��ѯ�����ѭ�������Ҫ�б�
        for(MoreAroundFarm maf: aroundFarm){
            Long destinationId = maf.getView_id();
            Summary ds = maf.getSummary();
            View v = flater.inflate(R.layout.listview_child_nearby, null);
            // pic
            ImageView ivPic = (ImageView) v.findViewById(R.id.ImageView_destination_pic);
            Bitmap bm = Tools.getBitmapFromUrl(ds.getPic());
            if(bm != null){
                ivPic.setImageBitmap(bm);
            }
            // name
            TextView tvName = (TextView) v
                    .findViewById(R.id.ListView_destination_name);
            tvName.setText(ds.getName());
            // sroce
            RatingBar rbSroce = (RatingBar) v
                    .findViewById(R.id.ratingBar_destination_sroce);
            rbSroce.setRating(ds.getScore());
            // price info
            TextView tvPrice = (TextView) v
                    .findViewById(R.id.ListView_destination_price);
            tvPrice.setText(ds.getPriceInfo());
            // hot
            TextView tvHot = (TextView) v
                    .findViewById(R.id.ListView_destination_hot);
            tvHot.setText("����ָ��:" + ds.getHot());
            // Characteristic
            TextView tvCharacteristic = (TextView) v
                    .findViewById(R.id.ListView_destination_characteristic);
            tvCharacteristic.setText("��ɫ:" + ds.getCharacteristic());
            // distance
            TextView tvDistance = (TextView) v
                    .findViewById(R.id.ListView_destination_distance);
            tvDistance.setText(String.valueOf(maf.getSummary().getDistance()));
            // add view
            list.addView(v);
            // ���ü���
            v.setTag(destinationId);
            v.setOnClickListener(details);
        
        }
        list.invalidate();
    }

    Handler handler4nearby = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String res = data.getString("value");
            Log.d(TAG,"�����ܱ�ũ���ֽ��Ϊ-->" + res);
            
        	//��̨�߳���ɺ������ʾ���
            showList(farmList);
            ++queryCounts;
            
            requestDataSuccess();
        }
    };
    
    Runnable runnable4nearby = new RunnableImp(LoadWay.INIT_LOAD);
    /**
     * Runnableʵ���࣬���������ܱ�ũ����list
     */
    class RunnableImp implements Runnable {
        //���ݼ��ط�ʽ��1����ʼ����, 2��ѡ��spinner��ʽ, 3��ѡ���������ط�ʽ
        LoadWay loadWay;
        RunnableImp(LoadWay loadWay) {
            this.loadWay = loadWay;
        }
        
        @Override
        public void run() {
        	Message msg = new Message();
            Bundle data = new Bundle();
            data.putString("value","ok");
            msg.setData(data);
            
            String Tag = "NearbyService";
            String errMsg = "��ȡ�����Ҫ��Ϣ����";
            String res4MoreAroundFarm = null;
            try {
            	res4MoreAroundFarm = HttpUtil.postUrl(GET_AROUND_FARM_HOME, getParameters());
                
    			try {
    				Gson gson = new Gson();
    				farmList = gson.fromJson(res4MoreAroundFarm, 
    	            		new TypeToken<List<MoreAroundFarm>>() {}.getType());
    			} catch (JsonSyntaxException e) {
    				Log.e(TAG, "������ȡ�ܱ�ũ������Ϣ��ʧ��", e);
    				Tools.showToastLong(AroundFarmActivity.this, "���ݼ���ʧ�ܡ�");
                    return;
    			}
    			
    			//û���ҵ�ũ����ʱ
    			if(farmList == null || farmList.size() <= 0) {
    				Tools.showToastLong(AroundFarmActivity.this, "û�и����ũ�����ˡ�");
                	requestDataSuccess();
                	return;
    			} 
                
            } catch (ClientProtocolException e1) {
                Log.e(Tag, errMsg, e1);
            } catch (IOException e1) {
                Log.e(Tag, errMsg, e1);
            } catch (Exception e1) {
            	Log.e(Tag, errMsg, e1);
            }
            
            handler4nearby.sendMessage(msg);
        }
    }
    
    /**
     * ���󾰵��б�ʱ������Ĳ���
     * 
     * @param defaultDistance �����ľ���
     * @return �������������list
     */
    private List<NameValuePair> getParameters() {
        List<NameValuePair> param = new ArrayList<NameValuePair>();
        
        //����ID
        BasicNameValuePair view_id = new BasicNameValuePair("view_id", String.valueOf(farmList.get(0).getView_id()));
        param.add(view_id);
        //�ڼ�������
        BasicNameValuePair count = new BasicNameValuePair("count", String.valueOf(queryCounts));
        param.add(count);
        
        return param;
    }

    /**
     * ����ˢ�´���
     */
	@Override
	public void onFooterRefresh(PullToRefreshView view) {
		Log.d("�������", queryCounts + "");
		new Thread(new RunnableImp(LoadWay.POLLUP_LOAD)).start();
	}
	
	/**
	 * ��������ɹ�ʱ�������ʾ
	 */
	private void requestDataSuccess() {
		//�Ϸ�ԲȦ��������ʧ
        if (loadingLayout != null) {
             loadingLayout.setVisibility(View.GONE);
        }
        
    	summaryLayout.setVisibility(View.VISIBLE);
        //������ɼ���
        mPullToRefreshView.onFooterRefreshComplete();
        //��������ˢ��
        mPullToRefreshView.unlock();
	}
	
}
