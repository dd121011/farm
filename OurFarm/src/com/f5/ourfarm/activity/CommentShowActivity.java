package com.f5.ourfarm.activity;

import static com.f5.ourfarm.util.URLConstants.GET_COMMENT;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Intent;
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
import com.f5.ourfarm.model.Comment;
import com.f5.ourfarm.model.LoadWay;
import com.f5.ourfarm.util.Constants;
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
public class CommentShowActivity extends Activity implements OnFooterRefreshListener{
    OnClickListener lback = null;
//    OnClickListener details = null;
    // д����
    OnClickListener writeComment = null;
    
    //loadingbar
    private LinearLayout loadingLayout;
    //��Ҫ��Ϣ
    private LinearLayout summaryLayout;
    //����ˢ��
    PullToRefreshView mPullToRefreshView;
    
    //�������List
    private List<Comment> commentList = new ArrayList<Comment>();
    
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
        setContentView(R.layout.activity_show_comment);
        
        initStatus();
        
        //����listeners
        this.batchSetListeners();
    }
    
    /**
     * ��ʼҳ��ʱ�Ŀؼ�״̬�趨
     */
    private void initStatus() {
    	//Titie������ʾ���۵ľ�������
    	TextView commentName = (TextView) this.findViewById(R.id.comment_name);
    	commentName.setText(getIntent().getExtras().getString("destinationName"));
    	
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
        
        queryCounts = 1;
    	new Thread(new RunnableImp(LoadWay.INIT_LOAD)).start();
        
    	/*
        //��ȡ��ҳ���󵽵�����
        commentList = (List<Comment>)getIntent().getSerializableExtra(Constants.MORE_COMMENT);
        if(commentList != null && commentList.size() > 0) {
        	//��ʼ������ʾ�����ǵ�2��
            queryCounts = 2;
        	showList(commentList);
        	requestDataSuccess();
        } else {
        	queryCounts = 1;
        	new Thread(new RunnableImp(LoadWay.INIT_LOAD)).start();
        }
        */
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
//                Intent nearby2detail = new Intent(CommentShowActivity.this,DetailActivity.class);
//                //���������������ݱ�����Intent��ȷ�����ݿ���activity�䴫��
//                nearby2detail.putExtra(Constants.DESTINATION_ID, getIntent().getExtras().getLong("destinationId"));
//                //�����жϵ��������������
//                nearby2detail.putExtra("nearbyType", "2");
//                
//                startActivity(nearby2detail);
//            	getIntent().getExtras().putInt("nearbyType", getIntent().getExtras().getInt("nearbyType"));
                finish();
            }
        };
        
        //д����
        writeComment = new OnClickListener(){
            public void onClick(View v) {
            	// �ж������Ƿ���ã�������������ʾ�û�������������ת
            	if(!Tools.checkNetworkStatus(CommentShowActivity.this)) {
            		return;
            	}
            	Intent toWriteCommentIntent = new Intent(CommentShowActivity.this, CommentWriteActivity.class);
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
        
        mPullToRefreshView.setOnFooterRefreshListener(this);
        
        //��д���۰�ť
        ImageView writeCommentImageView = (ImageView)findViewById(R.id.ImageView_write_comment);
        writeCommentImageView.setOnClickListener(writeComment);
    }
    
    
    /**
     * ���������б�
     * ���ݲ�ѯ�����������ݲ���ʾ������
     */
    private void showList(List<Comment> aroundFarm) {
        LayoutInflater flater = LayoutInflater.from(this);
        LinearLayout list = (LinearLayout) this.findViewById(R.id.ListView_main);
        
        // ��ȡ��ѯ�����ѭ�������Ҫ�б�
        for(Comment comment: aroundFarm){
        	View v = flater.inflate(R.layout.listview_child_comment, null);
        	TextView name = (TextView)v.findViewById(R.id.TextView_comment_name);
        	//�û�����
        	if(comment.getUser_id() != 0) {
        		name.setText(String.valueOf(comment.getUser_id()));
        	} else {
        		name.setText("�����û�");
        	}
        	//��������
        	RatingBar ratingBar = (RatingBar)v.findViewById(R.id.ratingBar_comment_sroce);
        	ratingBar.setRating(comment.getComment_score());
        	
        	//��������
        	TextView content = (TextView)v.findViewById(R.id.TextView_comment_text);
        	content.setText(comment.getContent());
        	//����ʱ��
        	TextView commenTime = (TextView)v.findViewById(R.id.TextView_comment_time);
        	commenTime.setText(comment.getComment_time());
        	
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
            Log.d(TAG,"�������۽��Ϊ-->" + res);
            
        	//��̨�߳���ɺ������ʾ���
            showList(commentList);
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
            String errMsg = "��ȡ������Ϣ����";
            String res4Comment = null;
            try {
            	res4Comment = HttpUtil.postUrl(GET_COMMENT, getParameters());
    			try {
    				Gson gson = new Gson();
    				commentList = gson.fromJson(res4Comment, 
    	            		new TypeToken<List<Comment>>() {}.getType());
    			} catch (JsonSyntaxException e) {
    				Log.e(TAG, "������ȡ������Ϣ��ʧ��", e);
    				Tools.showToastLong(CommentShowActivity.this, "���ݼ���ʧ�ܡ�");
                    return;
    			}
    			
    			//û���ҵ�����ʱ
    			if(commentList == null || commentList.size() <= 0) {
    				Tools.showToastLong(CommentShowActivity.this, "û�и���������ˡ�");
    				requestDataSuccess();
                	return;
    			} 
                
            } catch (ClientProtocolException e1) {
                Log.e(Tag, errMsg, e1);
                getDataError();
                return;
            } catch (IOException e1) {
                Log.e(Tag, errMsg, e1);
                getDataError();
                return;
            } catch (Exception e1) {
            	Log.e(Tag, errMsg, e1);
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
        
        //����ID
        BasicNameValuePair view_id = new BasicNameValuePair("destination_id", 
        		String.valueOf(getIntent().getExtras().getLong("destinationId")));
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

	/**
	 * ��������ʧ��
	 */
	private void getDataError() {
		Tools.showToastLong(CommentShowActivity.this, "���ݼ���ʧ�ܡ�");
	}
}
