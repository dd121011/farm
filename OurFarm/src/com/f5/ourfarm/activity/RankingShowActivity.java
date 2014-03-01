package com.f5.ourfarm.activity;

import static com.f5.ourfarm.util.URLConstants.GET_TOP;

import java.io.IOException;
import java.util.ArrayList;
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
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.f5.ourfarm.R;
import com.f5.ourfarm.layout.PullToRefreshView;
import com.f5.ourfarm.layout.PullToRefreshView.OnFooterRefreshListener;
import com.f5.ourfarm.model.DestinationType;
import com.f5.ourfarm.model.LoadWay;
import com.f5.ourfarm.model.RankingParams;
import com.f5.ourfarm.model.Summary;
import com.f5.ourfarm.util.HttpUtil;
import com.f5.ourfarm.util.Tools;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.umeng.analytics.MobclickAgent;

/**
 * ��ʾ����ҳ��
 * 
 * @author lify
 */
public class RankingShowActivity extends BaseActivity implements OnFooterRefreshListener{
	
    OnClickListener details = null;
    
    //�л���ͬ���͵�����
    OnClickListener rankingChange = null;
    
    //loadingbar
    private LinearLayout loadingLayout;
    //��Ҫ��Ϣ
    private LinearLayout summaryLayout;
    //����ˢ��
    PullToRefreshView mPullToRefreshView;
    
    private TextView rankingTitle;
    
    //�������List
    private List<Summary> rankingList = new ArrayList<Summary>();
    
    //��¼�ڼ�������ÿ����һ�Σ�����10����¼
    private int queryCounts = 1;
    
    //����������ʾ������
    private List<String> groups = DestinationType.getGroupList();
    
    //����,�ƻ�����
    private int type = 1;
    //��������
    private int type_value = 1;
    
