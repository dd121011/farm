package com.f5.ourfarm.activity;

import static com.f5.ourfarm.util.URLConstants.GET_COMMENT;
import static com.f5.ourfarm.util.URLConstants.NEARBY_DETAIL_URL;
import static com.f5.ourfarm.util.URLConstants.GET_AROUND_FARM_HOME;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.f5.ourfarm.R;
import com.f5.ourfarm.model.Comment;
import com.f5.ourfarm.model.Destination;
import com.f5.ourfarm.model.Request;
import com.f5.ourfarm.model.Summary;
import com.f5.ourfarm.util.Constants;
import com.f5.ourfarm.util.HttpUtil;
import com.f5.ourfarm.util.Tools;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.umeng.analytics.MobclickAgent;

public class MountainVillaActivity extends Activity implements OnClickListener {
	private static final String TAG = "详细信息页面:";
	
	private static final String errMsg = "获取山庄详细信息失败";
	// 后退
	ImageView iBack;
	// 概要的缩略图
	ImageView ivPic;
	// 电话
	LinearLayout phoneLayout;
	// 获取地址
	LinearLayout showOnMapLayout;

	// 底部面板3个按钮
	TextView tvfavorites;
	TextView tvcheckin;
	TextView tvshare;
	// detail相关的view
	TextView tvName;
	// sroce
	RatingBar rbSroce;
	// price info
	TextView tvPrice;
	// address
	TextView tvAdress;
	// tel
	TextView tvTel;
	// introduction_name
	TextView tvName4introduction;
	// introduction
	TextView tvIntroduction;
	// preferentialInfo
	TextView tvPreferentialInfo;
	// loading
	//评论内容
    private LinearLayout commentContent;
	LinearLayout panelBottom;
	LinearLayout loadingLayout;
	LinearLayout commentLoadingbar;
	LinearLayout detailSummaryLayout;
	ImageView writeCommentImageView;
    
	// content
	LinearLayout contentLayout;
	// desid
	long desid;
	// 明细信息
	Destination destination = null;
	HashMap<String, String> par = new HashMap<String, String>();

	int detailnum = 1;
	int commentnum = 2;
	int picnum = 3;

