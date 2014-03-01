package com.f5.ourfarm.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.f5.ourfarm.R;
import com.f5.ourfarm.third.ThirdAuthInfo;
import com.f5.ourfarm.third.qq.OQQ;
import com.f5.ourfarm.third.qq.OQQToken;
import com.f5.ourfarm.third.weibo.OWeibo;
import com.f5.ourfarm.third.weibo.OWeiboAuthListener;
import com.f5.ourfarm.util.AccessTokenKeeper;
import com.f5.ourfarm.util.Constants;
import com.f5.ourfarm.util.Tools;
import com.tencent.weibo.oauthv2.OAuthV2;
import com.tencent.weibo.webview.OAuthV2AuthorizeWebView;
import com.umeng.analytics.MobclickAgent;
import com.weibo.sdk.android.WeiboDialogError;
import com.weibo.sdk.android.WeiboException;

/**
 * 我的页面
 * 
 * @author tianhao
 */
public class HomeMyActivity extends BaseActivity implements OnClickListener{

	//我的收藏
    OnClickListener showMyFavorites = null;
    //我的本次行程
    OnClickListener showMyThisTrip = null;
 
	OWeiboAuthListener weibolistener;
	
	ImageView weibo;
	ImageView qq;
	ImageView backImage;
	//View sendweibo;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 去除标题栏
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 准备listeners
		this.prepareListeners();
		
		setContentView(R.layout.activity_main_frame);
		//初始化token
		AccessTokenKeeper.init(this);
		backImage = (ImageView)this.findViewById(R.id.ImageView_button_back);
		weibo = (ImageView) this.findViewById(R.id.more_image_weibo);
		weibo.setTag(R.drawable.weibo_off);
		qq = (ImageView) this.findViewById(R.id.more_image_qq);
		qq.setTag(R.drawable.weibo_off);
		Log.i("qqToken.weibotype", ""+ThirdAuthInfo.qqToken.weibotype);
		Log.i("weiboAccessToken.weibotype", ""+ThirdAuthInfo.weiboAccessToken.isWeibotype());
		if(ThirdAuthInfo.qqToken != null && ThirdAuthInfo.qqToken.weibotype)
		{
			setQQBand(true);
		}
			
		if(ThirdAuthInfo.weiboAccessToken != null && ThirdAuthInfo.weiboAccessToken.isWeibotype())
		{
			setWeiboBand(true);
		}
		
		// 设置标题
		/*TextView tvTitle = (TextView) this
				.findViewById(R.id.TextView_home_title);
		tvTitle.setText(R.string.common_my);*/
		// 设置底部切换颜色
		ImageView ivMy = (ImageView) this.findViewById(R.id.ImageView_home_my);
		//sendweibo = this.findViewById(R.id.sendweiboview);
		//sendweibo.setOnClickListener(this);
		ivMy.setImageResource(R.drawable.home_my_active);
		// 设置listeners
		this.batchSetListeners();
		
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode == KeyEvent.KEYCODE_BACK)
		{
			this.finish();
		}
		return false;
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

		
		showMyThisTrip = new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i2myThisTrip = new Intent(getApplicationContext(),
						MyThisTripActivity.class);
				startActivity(i2myThisTrip);
			}
		};
		// 我的收藏
		showMyFavorites = new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i2myFavotite = new Intent(getApplicationContext(),
						MyFavoriteActivity.class);
				startActivity(i2myFavotite);
			}
		};

		weibolistener = new OWeiboAuthListener(HomeMyActivity.this) {

			
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
		
	}

	/**
	 * 绑定view和监听
	 */
	private void batchSetListeners() {
		// 进入我的收藏页面
		LinearLayout myFavorite = (LinearLayout) this
				.findViewById(R.id.layout_my_favorite);
		myFavorite.setOnClickListener(showMyFavorites);

		// 进入我的本次行程页面
		LinearLayout myThisTrip = (LinearLayout) this
				.findViewById(R.id.layout_my_this_trip);
		myThisTrip.setOnClickListener(showMyThisTrip);

		// 底部面板4个按钮
		RelativeLayout rHome = (RelativeLayout) this
				.findViewById(R.id.Layout_home_panelBottom_home);
		rHome.setTag(1);
		rHome.setOnClickListener(lPanelBottom);

		RelativeLayout rCheckin = (RelativeLayout) this
				.findViewById(R.id.Layout_home_panelBottom_checkin);
		rCheckin.setTag(2);
		rCheckin.setOnClickListener(lPanelBottom);

		RelativeLayout rMy = (RelativeLayout) this
				.findViewById(R.id.Layout_home_panelBottom_my);
		rMy.setTag(3);
		rMy.setOnClickListener(lPanelBottom);

		RelativeLayout rMore = (RelativeLayout) this
				.findViewById(R.id.Layout_home_panelBottom_more);
		rMore.setTag(4);
		rMore.setOnClickListener(lPanelBottom);
		
		RelativeLayout rHomeMain = (RelativeLayout) this
				.findViewById(R.id.Layout_home_main);
		rHomeMain.setTag(5);
		rHomeMain.setOnClickListener(lPanelBottom);
		
		weibo.setOnClickListener(this);	
		qq.setOnClickListener(this);
		backImage.setOnClickListener(this);
		
	}

	
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
                	this.setQQBand(false);
                	//qq.setChecked(false);
                }
            }
            else
            {
            	setQQBand(false);
            }
    }
	private void setWeiboBand(boolean isband)
	{
		if(isband)
		{
			weibo.setImageResource(R.drawable.weibo_on);
			weibo.setTag(R.drawable.weibo_on);
		}	
		else
		{
			weibo.setImageResource(R.drawable.weibo_off);
			weibo.setTag(R.drawable.weibo_off);
		}
			
		weibo.invalidate();
	}
	private void setQQBand(boolean isband)
	{
		if(isband)
		{
			qq.setImageResource(R.drawable.weibo_on);
			qq.setTag(R.drawable.weibo_on);
		}
			
		else
		{
			qq.setImageResource(R.drawable.weibo_off);
			qq.setTag(R.drawable.weibo_off);
		}
			
		qq.invalidate();
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		  /*Uri uri = Uri.parse("market://details?id=com.f5.ourfarm");
		  Intent it = new Intent(Intent.ACTION_VIEW, uri); 
		  this.startActivity(it);*/
		if(v.getId() == weibo.getId())
		{
			if (R.drawable.weibo_off == (Integer)weibo.getTag()) {
				OWeibo.authorize(HomeMyActivity.this,weibolistener);
			} else {
				OWeibo.setWeiboType(HomeMyActivity.this, false);
				setWeiboBand(false);
				//OWeibo.sendToWeibo(HomeMyActivity.this, "测试，测试，机长呼叫塔台");
			}
		}
		if(v.getId() == qq.getId())
		{
			if (R.drawable.weibo_off == (Integer)qq.getTag()) {
				OQQ.authorize(HomeMyActivity.this);
			}
			else
			{
				OQQ.setQQType(HomeMyActivity.this, false);
				setQQBand(false);
			}
		}
		if(v.getId() == backImage.getId())
		{
			this.finish();
		}
	}
}