  	private static String TAG = "����ҳ��";
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); 
        // ȥ��������
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_ranking);
        
        //׼��listeners
        this.prepareListeners();
        //����listeners
        this.batchSetListeners();
        initStatus();
        
    }
    
    /**
     * ��ʼҳ��ʱ�Ŀؼ�״̬�趨
     */
    private void initStatus() {
    	//Titie����Ĭ��Ϊ����
    	rankingTitle.setText("����");
        //��������ҳ��
        mPullToRefreshView.lock();
        
        //loadingbar
        loadingLayout = (LinearLayout)findViewById(R.id.nearby_loadingbar);
        //��Ҫ��Ϣ
        summaryLayout = (LinearLayout)findViewById(R.id.ListView_main);
        
        //������ʾ���
        loadingLayout.setVisibility(View.VISIBLE);
        summaryLayout.setVisibility(View.GONE);
        
        queryCounts = 1;
    	new Thread(new RunnableImp(LoadWay.INIT_LOAD)).start();
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
    	
    	//�л���ͬ���͵�����
    	rankingChange = new OnClickListener(){
            public void onClick(View v) {
            	showPopupWindow(v, groups, new OnItemClickListener() {  
          		  
                    @Override  
                    public void onItemClick(AdapterView<?> adapterView, View view,  
                            int position, long id) {  
                    	//ѡ�����������
                    	RankingParams rankingParams = DestinationType.getKeyFromTypeName(groups.get(position));
                    	if(rankingParams == null) return;
                    	
                    	type = rankingParams.getType();
                    	type_value = rankingParams.getType_value();
                    	//Titie����Ĭ��Ϊ����
                    	rankingTitle.setText(rankingParams.getName());
                    	
                    	//��������ҳ��
                        mPullToRefreshView.lock();
                        //������ʾ���
                        loadingLayout.setVisibility(View.VISIBLE);
                        summaryLayout.setVisibility(View.GONE);
                    	queryCounts = 1;
                    	//���ԭ���ļ�¼
                    	LinearLayout list = (LinearLayout) RankingShowActivity.this.findViewById(R.id.ListView_main);
                    	list.removeAllViews();
                    	new Thread(new RunnableImp(LoadWay.INIT_LOAD)).start();
                    	
                        if (popupWindow != null) {  
                            popupWindow.dismiss();  
                        }  
                    }  
                });
            }
        };
        
        //details
        details = new OnClickListener(){
            public void onClick(View v) {
            	// �ж������Ƿ���ã�������������ʾ�û�������������ת
            	if(!Tools.checkNetworkStatus(RankingShowActivity.this)) {
            		return;
            	}
                long destinationId =  (Long)v.getTag(); 
                
                //����ɽׯ
                if(type == 1 && type_value == 3) {
                	Intent nearby2mountainVilla = new Intent(RankingShowActivity.this,MountainVillaActivity.class);
                	//���������������ݱ�����Intent��ȷ�����ݿ���activity�䴫��
                	nearby2mountainVilla.putExtra("destinationId", destinationId);
                	
                	startActivity(nearby2mountainVilla);
                } else {//��������
                	Intent nearby2detail = new Intent(RankingShowActivity.this,DetailActivity.class);
                	//���������������ݱ�����Intent��ȷ�����ݿ���activity�䴫��
                	nearby2detail.putExtra("destinationId", destinationId);
                	//�����жϵ��������������
                	nearby2detail.putExtra("nearbyType", type_value);
                	
                	startActivity(nearby2detail);
                }
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
        
        //ҳ��ͷ����title
        rankingTitle = (TextView) this.findViewById(R.id.TextView_ranking_title);
        rankingTitle.setOnClickListener(rankingChange);
        
        mPullToRefreshView = (PullToRefreshView) findViewById(R.id.main_pull_refresh_view);
        mPullToRefreshView.setOnFooterRefreshListener(this);
        
    }
    
    
    /**
     * ���������б�
     * ���ݲ�ѯ�����������ݲ���ʾ������
     */
    private void showList(List<Summary> summaryList) {
        
        LayoutInflater flater = LayoutInflater.from(this);
        LinearLayout list = (LinearLayout) this.findViewById(R.id.ListView_main);
        
        // ��ȡ��ѯ�����ѭ�������Ҫ�б�
        for(Summary sum: summaryList){
            Long destinationId = sum.getDestinationId();
            View v = flater.inflate(R.layout.listview_child_plan, null);
            // pic
            ImageView ivPic = (ImageView) v.findViewById(R.id.ImageView_plan_pic);
            Bitmap bm = Tools.getBitmapFromUrl(sum.getPic());
            if(bm != null){
                ivPic.setImageBitmap(bm);
            }
            // name
            TextView tvName = (TextView) v
                    .findViewById(R.id.ListView_plan_name);
            tvName.setText(sum.getName());
            // sroce
            RatingBar rbSroce = (RatingBar) v
                    .findViewById(R.id.ratingBar_plan_sroce);
            rbSroce.setRating(sum.getScore());
            // price info
            TextView tvPrice = (TextView) v
                    .findViewById(R.id.ListView_plan_price);
            tvPrice.setText(sum.getPriceInfo());
            // Characteristic
            TextView tvCharacteristic = (TextView) v
                    .findViewById(R.id.ListView_plan_characteristic);
            tvCharacteristic.setText("��ɫ:" + sum.getCharacteristic());

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
            Log.d(TAG,"�������۽��Ϊ-->" + res);
            
        	//��̨�߳���ɺ������ʾ���
            showList(rankingList);
            ++queryCounts;
            
            requestDataSuccess();
        }
    };
    
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
            
            String errMsg = "��ȡ������Ϣ����";
            String res4Ranking = null;
            try {
            	res4Ranking = HttpUtil.postUrl(GET_TOP, getParameters());
    			try {
    				Gson gson = new Gson();
    				rankingList = gson.fromJson(res4Ranking, new TypeToken<List<Summary>>() {}.getType());
    			} catch (JsonSyntaxException e) {
    				Log.e(TAG, "������ȡ������Ϣʧ��", e);
    				Tools.showToastLong(RankingShowActivity.this, "���ݼ���ʧ�ܡ�");
                    return;
    			}
    			
    			//û���ҵ�����ʱ
    			if(rankingList == null || rankingList.size() <= 0) {
    				Tools.showToastLong(RankingShowActivity.this, "û�и������Ϣ�ˡ�");
    				requestDataSuccess();
                	return;
    			} 
                
            } catch (ClientProtocolException e1) {
                Log.e(TAG, errMsg, e1);
                getDataError();
                return;
            } catch (IOException e1) {
                Log.e(TAG, errMsg, e1);
                getDataError();
                return;
            } catch (Exception e1) {
            	Log.e(TAG, errMsg, e1);
            	getDataError();
                return;
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
        //����,�ƻ�����
        BasicNameValuePair typeBNV = new BasicNameValuePair("type", String.valueOf(type));
        param.add(typeBNV);
        //��������
        BasicNameValuePair type_valueBNV = new BasicNameValuePair("type_value", String.valueOf(type_value));
        param.add(type_valueBNV);
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
