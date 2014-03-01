package com.f5.ourfarm.activity;

import static com.f5.ourfarm.util.Constants.PROGRESS_MESSAGE;
import static com.f5.ourfarm.util.Constants.PROGRESS_TITLE;
import static com.f5.ourfarm.util.URLConstants.ACTIVITY_URL;
import static com.f5.ourfarm.util.URLConstants.HOTTOP_URL;
import static com.f5.ourfarm.util.URLConstants.PROMOTIONS_URL;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.ProgressDialog;
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
import android.widget.TextView;

import com.f5.ourfarm.R;
import com.f5.ourfarm.activity.HotActivity.RunnableImp;
import com.f5.ourfarm.layout.PullToRefreshView;
import com.f5.ourfarm.layout.PullToRefreshView.OnFooterRefreshListener;
import com.f5.ourfarm.model.Activitis;
import com.f5.ourfarm.model.LoadWay;
import com.f5.ourfarm.model.Promotions;
import com.f5.ourfarm.util.HttpUtil;
import com.f5.ourfarm.util.Tools;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.umeng.analytics.MobclickAgent;

/**
 * �����
 * 
 * @author tianhao
 *
 */
public class PromotionsActivity extends Activity implements OnFooterRefreshListener {
    
    public String res4activity ;
    public String res4promotions;
    
    OnClickListener lback = null;
    OnClickListener lactivity = null;
    OnClickListener lpromotions = null;
    OnClickListener ldetails = null;
    
    //������
    ProgressDialog progressDialog = null;
  
    //��¼�ڼ�������ÿ����һ�Σ�����10����¼
    private int queryCountsActivity = 1;//�
    private int queryCountsPromotions = 1;//����
    //����ˢ��
    PullToRefreshView mPullToRefreshView;
    
    private static String TagPro = "����ҳ��";
    private static String errMsgPro = "��ȡ������Ϣʧ��";
    private static String TagAct = "�ҳ��";
    private static String errMsgAct = "��ȡ���Ϣʧ��";
    