	String picUrl;
	Bitmap pic;
	HashMap<String, Bitmap> pics = new HashMap<String, Bitmap>();
	ArrayList<String> urls = new ArrayList<String>();
	private ArrayList<Comment> commentList = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_mountain_villa);
		this.setViewId();
		this.setViewListener();
		this.initView();
	}

	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	private void setViewId() {
		iBack = (ImageView) this.findViewById(R.id.ImageView_button_back);
		ivPic = (ImageView) this.findViewById(R.id.ImageView_destination_pic);
		phoneLayout = (LinearLayout) this
				.findViewById(R.id.LinearLayout_detail_tel);
		showOnMapLayout = (LinearLayout) this
				.findViewById(R.id.LinearLayout_detail_map);
		tvfavorites = (TextView) this
				.findViewById(R.id.TextView_panelBottom_detail_favorites);
		tvcheckin = (TextView) this
				.findViewById(R.id.TextView_panelBottom_detail_addto_this_trip);
		tvshare = (TextView) this
				.findViewById(R.id.TextView_panelBottom_detail_share);
		panelBottom = (LinearLayout) findViewById(R.id.Layout_detail_panelBottom);
		loadingLayout = (LinearLayout) findViewById(R.id.loadingbar);
		commentLoadingbar = (LinearLayout) findViewById(R.id.comment_loadingbar);
		contentLayout = (LinearLayout) findViewById(R.id.LinearLayout_content);
		detailSummaryLayout = (LinearLayout) findViewById(R.id.LinearLayout_detail_summary);
		tvName = (TextView) this.findViewById(R.id.ListView_destination_name);
		// sroce
		rbSroce = (RatingBar) this
				.findViewById(R.id.ratingBar_destination_sroce);
		// price info
		tvPrice = (TextView) this.findViewById(R.id.ListView_destination_price);
		// address
		tvAdress = (TextView) this.findViewById(R.id.TextView_detail_address);
		// tel
		tvTel = (TextView) this.findViewById(R.id.TextView_detail_tel);
		// introduction_name
		tvName4introduction = (TextView) this
				.findViewById(R.id.TextView_introduction_name);
		// introduction
		tvIntroduction = (TextView) this
				.findViewById(R.id.TextView_introduction_detail);
		// preferentialInfo
		tvPreferentialInfo = (TextView) this
				.findViewById(R.id.TextView_preferentialInfo_detail);
		commentContent = (LinearLayout)findViewById(R.id.LinearLayout_comment);
		writeCommentImageView = (ImageView)findViewById(R.id.ImageView_write_comment);
	}

	private void setViewListener() {
		iBack.setOnClickListener(this);
		ivPic.setOnClickListener(this);
		phoneLayout.setOnClickListener(this);
		showOnMapLayout.setOnClickListener(this);
		tvfavorites.setOnClickListener(this);
		tvcheckin.setOnClickListener(this);
		tvshare.setOnClickListener(this);
		commentContent.setOnClickListener(this);
		writeCommentImageView.setOnClickListener(this);
		// panelBottom.setOnClickListener(this);
		// loadingLayout.setOnClickListener(this);
		// commentLoadingbar.setOnClickListener(this);

	}

	private View createDetailContent() {
		return null;
	}

	private void initView() {
		loadingLayout.setVisibility(View.VISIBLE);
		commentLoadingbar.setVisibility(View.VISIBLE);
		detailSummaryLayout.setVisibility(View.GONE);
		panelBottom.setVisibility(View.GONE);
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			desid = extras.getLong("destinationId");
		} else {
			// 返回错误？
		}
		getCommentInit();
		new Thread(new getDataThread(getRequest("detail"))).start();

	}
	/**
	 * 获取评论
	 */
	private void getCommentInit() {
		//评论显示模块(错误消息+评论内容)
		LinearLayout detailComment = (LinearLayout)findViewById(R.id.LinearLayout_comment_block);
		detailComment.setVisibility(View.VISIBLE);
		//获取评论
		new Thread(new getDataThread(getRequest("comment"))).start();
	}
	private Request getRequest(String type) {
		Request request = null;
		if (type.equals("detail")) {
			par.clear();
			par.put("destinationId", "" + desid);
			request = new Request(NEARBY_DETAIL_URL, this, par, this.detailnum);
		}
		if (type.equals("comment")) {
			par.clear();
			par.put("destinationId", "" + desid);
			request = new Request(GET_COMMENT, this, par, this.commentnum);
		}
		return request;
	}

	/**
	 * 获取详细信息
	 */
	Handler mountailVillaHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Bundle data = msg.getData();
			int type = data.getInt("type");
			switch (type) {
			case 1:
				showDetail(data.getString("result"));
				break;
			case 2:
				showComment(data.getString("result"));
				break;
			case 3:
				ivPic.setImageBitmap(pic);
		        ivPic.invalidate();
				break;
			default:
				break;
			}

			/*
			 * data.get msg.getData() String res4detail =
			 * data.getString("detail");
			 * MountainVillaActivity.this.showDetail(res4detail);
			 * Log.i("mylog","请求结果为-->" + res4detail);
			 */
		}
	};

	/**
	 * 展示明细信息
	 */
	private void showDetail(String res4detail) throws JsonSyntaxException {
		if (res4detail != null) {
			try {
				Gson gson = new Gson();
				destination = gson.fromJson(res4detail,
						new TypeToken<Destination>() {
						}.getType());
			} catch (JsonSyntaxException e) {
				// Log.d(TAG, "解析明细信息失败", e);
				// Tools.showToastShort(this, );
				this.finish();
			}

			if (destination != null) {
				// name
				tvName.setText(destination.getScenerySummary().getName());
				// sroce
				rbSroce.setRating(destination.getScenerySummary().getScore());
				// price info
				tvPrice.setText(destination.getScenerySummary().getPriceInfo());
				// address
				tvAdress.setText(destination.getScenerySummary().getAddress());
				// tel
				tvTel.setText(destination.getScenerySummary().getTel());
				// introduction_name
				tvName4introduction.setText(destination.getScenerySummary()
						.getName());
				// introduction
				tvIntroduction.setText(destination.getIntroduction());
				// preferentialInfo
				tvPreferentialInfo.setText(destination.getPreferentialInfo());
				// pic

				picUrl = destination.getScenerySummary().getPic();
				new Thread(new getPicThread()).start();

				// 显示画面，加载bar去掉
				loadingLayout.setVisibility(View.GONE);
				detailSummaryLayout.setVisibility(View.VISIBLE);
				panelBottom.setVisibility(View.VISIBLE);
			}
		}

	}
	/**
	 * 获取点评失败时，页面如何显示
	 */
	private void showGetCommentError() {
		this.findViewById(R.id.find_comment_error).setVisibility(View.VISIBLE);
		commentLoadingbar.setVisibility(View.GONE);
	}
	private void showComment(String res4Comment) throws JsonSyntaxException{
		if (res4Comment != null) {
			try {
				Gson gson = new Gson();
				commentList = gson.fromJson(res4Comment, 
						new TypeToken<List<Comment>>() {}.getType());
			} catch (JsonSyntaxException e) {
				Log.e(TAG, "解析获取点评信息失败", e);
				showGetCommentError();
				return;
			}
		}
		//没有找到评论时
		if(commentList == null || commentList.size() <= 0) {
			commentLoadingbar.setVisibility(View.GONE);
			findViewById(R.id.LinearLayout_comment_block).setVisibility(View.GONE);
			return;
		}
		
		//展示最近一条评论
		Comment comment = commentList.get(0);
		//评分
		RatingBar ratingBar = (RatingBar)findViewById(R.id.ratingBar_comment_sroce);
		ratingBar.setRating(comment.getComment_score());
		//内容
		TextView text = (TextView)findViewById(R.id.TextView_comment_text);
		text.setText(comment.getContent());
		//时间
		TextView time = (TextView)findViewById(R.id.TextView_comment_time);
		time.setText(comment.getComment_time());
		
		//显示画面，加载bar去掉
		commentLoadingbar.setVisibility(View.GONE);
		commentContent.setVisibility(View.VISIBLE);
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId() == iBack.getId()) {
			finish();
		}
		if (v.getId() == ivPic.getId()) {
			if (destination.getPics() == null
					|| destination.getPics().length <= 0) {
				Tools.showToastLong(this, Constants.NO_DETAIL_PICTURE);
				return;
			}
			Intent intent = new Intent();
			intent.setClass(this, DetailGridPicActivity.class);
			// 传递商家详细信息
			intent.putExtra(Constants.DETAIL_PICTURE_SHOW,
					destination.getPics());
			startActivity(intent);
		}
		if (v.getId() == phoneLayout.getId()) {
			TextView tvTel = (TextView) findViewById(R.id.TextView_detail_tel);
			String callee = tvTel.getText().toString();
			Log.d("phone test", callee);
			// 判断电话号码是否合理
			if (PhoneNumberUtils.isGlobalPhoneNumber(callee)) {
				// 弹出拨号画面，待用户确认是否拨号
				Intent callIntent = new Intent(Intent.ACTION_DIAL,
						Uri.parse("tel://" + callee));
				startActivity(callIntent);
			} else {
				Toast.makeText(this, R.string.detail_error_phoneno,
						Toast.LENGTH_LONG).show();
			}
		}
		if (v.getId() == showOnMapLayout.getId()) {
			if (!Tools.checkNetworkStatus(this)) {
				return;
			}
			// 将要显示的内容传到地图页面
			Intent toMapIntent = new Intent(this, DetailOnMapActivity.class);
			toMapIntent.putExtra(Constants.MAP_SHOW_SPOT, destination);
			startActivity(toMapIntent);
		}
		if(v.getId() == commentContent.getId())
		{
			if(!Tools.checkNetworkStatus(this)) {
        		return;
        	}
        	Intent toMoreCommentIntent = new Intent(this, CommentShowActivity.class);
//        	toMoreCommentIntent.putExtra(Constants.MORE_COMMENT, commentList);
        	toMoreCommentIntent.putExtra(Constants.DESTINATION_ID, getIntent().getExtras().getLong("destinationId"));
        	toMoreCommentIntent.putExtra("destinationName", destination.getScenerySummary().getName());
        	//toMoreCommentIntent.putExtra("nearbyType", detailType);
            startActivity(toMoreCommentIntent);
		}
		if(v.getId() == writeCommentImageView.getId())
		{
			if(!Tools.checkNetworkStatus(this)) {
        		return;
        	}
        	Intent toWriteCommentIntent = new Intent(this, CommentWriteActivity.class);
        	toWriteCommentIntent.putExtra(Constants.DESTINATION_ID, getIntent().getExtras().getLong("destinationId"));
        	toWriteCommentIntent.putExtra("destinationName", destination.getScenerySummary().getName());
        	//toWriteCommentIntent.putExtra("nearbyType", detailType);
        	toWriteCommentIntent.putExtra("fromActivity", "detail");
            startActivity(toWriteCommentIntent);
		}
	}

	class getPicThread implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			pic = Tools.getBitmapFromUrl(picUrl);
			Message msg = new Message();
			Bundle data = new Bundle();
			data.putString("result", "ok");
			data.putInt("type", 2);
			msg.setData(data);
			MountainVillaActivity.this.mountailVillaHandler.sendMessage(msg);
		}

	}

	class getDataThread implements Runnable {
		Request request;

		public getDataThread(Request request) {
			this.request = request;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			String res = "";
			List<NameValuePair> param = new ArrayList<NameValuePair>();
			for (Entry<String, String> par : request.getPar().entrySet()) {
				param.add(new BasicNameValuePair(par.getKey(), par.getValue()));
			}
			try {
				res = HttpUtil.postUrl(request.getUrl(), param);
			} catch (ClientProtocolException e1) {
				//
				return;
			} catch (IOException e1) {
				//
				return;
			}
			Message msg = new Message();
			Bundle data = new Bundle();
			data.putString("result", res);
			data.putInt("type", request.getCallBack());
			msg.setData(data);
			MountainVillaActivity.this.mountailVillaHandler.sendMessage(msg);
		}

	}
}
