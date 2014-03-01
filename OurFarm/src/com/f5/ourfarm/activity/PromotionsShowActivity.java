package com.f5.ourfarm.activity;

import static com.f5.ourfarm.util.URLConstants.GET_ACTIVITY;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.f5.ourfarm.R;
import com.f5.ourfarm.model.ActivitisInfo;
import com.f5.ourfarm.model.LoadWay;
import com.f5.ourfarm.util.Constants;
import com.f5.ourfarm.util.HttpUtil;
import com.f5.ourfarm.util.Tools;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.umeng.analytics.MobclickAgent;

/**
 * ��ʾ�ҳ��
 * 
 * @author lify
 */
public class PromotionsShowActivity extends BaseActivity {
    //ȥ�ٷ���վ
    OnClickListener gotoNet = null;
    //loadingbar
    private LinearLayout loadingLayout;
    //���Ҫ��Ϣ
    private LinearLayout promotionsLayout;
    
    //��ŻList
    private List<ActivitisInfo> promotionsList = new ArrayList<ActivitisInfo>();
    
  	private static String TAG = "���ʾҳ��";
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); 
        setContentView(R.layout.activity_show_promotions);
        //׼��listeners
        this.prepareListeners();
        
        initStatus();
        
        //����listeners
        this.batchSetListeners();
    }
    
    /**
     * ��ʼҳ��ʱ�Ŀؼ�״̬�趨
     */
    private void initStatus() {
        //loadingbar
        loadingLayout = (LinearLayout)findViewById(R.id.nearby_loadingbar);
        //��Ҫ��Ϣ
        promotionsLayout = (LinearLayout)findViewById(R.id.ListView_main);
        
        //������ʾ���
        loadingLayout.setVisibility(View.VISIBLE);
        promotionsLayout.setVisibility(View.GONE);
        
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
        //ȥ�ٷ���վ
        gotoNet = new OnClickListener(){
            public void onClick(View v) {
            	// �ж������Ƿ���ã�������������ʾ�û�������������ת
            	if(!Tools.checkNetworkStatus(PromotionsShowActivity.this)) {
            		return;
            	}
            	Intent toWriteCommentIntent = new Intent(PromotionsShowActivity.this, CommentWriteActivity.class);
            	toWriteCommentIntent.putExtra(Constants.DESTINATION_ID, getIntent().getExtras().getLong("destinationId"));
            	toWriteCommentIntent.putExtra("destinationName", getIntent().getExtras().getString("destinationName"));
            	toWriteCommentIntent.putExtra("fromActivity", "comment");
                startActivity(toWriteCommentIntent);
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
    }
    
    /**
     * ������Ϣ�б�,���ݲ�ѯ�������Ϣ����ʾ������
     */
    private void showList(List<ActivitisInfo> activityList) {
        LayoutInflater flater = LayoutInflater.from(this);
        LinearLayout list = (LinearLayout) this.findViewById(R.id.ListView_main);
        
        // ��ȡ��ѯ�����ѭ�������Ҫ�б�
        for(ActivitisInfo activity: activityList){
        	View v = flater.inflate(R.layout.listview_child_activity, null);
        	TextView name = (TextView)v.findViewById(R.id.TextView_activity_name);
        	name.setText(activity.getName());
        	String starTime = activity.getStart_time();
        	String endTime = activity.getEnd_time();
        	//�����ʼ�ͽ���ʱ�䶼Ϊ��,����ʾ
        	if(Tools.isEmpty(starTime) && Tools.isEmpty(endTime)) {
        		v.findViewById(R.id.LinearLayout_activity_time).setVisibility(View.GONE);
        	}
        	//���ÿ�ʼ����ʱ��
        	TextView starTimeTextView = (TextView)v.findViewById(R.id.TextView_activity_startime);
        	starTimeTextView.setText(activity.getStart_time());
        	TextView endTimeTextView = (TextView)v.findViewById(R.id.TextView_activity_endtime);
        	endTimeTextView.setText(activity.getEnd_time());
        	//����
        	TextView introduction = (TextView)v.findViewById(R.id.TextView_activity_text);
        	introduction.setText(activity.getIntroduction());
        	//��ַ
        	TextView address = (TextView)v.findViewById(R.id.TextView_activity_address);
        	address.setText(activity.getAddress());
        	//�绰
        	TextView tel = (TextView)v.findViewById(R.id.TextView_activity_tel);
        	tel.setText(activity.getTel());
        	
    		//��ȡ�绰��ʾ��һ��
    		LinearLayout phoneLayout = (LinearLayout) v.findViewById(R.id.LinearLayout_activity_tel);
    		phoneLayout.setTag(activity.getTel());
    		phoneLayout.setOnClickListener(phoneCall);
    		//��ȡURL��һ��
    		LinearLayout gotoNet = (LinearLayout) v.findViewById(R.id.LinearLayout_goto_net);
    		gotoNet.setTag(activity.getWww());
    		gotoNet.setOnClickListener(netCall);
        	
            // add view
            list.addView(v);
        }
        list.invalidate();
    }

    Handler handler4nearby = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String res = data.getString("value");
            Log.d(TAG,"�������Ϊ-->" + res);
            
        	//��̨�߳���ɺ������ʾ���
            showList(promotionsList);
            
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
            
            String errMsg = "��ȡ���Ϣ����";
            String res4Comment = null;
            try {
            	res4Comment = HttpUtil.postUrl(GET_ACTIVITY, getParameters());
            	//TODO ģ�����ݲ���
    			try {
    				Gson gson = new Gson();
    				promotionsList = gson.fromJson(res4Comment, 
    	            		new TypeToken<List<ActivitisInfo>>() {}.getType());
    			} catch (JsonSyntaxException e) {
    				Log.e(TAG, "������ȡ���Ϣʧ��", e);
    				Tools.showToastLong(PromotionsShowActivity.this, "���ݼ���ʧ�ܡ�");
                    return;
    			}
    			//û���ҵ����Ϣʱ
    			if(promotionsList == null || promotionsList.size() <= 0) {
    				Tools.showToastLong(PromotionsShowActivity.this, "û�в�ѯ�����Ϣ��");
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
        
         //�ڼ�������
        BasicNameValuePair count = new BasicNameValuePair("count", "1");
        param.add(count);
        return param;
    }

	
	/**
	 * ��������ɹ�ʱ�������ʾ
	 */
	private void requestDataSuccess() {
		//�Ϸ�ԲȦ��������ʧ
        if (loadingLayout != null) {
             loadingLayout.setVisibility(View.GONE);
        }
        
    	promotionsLayout.setVisibility(View.VISIBLE);
	}

}