    private static String msgActUploadNodata = "û�и���Ļ��"; 
    private static String msgProUploadNodata = "û�и���Ĵ�����"; 
    private static String msgActInitNodata = "��ѯ�������Ϣ"; 
    private static String msgProInitNodata = "��ѯ����������Ϣ"; 

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // ȥ��������
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_promotions);
        
        showBackgroundActivity();
        
        //��ʼ������ʾ�����ǵ�һ��
        queryCountsActivity = 1;
        queryCountsPromotions = 1;
        mPullToRefreshView = (PullToRefreshView) findViewById(R.id.main_pull_refresh_view);
        
        //׼��listeners
        this.prepareListeners();
        //����listeners
        this.batchSetListeners();
        
        progressDialog = ProgressDialog.show(PromotionsActivity.this, PROGRESS_TITLE, PROGRESS_MESSAGE, true);
        progressDialog.setCancelable(true);
        //����listview
        new Thread(runnable4activity).start();
        new Thread(runnable4promotions).start();
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
     * ����ʱ������ɫ
     */
    private void showBackgroundActivity() {
        PromotionsActivity.this.findViewById(R.id.TextView_activity_promotion).
        	setBackgroundResource(R.drawable.activity_buttom);
        PromotionsActivity.this.findViewById(R.id.TextView_activity_activity).
        	setBackgroundResource(R.drawable.activity_buttom_activity);
    }
    
    /**
     * �������ʱ������ɫ
     */
    private void showBackgroundPromotion() {
        PromotionsActivity.this.findViewById(R.id.TextView_activity_promotion).
        	setBackgroundResource(R.drawable.activity_buttom_activity);
        PromotionsActivity.this.findViewById(R.id.TextView_activity_activity).
        	setBackgroundResource(R.drawable.activity_buttom);
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
        //�
        lactivity = new  OnClickListener() {
            public void onClick(View v) {
            	showBackgroundActivity();
            	
            	//��ɼ�����������
            	PromotionsActivity.this.findViewById(R.id.ListView_main_active).
            		setVisibility(View.VISIBLE);
            	PromotionsActivity.this.findViewById(R.id.ListView_main_promotions).
            	setVisibility(View.GONE);
            }
        };
        //����
        lpromotions = new  OnClickListener() {
            public void onClick(View v) {
            	showBackgroundPromotion();
            	//�����ɼ��������
            	PromotionsActivity.this.findViewById(R.id.ListView_main_active).
            		setVisibility(View.GONE);
            	PromotionsActivity.this.findViewById(R.id.ListView_main_promotions).
            	setVisibility(View.VISIBLE);
            }
        };
    }
    
    /**
     * ��view�ͼ���
     */
    private void batchSetListeners() {
    	//����
        ImageView iback2home = (ImageView) this.findViewById(R.id.ImageView_button_back);
        iback2home.setOnClickListener(lback);
        
        //�
        TextView activity = (TextView) this.findViewById(R.id.TextView_activity_activity);
        activity.setOnClickListener(lactivity);
        
        //����
        TextView promotions = (TextView) this.findViewById(R.id.TextView_activity_promotion);
        promotions.setOnClickListener(lpromotions);
        
        //����ˢ��
        mPullToRefreshView.setOnFooterRefreshListener(this);
    }
    
    /**
     * ����û������ʱ�Ĵ���
     * 
     * @param loadWay ���ط�ʽ����ʼ��������������
     * @param msgInit ��ʼ����������Ϣ��ʾ
     * @param msgUpload ����������������Ϣ��ʾ
     */
    private void loadNoData(int loadWay, String msgInit, String msgUpload) {
    	if(loadWay == LoadWay.POLLUP_LOAD.ordinal()) {
    		Tools.showToastLong(PromotionsActivity.this, msgUpload);
    		mPullToRefreshView.onFooterRefreshComplete();
    	} else {
    		//���������ť��û�в�ѯ�����ݣ�������ʾ
    		Tools.showToastLong(PromotionsActivity.this, msgInit);
    	}
    }

    /**
     * ��װ������Ϣ
     * 
     * @param res
     */
    private void creatListView4promotions(String res, int loadWay) {
        LayoutInflater flater = LayoutInflater.from(this);
        LinearLayout list = (LinearLayout) this
                .findViewById(R.id.ListView_main_promotions);
        List<Promotions> lds = null;
        try{
	        Gson gson = new Gson();
	        lds = gson.fromJson(res, new TypeToken<List<Promotions>>() {}.getType());
		} catch (JsonSyntaxException e) {
//	        Tools.showToastShort(PromotionsActivity.this, errMsgPro);
//	        PromotionsActivity.this.finish();
	        loadNoData(loadWay, msgProInitNodata, msgProUploadNodata);
	        return;
		}
        if(lds == null || lds.size() == 0){
        	loadNoData(loadWay, msgProInitNodata, msgProUploadNodata);
        	return;
        } 
        
        for (Promotions ds : lds) {
            View v = flater.inflate(R.layout.listview_child_promotions,null);
         // name
            TextView tvName = (TextView) v
                    .findViewById(R.id.ListView_chlid_name);
            tvName.setText(ds.getName());
            // start time
            TextView tvSTime = (TextView) v
                    .findViewById(R.id.ListView_child_start);
            tvSTime.setText("��ʼʱ�䣺"+ds.getStartTime());
         // end time
            TextView tvETime = (TextView) v
                    .findViewById(R.id.ListView_child_end);
            tvETime.setText("����ʱ�䣺"+ds.getEndTime());
            //Content
            TextView tvContent = (TextView) v
                    .findViewById(R.id.ListView_chlid_content);
            tvContent.setText(ds.getContent());
            //
            list.addView(v);
            v.invalidate();
            list.invalidate();
        }
        
        mPullToRefreshView.onFooterRefreshComplete();
    }
    
    /**
     * ��װ���Ϣ����
     * 
     * @param res
     */
    private void creatListView4activity(String res, int loadWay) {
        LayoutInflater flater = LayoutInflater.from(this);
        LinearLayout list = (LinearLayout) this
                .findViewById(R.id.ListView_main_active);
        List<Activitis> lds = null;
        try{
	        Gson gson = new Gson();
	        lds = gson.fromJson(res, new TypeToken<List<Activitis>>() {}.getType());
		} catch (JsonSyntaxException e) {
//	        Tools.showToastShort(PromotionsActivity.this, errMsgAct);
//	        PromotionsActivity.this.finish();
	        loadNoData(loadWay, msgActInitNodata, msgActUploadNodata);
        	return;
		}
        if(lds == null || lds.size() == 0){
        	loadNoData(loadWay, msgActInitNodata, msgActUploadNodata);
        	return;
        }
        
        for (Activitis ds : lds) {
            View v = flater.inflate(R.layout.listview_child_promotions,null);
         // name
            TextView tvName = (TextView) v
                    .findViewById(R.id.ListView_chlid_name);
            tvName.setText(ds.getName());
            // start time
            TextView tvSTime = (TextView) v
                    .findViewById(R.id.ListView_child_start);
            tvSTime.setText("��ʼʱ�䣺"+ds.getStartTime());
         // end time
            TextView tvETime = (TextView) v
                    .findViewById(R.id.ListView_child_end);
            tvETime.setText("����ʱ�䣺"+ds.getEndTime());
            //Content
            TextView tvContent = (TextView) v
                    .findViewById(R.id.ListView_chlid_content);
            tvContent.setText(ds.getContent());
            //
            list.addView(v);
            v.invalidate();
            list.invalidate();
        }
        mPullToRefreshView.onFooterRefreshComplete();
    }
    //�
    Handler handler4activity = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String res = data.getString("value");
            Log.i(TagAct, "������Ϊ-->" + res);
            progressDialog.dismiss();
            //������
            setRes4activity(res);
            creatListView4activity(res, data.getInt("loadway"));
        }
    };

    //�
    Runnable runnable4activity = new RunnableImpActivity(LoadWay.INIT_LOAD);
    
    class RunnableImpActivity implements Runnable {
        //���ݼ��ط�ʽ��1����ʼ����, 2��ѡ��spinner��ʽ, 3��ѡ���������ط�ʽ
        LoadWay loadWay;
        RunnableImpActivity(LoadWay loadWay) {
            this.loadWay = loadWay;
        }
        
        @Override
        public void run() {
            List<NameValuePair> param = new ArrayList<NameValuePair>();
            BasicNameValuePair count  = new BasicNameValuePair("count ", String.valueOf(queryCountsActivity));
            param.add(count);
            String res = null;
            try {
                res = HttpUtil.postUrl(ACTIVITY_URL, param);
            } catch (ClientProtocolException e1) {
                Log.e(TagAct, errMsgAct, e1);
            } catch (IOException e1) {
                Log.e(TagAct, errMsgAct, e1);
            }
            Message msg = new Message();
            Bundle data = new Bundle();
            data.putString("value",res);
            //�����������
            data.putInt("loadway", loadWay.ordinal());
            msg.setData(data);
            handler4activity.sendMessage(msg);
        }
    }
    
    //����
    Handler handler4promotions = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String res = data.getString("value");
            Log.i(TagPro, "������Ϊ-->" + res);
            progressDialog.dismiss();
            //������
            setRes4promotions(res);
            creatListView4promotions(res, data.getInt("loadway"));
        }
    };

    //������Ϣ
    Runnable runnable4promotions = new RunnableImpPromotions(LoadWay.INIT_LOAD);
    
    class RunnableImpPromotions implements Runnable {
        //���ݼ��ط�ʽ��1����ʼ����, 2��ѡ��spinner��ʽ, 3��ѡ���������ط�ʽ
        LoadWay loadWay;
        RunnableImpPromotions(LoadWay loadWay) {
            this.loadWay = loadWay;
        }
        
        @Override
        public void run() {
            List<NameValuePair> param = new ArrayList<NameValuePair>();
            BasicNameValuePair count  = new BasicNameValuePair("count ", String.valueOf(queryCountsPromotions));
            param.add(count);
            String res = null;
            try {
                res = HttpUtil.postUrl(PROMOTIONS_URL, param);
            } catch (ClientProtocolException e1) {
                Log.e(TagPro, errMsgPro, e1);
            } catch (IOException e1) {
                Log.e(TagPro, errMsgPro, e1);
            }
            Message msg = new Message();
            Bundle data = new Bundle();
            data.putString("value",res);
            //�����������
            data.putInt("loadway", loadWay.ordinal());
            msg.setData(data);
            handler4promotions.sendMessage(msg);
        }
    }

    public String getRes4activity() {
        return res4activity;
    }

    public void setRes4activity(String res4activity) {
        this.res4activity = res4activity;
    }

    public String getRes4promotions() {
        return res4promotions;
    }

    public void setRes4promotions(String res4promotions) {
        this.res4promotions = res4promotions;
    }
    
    /**
     * ����ˢ�´���                                                                                                                                                                                                                          
     */
	@Override
	public void onFooterRefresh(PullToRefreshView view) {
		
		LinearLayout listActive = (LinearLayout) this
                .findViewById(R.id.ListView_main_active);
		//�ж��ĸ��ǻ������
		if(listActive.getVisibility() == View.VISIBLE) {
			++queryCountsActivity;
			Log.d("��������", queryCountsActivity + "");
			new Thread(new RunnableImpActivity(LoadWay.POLLUP_LOAD)).start();
		} else {
			++queryCountsPromotions;
			Log.d("�����������", queryCountsPromotions + "");
			new Thread(new RunnableImpPromotions(LoadWay.POLLUP_LOAD)).start();
		}
	}
}
