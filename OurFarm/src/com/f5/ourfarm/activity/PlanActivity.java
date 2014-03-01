package com.f5.ourfarm.activity;

import static com.f5.ourfarm.util.URLConstants.PLAN_URL;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.f5.ourfarm.R;
import com.f5.ourfarm.activity.AroundFarmActivity.RunnableImp;
import com.f5.ourfarm.layout.PullToRefreshView;
import com.f5.ourfarm.layout.PullToRefreshView.OnFooterRefreshListener;
import com.f5.ourfarm.model.LoadWay;
import com.f5.ourfarm.model.Summary;
import com.f5.ourfarm.util.Constants;
import com.f5.ourfarm.util.HttpUtil;
import com.f5.ourfarm.util.Tools;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.umeng.analytics.MobclickAgent;

/**
 * �ƻ��б�
 * 
 * @author lify
 *
 */
public class PlanActivity extends BaseActivity implements OnFooterRefreshListener{
    OnClickListener details = null;
    OnClickListener showMap = null;
    
    //loadingbar
    private LinearLayout loadingLayout;
    //��Ҫ��Ϣ
    private LinearLayout summaryLayout;
    //����ˢ��
    PullToRefreshView mPullToRefreshView;
    
    //�ƻ������б�����
    private List<Summary> planList = new ArrayList<Summary>();
    //��¼�ڼ�������ÿ����һ�Σ�����10����¼
    private int queryCounts = 1;
    
