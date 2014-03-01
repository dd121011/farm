package com.f5.ourfarm.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.f5.ourfarm.R;
import com.f5.ourfarm.util.HttpUtil;
import com.f5.ourfarm.util.Tools;
import com.f5.ourfarm.util.URLConstants;
import com.umeng.analytics.MobclickAgent;

/**
 * ���鷴��
 * 
 * @author lify
 *
 */
public class MoreFeedbackActivity extends Activity {
    
    private static final String TAG = "���鷴��";
    private static final String errMsg = "�ύ������Ϣʧ��";
    
    //���ذ�ť
    OnClickListener lback = null;
    //������ť
    OnClickListener lfeedback = null;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // ȥ��������
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_more_feedback);
        
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
    private void prepareListeners() {
    	//moreFeedback->my
        lback = new OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        };
        //���������ť
        lfeedback = new OnClickListener() {
        	public void onClick(View v) {
        		//����
        		EditText feedbackMsg = (EditText)findViewById(R.id.editFeedbackMsg);
        		//��ϵ��ʽ(����)
        		EditText feedbackContactWay = (EditText)findViewById(R.id.editFeedbackContactWay);
        		
        		//��������Ϊ��
        		if(Tools.isEmpty(feedbackMsg.getText().toString())) {
        			Tools.showToastShort(MoreFeedbackActivity.this, "���������Ľ��飡");
        			return;
        		}
        		
        		List<NameValuePair> params = new ArrayList<NameValuePair>();
        		params.add(new BasicNameValuePair("content", feedbackMsg.getText().toString())); 
        		params.add(new BasicNameValuePair("contact", feedbackContactWay.getText().toString())); 
        		//����ֵ
        		String res = "";
        		try {
        			res = HttpUtil.postUrl(URLConstants.FEEDBACK_URL, params, "utf-8");
				} catch (ClientProtocolException e) {
					Log.e(TAG, errMsg);
				} catch (IOException e) {
					Log.e(TAG, errMsg);
				}
        		
        		Tools.showToastShort(MoreFeedbackActivity.this, "���Ľ������ύ��лл��");
        		finish();
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
        
        // ������ť
        Button feedback = (Button) this.findViewById(R.id.feedbackButton);
        feedback.setOnClickListener(lfeedback);
    }
}
