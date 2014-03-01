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
 * 写评价
 * 
 * @author lify
 *
 */
public class CommentWriteActivity extends Activity {
    
    private static final String TAG = "写评价";
    private static final String errMsg = "写评价失败";
    
    //返回按钮
    OnClickListener lback = null;
    //提交按钮
    OnClickListener writeCommit = null;
    OnRatingBarChangeListener orbcl = null;
    RatingBar ratingbar = null;
    
    //评分
    private float score = 1.0f;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 去除标题栏
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_write_comment);
        
        //准备listeners
        this.prepareListeners();
        
        //设置listeners
        this.batchSetListeners();
        
        //界面初始化
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
     * 页面初始化
     */
    private void initStatus() {
    	TextView commentName = (TextView) this.findViewById(R.id.comment_name);
    	commentName.setText(getIntent().getExtras().getString("destinationName"));
    }
    
    /**
     * 监听到事件后的动作；
     */
    private void prepareListeners() {
    	//moreFeedback->my
        lback = new OnClickListener() {
            public void onClick(View v) {
//                Intent nearby2detail = new Intent(CommentWriteActivity.this,DetailActivity.class);
//                //将服务器返回数据保存入Intent，确保数据可在activity间传递
//                nearby2detail.putExtra(Constants.DESTINATION_ID, getIntent().getExtras().getLong("destinationId"));
//                //用于判断点击的是那种类型
//                nearby2detail.putExtra("nearbyType", "2");
//                
//                startActivity(nearby2detail);
//            	getIntent().getExtras().putInt("nearbyType", getIntent().getExtras().getInt("nearbyType"));
                finish();
            }
        };
        //点击反馈按钮
        writeCommit = new OnClickListener() {
        	public void onClick(View v) {
        		//评论内容
        		EditText feedbackMsg = (EditText)findViewById(R.id.editCommentMsg);
        		
        		//评论输入为空
        		if(Tools.isEmpty(feedbackMsg.getText().toString())) {
        			Tools.showToastShort(CommentWriteActivity.this, "请输入您的评论内容！");
        			return;
        		}
        		
        		//返回值
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
        			Tools.showToastShort(CommentWriteActivity.this, "您的评论已提交，谢谢！");
        			
        			Intent afterWrite2detail = null;
        			String fromActivity = getIntent().getExtras().getString("fromActivity");
        			if("detail".equals(fromActivity)) {
        				afterWrite2detail = new Intent(CommentWriteActivity.this,DetailActivity.class);
        				//用于判断点击的是那种类型
        				afterWrite2detail.putExtra("nearbyType", getIntent().getExtras().getInt("nearbyType"));
        			} else if("comment".equals(fromActivity)) {
        				afterWrite2detail = new Intent(CommentWriteActivity.this,CommentShowActivity.class);
        				afterWrite2detail.putExtra("destinationName", getIntent().getExtras().getString("destinationName"));
        			}
        			
                    //将服务器返回数据保存入Intent，确保数据可在activity间传递
        			afterWrite2detail.putExtra(Constants.DESTINATION_ID, getIntent().getExtras().getLong("destinationId"));
                    startActivity(afterWrite2detail);
        			
        			finish();
        		} else {
        			writeCommentFail();
        			return;
        		}
        	}
        };
        
        //点击评分星星，获取评分数
        orbcl = new OnRatingBarChangeListener(){
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating,
                    boolean fromUser) {
            	score = ratingbar.getRating();
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
        
        //评分星星
        ratingbar = (RatingBar) this.findViewById(R.id.write_comment_ratingbar);
        ratingbar.setOnRatingBarChangeListener(orbcl);
        // 提交按钮
        Button feedback = (Button) this.findViewById(R.id.commentCommitButton);
        feedback.setOnClickListener(writeCommit);
    }
    
    /**
     * 准备评价参数
     * 
     * @param content
     * @return 组装后的参数列表对象
     */
    private List<NameValuePair> getParams(String content) {
    	List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("destination_id", String.valueOf(getIntent().getExtras().getLong(Constants.DESTINATION_ID))));
		params.add(new BasicNameValuePair("content", content));
		params.add(new BasicNameValuePair("comment_score", String.valueOf(score)));
		params.add(new BasicNameValuePair("comment_time", ""));
		//TODO 暂时都是匿名用户
		params.add(new BasicNameValuePair("user_id", "0"));
		params.add(new BasicNameValuePair("otherSYS_type", "0"));
		
		return params;
    }
    
    /**
     * 提交评论失败
     */
    private void writeCommentFail() {
    	Tools.showToastShort(CommentWriteActivity.this, "很抱歉，您的评论未能提交成功，请重试！");
    }
}
