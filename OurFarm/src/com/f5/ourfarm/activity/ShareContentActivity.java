package com.f5.ourfarm.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.f5.ourfarm.R;
import com.f5.ourfarm.third.ThirdAuthInfo;
import com.f5.ourfarm.third.qq.OQQ;
import com.f5.ourfarm.third.qq.OQQToken;
import com.f5.ourfarm.third.weibo.OWeibo;
import com.f5.ourfarm.third.weibo.OWeiboAuthListener;
import com.f5.ourfarm.third.weibo.OWeiboMessageListener;
import com.f5.ourfarm.util.AccessTokenKeeper;
import com.tencent.weibo.oauthv2.OAuthV2;
import com.tencent.weibo.webview.OAuthV2AuthorizeWebView;
import com.weibo.sdk.android.WeiboDialogError;
import com.weibo.sdk.android.WeiboException;

public class ShareContentActivity extends Activity implements OnClickListener{
	ImageView backView;
	Button shareButton;
	EditText editText;
	ImageView imageView_my_weibo;
	ImageView imageView_my_qq;
	TextView edittext_length;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_share);
        AccessTokenKeeper.init(this);
        
        setView();
        setListener();
        initView();
    }
    private void initView()
    {
    	if(ThirdAuthInfo.qqToken != null && ThirdAuthInfo.qqToken.weibotype)
		{
			setQQBand(true);
		}
			
		if(ThirdAuthInfo.weiboAccessToken != null && ThirdAuthInfo.weiboAccessToken.isWeibotype())
		{
			setWeiboBand(true);
		}
		editText.setText(this.getIntent().getStringExtra("shareContent"));
		
    }
    private void setWeiboBand(boolean isband)
	{
		if(isband)
		{
			imageView_my_weibo.setImageResource(R.drawable.active_more_weibo);
			imageView_my_weibo.setTag(R.drawable.active_more_weibo);
		}	
		else
		{
			imageView_my_weibo.setImageResource(R.drawable.refresh);
			imageView_my_weibo.setTag(R.drawable.refresh);
		}
			
		imageView_my_weibo.invalidate();
	}
	private void setQQBand(boolean isband)
	{
		if(isband)
		{
			imageView_my_qq.setImageResource(R.drawable.active_more_qq);
			imageView_my_qq.setTag(R.drawable.active_more_qq);
		}
			
		else
		{
			imageView_my_qq.setImageResource(R.drawable.refresh);
			imageView_my_qq.setTag(R.drawable.refresh);
		}
			
		imageView_my_qq.invalidate();
	}
    private void setView()
    {
    	backView = (ImageView) findViewById(R.id.ImageView_button_back);
    	shareButton = (Button) findViewById(R.id.ImageView_button_map);
    	editText = (EditText) findViewById(R.id.weibosdk_etEdit);
    	imageView_my_weibo = (ImageView) findViewById(R.id.ImageView_my_weibo);
    	imageView_my_weibo.setTag(R.drawable.refresh);
    	imageView_my_qq = (ImageView) findViewById(R.id.ImageView_my_qq);
    	imageView_my_qq.setTag(R.drawable.refresh);
    	edittext_length = (TextView) findViewById(R.id.TextView_edittext_length);
    }
    private void setListener()
    {
    	backView.setOnClickListener(this);
    	shareButton.setOnClickListener(this);
    	imageView_my_weibo.setOnClickListener(this);
    	imageView_my_qq.setOnClickListener(this);
    	editText.addTextChangedListener(textWatcher);
    }
    
    TextWatcher textWatcher = new TextWatcher()
    {

		@Override
		public void afterTextChanged(Editable arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// TODO Auto-generated method stub
			changeText(""+s.length());
		}
    	
    };
    public void changeText(String text)
    {
    	edittext_length.setText(text);
    	edittext_length.invalidate();
    }
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.getId() == backView.getId())
		{
			this.finish();
		}
		if(v.getId() == imageView_my_weibo.getId())
		{
			if (R.drawable.refresh == (Integer)imageView_my_weibo.getTag()) {
				OWeibo.authorize(this,weibolistener);
			} else {
				OWeibo.setWeiboType(this, false);
				setWeiboBand(false);
				//OWeibo.sendToWeibo(HomeMyActivity.this, "测试，测试，机长呼叫塔台");
			}
		}
		if(v.getId() == imageView_my_qq.getId())
		{
			if (R.drawable.refresh == (Integer)imageView_my_qq.getTag()) {
				OQQ.authorize(this);
			}
			else
			{
				OQQ.setQQType(this, false);
				setQQBand(false);
			}
		}
		if(v.getId() == shareButton.getId())
		{
			if(editText.getText().length()>140)
			{
				//Toast.makeText(this, "发生内容超过140个字符", Toast.LENGTH_SHORT);
				changeText("超过140个字符");
				return;
			}
			Log.i("qqtype", ""+ThirdAuthInfo.qqToken.weibotype);
			Log.i("weibotype", ""+ThirdAuthInfo.weiboAccessToken.isWeibotype());
			if(ThirdAuthInfo.qqToken != null && ThirdAuthInfo.qqToken.weibotype)
			{
				OQQ.sendToQQNew(this, editText.getText().toString());
			}
				
			if(ThirdAuthInfo.weiboAccessToken != null && ThirdAuthInfo.weiboAccessToken.isWeibotype())
			{
				OWeibo.sendToWeiboNew(this, editText.getText().toString(), new OWeiboMessageListener(ShareContentActivity.this));
			}
		}
	}
	OWeiboAuthListener weibolistener = new OWeiboAuthListener(this) {

		
		@Override
		public void onComplete(Bundle values) {
			// TODO Auto-generated method stub
			super.onComplete(values);
			setWeiboBand(true);
		}

		@Override
		public void onError(WeiboDialogError e) {
			// TODO Auto-generated method stub
			
			super.onError(e);
			Log.i("weibo auth","onError");
			setWeiboBand(false);
		}

		@Override
		public void onCancel() {
			// TODO Auto-generated method stub
			super.onCancel();
			Log.i("weibo auth","onCancel");
			setWeiboBand(false);
		}

		@Override
		public void onWeiboException(WeiboException e) {
			// TODO Auto-generated method stub
			super.onWeiboException(e);
			Log.i("weibo auth","onWeiboException");
			setWeiboBand(false);
		}
		
	};
	protected void onActivityResult(int requestCode, int resultCode, Intent data)   {
        if (requestCode==2 && resultCode==OAuthV2AuthorizeWebView.RESULT_CODE) {
            	OAuthV2 oAuth=(OAuthV2) data.getExtras().getSerializable("oauth");
                if(oAuth.getStatus()==0)
                {
                    OQQToken qqtoken = new OQQToken();
     				ThirdAuthInfo.qqToken = qqtoken;
     				ThirdAuthInfo.qqToken.accessToken = oAuth.getAccessToken();
     				ThirdAuthInfo.qqToken.expiresIn = Long.parseLong(oAuth.getExpiresIn());
     				ThirdAuthInfo.qqToken.setWeibotype(true);
     				AccessTokenKeeper.keepAccessToken(this, ThirdAuthInfo.qqToken);
     				setQQBand(true);
                }
                else
                {
                	setQQBand(false);
           
                }
            }
            else
            {
            	setQQBand(false);
            }
    }
}