  	private static String TAG = "�ƻ��б�ҳ��";
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); 
        setContentView(R.layout.activity_plan);
        
        //׼��listeners
        this.prepareListeners();
        initStatus();
        //����listeners
        this.batchSetListeners();
        
        new Thread(new RunnableImp(LoadWay.INIT_LOAD)).start();
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
        //��ʼ������ʾ�����ǵ�һ��
        queryCounts = 1;
        //����titleֵ
        TextView planName = (TextView) this.findViewById(R.id.TextView_toppanel_title);
        planName.setText(getIntent().getExtras().getString("planName"));
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
        //details
        details = new OnClickListener(){
            public void onClick(View v) {
            	// �ж������Ƿ���ã�������������ʾ�û�������������ת
            	if(!Tools.checkNetworkStatus(PlanActivity.this)) {
            		return;
            	}
                long destinationId =  (Long) v.getTag(); 
                Log.d("res", ""+destinationId);
                Intent nearby2detail = new Intent(PlanActivity.this,DetailActivity.class);
                //���������������ݱ�����Intent��ȷ�����ݿ���activity�䴫��
                nearby2detail.putExtra("destinationId", destinationId);
                //�����жϵ��������������
                nearby2detail.putExtra("planType", getIntent().getExtras().getInt("planType"));
                
                startActivity(nearby2detail);
            }
        };
        //map
        showMap = new OnClickListener(){
            public void onClick(View v) {
            	// �ж������Ƿ���ã�������������ʾ�û�������������ת
            	if(!Tools.checkNetworkStatus(PlanActivity.this)) {
            		return;
            	}
            	
            	//Ϊ�˸���ͼ��ʾ��,����ԭ������������
                HashMap<Long, Summary> lds = new HashMap<Long ,Summary>();
                if(planList != null){
	                for(Summary summary : planList) {
	                	lds.put(summary.getDestinationId(), summary);
	                }
                }
                
            	//��Ҫ��ʾ�����ݴ�����ͼҳ��
                Intent toMapIntent = new Intent(PlanActivity.this, NearbyMapActivity.class);
                toMapIntent.putExtra(Constants.MAP_SHOW_SPOT, lds);
                toMapIntent.putExtra(Constants.MAP_SHOW_DISTANCE, 50);//ѡ��ľ��룬�����жϵ�ͼ����
                startActivity(toMapIntent);
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
        //map
        ImageView i2map = (ImageView) this.findViewById(R.id.ImageView_button_map);
        i2map.setOnClickListener(showMap);
        
        mPullToRefreshView.setOnFooterRefreshListener(this);
    }
    
    /**
     * ���츽���б�
     * ���ݲ�ѯ�������Ҹ������ݣ�Ȼ�����ݸ�Ҫ��Ϣ��ʾ������
     */
    private void showList(List<Summary> summaryList) {
        LayoutInflater flater = LayoutInflater.from(this);
        LinearLayout list = (LinearLayout) this
                .findViewById(R.id.ListView_main);
        
        // ��ȡ��ѯ�����ѭ�������Ҫ�б�
        for(Summary summary: summaryList){
            Long destinationId = summary.getDestinationId();
            View v = flater.inflate(R.layout.listview_child_plan, null);
            // pic
            ImageView ivPic = (ImageView) v.findViewById(R.id.ImageView_plan_pic);
            Bitmap bm = Tools.getBitmapFromUrl(summary.getPic());
            if(bm != null){
                ivPic.setImageBitmap(bm);
            }
            
            // name
            TextView tvName = (TextView) v.findViewById(R.id.ListView_plan_name);
            tvName.setText(summary.getName());
            // sroce
            RatingBar rbSroce = (RatingBar) v.findViewById(R.id.ratingBar_plan_sroce);
            rbSroce.setRating(summary.getScore());
            // price info
            TextView tvPrice = (TextView) v.findViewById(R.id.ListView_plan_price);
            tvPrice.setText(summary.getPriceInfo());
            // Characteristic
            TextView tvCharacteristic = (TextView) v.findViewById(R.id.ListView_plan_characteristic);
            tvCharacteristic.setText("��ɫ:" + summary.getCharacteristic());
            // add view
            list.addView(v);
            // ���ü���
            v.setTag(destinationId);
            v.setOnClickListener(details);
        }
        list.invalidate();
    }

    Handler handler4Plan = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String res = data.getString("value");
            Log.i("TAG","�ƻ�����������Ϊ-->" + res);
            
            //��̨�߳���ɺ������ʾ���
            showList(planList);
            ++queryCounts;
            
            requestDataSuccess();
        }
    };
    
    Runnable runnable4plan = new RunnableImp(LoadWay.INIT_LOAD);
    /**
     * Runnableʵ���࣬�������󾰵�list
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
            
            String errMsg = "��ȡ�����Ҫ��Ϣ����";
            String res = null;
            try {
                res = HttpUtil.postUrl(PLAN_URL, getParameters());
                try {
    				Gson gson = new Gson();
    				planList = gson.fromJson(res, 
    	            		new TypeToken<List<Summary>>() {}.getType());
    			} catch (JsonSyntaxException e) {
    				Log.e(TAG, "������ȡ���мƻ���Ϣ��ʧ��", e);
    				Tools.showToastLong(PlanActivity.this, "���ݼ���ʧ�ܡ�");
                    return;
    			}
    			
    			//û���ҵ�ũ����ʱ
    			if(planList == null || planList.size() <= 0) {
    				Tools.showToastLong(PlanActivity.this, "û�и������Ϣ�ˡ�");
                	requestDataSuccess();
                	return;
    			} 
    			
            } catch (ClientProtocolException e1) {
                Log.e(TAG, errMsg, e1);
            } catch (IOException e1) {
                Log.e(TAG, errMsg, e1);
            } catch (Exception e1) {
            	Log.e(TAG, errMsg, e1);
            }
            
            handler4Plan.sendMessage(msg);
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
        //ѡ��ƻ�����
        Bundle extras = getIntent().getExtras();
        int planType = extras.getInt("planType");
        //�ڼ�������
        BasicNameValuePair count = new BasicNameValuePair("count", String.valueOf(queryCounts));
        param.add(count);
        //ѡ������
        BasicNameValuePair type = new BasicNameValuePair("type", String.valueOf(planType));
        param.add(type);
        
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
