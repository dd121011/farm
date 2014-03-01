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
 * 建议反馈
 * 
 * @author lify
 *
 */
public class MoreFeedbackActivity extends Activity {
    
    private static final String TAG = "建议反馈";
    private static final String errMsg = "提交反馈信息失败";
    
    //返回按钮
    OnClickListener lback = null;
    //反馈按钮
    OnClickListener lfeedback = null;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 去除标题栏
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_more_feedback);
        
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
    private void prepareListeners() {
    	//moreFeedback->my
        lback = new OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        };
        //点击反馈按钮
        lfeedback = new OnClickListener() {
        	public void onClick(View v) {
        		//建议
        		EditText feedbackMsg = (EditText)findViewById(R.id.editFeedbackMsg);
        		//联系方式(邮箱)
        		EditText feedbackContactWay = (EditText)findViewById(R.id.editFeedbackContactWay);
        		
        		//建议输入为空
        		if(Tools.isEmpty(feedbackMsg.getText().toString())) {
        			Tools.showToastShort(MoreFeedbackActivity.this, "请输入您的建议！");
        			return;
        		}
        		
        		List<NameValuePair> params = new ArrayList<NameValuePair>();
        		params.add(new BasicNameValuePair("content", feedbackMsg.getText().toString())); 
        		params.add(new BasicNameValuePair("contact", feedbackContactWay.getText().toString())); 
        		//返回值
        		String res = "";
        		try {
        			res = HttpUtil.postUrl(URLConstants.FEEDBACK_URL, params, "utf-8");
				} catch (ClientProtocolException e) {
					Log.e(TAG, errMsg);
				} catch (IOException e) {
					Log.e(TAG, errMsg);
				}
        		
        		Tools.showToastShort(MoreFeedbackActivity.this, "您的建议已提交，谢谢！");
        		finish();
        	}
        };
    }
    
    /**
     * 绑定view和监听
     */
    private void batchSetListeners() {
    	// back
        ImageView iback2home = (ImageView) this.findViewById(R.id.ImageView_button_back);
        iback2home.setOnClickListener(lback);
        
        // 反馈按钮
        Button feedback = (Button) this.findViewById(R.id.feedbackButton);
        feedback.setOnClickListener(lfeedback);
    }
}
