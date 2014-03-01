package com.f5.ourfarm.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;

import com.f5.ourfarm.R;
import com.f5.ourfarm.util.Constants;
import com.f5.ourfarm.util.HttpUtil;
import com.f5.ourfarm.util.Tools;
import com.f5.ourfarm.util.URLConstants;
import com.umeng.analytics.MobclickAgent;

/**
 * д����
 * 
 * @author lify
 *
 */
public class CommentWriteActivity extends Activity {
    
    private static final String TAG = "д����";
    private static final String errMsg = "д����ʧ��";
    
    //���ذ�ť
    OnClickListener lback = null;
    //�ύ��ť
    OnClickListener writeCommit = null;
    OnRatingBarChangeListener orbcl = null;
    RatingBar ratingbar = null;
    
    //����
    private float score = 1.0f;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // ȥ��������
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_write_comment);
        
        //׼��listeners
        this.prepareListeners();
        
        //����listeners
        this.batchSetListeners();
        
        //�����ʼ��
        initStatus();
        
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
     * ҳ���ʼ��
     */
    private void initStatus() {
    	TextView commentName = (TextView) this.findViewById(R.id.comment_name);
    	commentName.setText(getIntent().getExtras().getString("destinationName"));
    }
    
    /**
     * �������¼���Ķ�����
     */
    private void prepareListeners() {
    	//moreFeedback->my
        lback = new OnClickListener() {
            public void onClick(View v) {
//                Intent nearby2detail = new Intent(CommentWriteActivity.this,DetailActivity.class);
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
        //���������ť
        writeCommit = new OnClickListener() {
        	public void onClick(View v) {
        		//��������
        		EditText feedbackMsg = (EditText)findViewById(R.id.editCommentMsg);
        		
        		//��������Ϊ��
        		if(Tools.isEmpty(feedbackMsg.getText().toString())) {
        			Tools.showToastShort(CommentWriteActivity.this, "�����������������ݣ�");
        			return;
        		}
        		
        		//����ֵ
        		String res = "";
        		try {
        			res = HttpUtil.postUrl(URLConstants.ADD_COMMENT, getParams(feedbackMsg.getText().toString()), "utf-8");
				} catch (ClientProtocolException e) {
					Log.e(TAG, errMsg);
					writeCommentFail();
        			return;
				} catch (IOException e) {
					Log.e(TAG, errMsg);
					writeCommentFail();
        			return;
				}
        		
        		if("0".equals(res)) {
        			Tools.showToastShort(CommentWriteActivity.this, "�����������ύ��лл��");
        			
        			Intent afterWrite2detail = null;
        			String fromActivity = getIntent().getExtras().getString("fromActivity");
        			if("detail".equals(fromActivity)) {
        				afterWrite2detail = new Intent(CommentWriteActivity.this,DetailActivity.class);
        				//�����жϵ��������������
        				afterWrite2detail.putExtra("nearbyType", getIntent().getExtras().getInt("nearbyType"));
        			} else if("comment".equals(fromActivity)) {
        				afterWrite2detail = new Intent(CommentWriteActivity.this,CommentShowActivity.class);
        				afterWrite2detail.putExtra("destinationName", getIntent().getExtras().getString("destinationName"));
        			}
        			
                    //���������������ݱ�����Intent��ȷ�����ݿ���activity�䴫��
        			afterWrite2detail.putExtra(Constants.DESTINATION_ID, getIntent().getExtras().getLong("destinationId"));
                    startActivity(afterWrite2detail);
        			
        			finish();
        		} else {
        			writeCommentFail();
        			return;
        		}
        	}
        };
        
        //����������ǣ���ȡ������
        orbcl = new OnRatingBarChangeListener(){
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating,
                    boolean fromUser) {
            	score = ratingbar.getRating();
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
        
        //��������
        ratingbar = (RatingBar) this.findViewById(R.id.write_comment_ratingbar);
        ratingbar.setOnRatingBarChangeListener(orbcl);
        // �ύ��ť
        Button feedback = (Button) this.findViewById(R.id.commentCommitButton);
        feedback.setOnClickListener(writeCommit);
    }
    
    /**
     * ׼�����۲���
     * 
     * @param content
     * @return ��װ��Ĳ����б����
     */
    private List<NameValuePair> getParams(String content) {
    	List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("destination_id", String.valueOf(getIntent().getExtras().getLong(Constants.DESTINATION_ID))));
		params.add(new BasicNameValuePair("content", content));
		params.add(new BasicNameValuePair("comment_score", String.valueOf(score)));
		params.add(new BasicNameValuePair("comment_time", ""));
		//TODO ��ʱ���������û�
		params.add(new BasicNameValuePair("user_id", "0"));
		params.add(new BasicNameValuePair("otherSYS_type", "0"));
		
		return params;
    }
    
    /**
     * �ύ����ʧ��
     */
    private void writeCommentFail() {
    	Tools.showToastShort(CommentWriteActivity.this, "�ܱ�Ǹ����������δ���ύ�ɹ��������ԣ�");
    }
}
