package com.f5.ourfarm.activity;

import static com.f5.ourfarm.util.URLConstants.NEARBY_DETAIL_URL;
import static com.f5.ourfarm.util.URLConstants.GET_AROUND_FARM_HOME;
import static com.f5.ourfarm.util.URLConstants.GET_COMMENT;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.f5.ourfarm.R;
import com.f5.ourfarm.model.Comment;
import com.f5.ourfarm.model.Destination;
import com.f5.ourfarm.model.MoreAroundFarm;
import com.f5.ourfarm.model.SinaWeiboErrorMsg;
import com.f5.ourfarm.sqlite.DestinationDbAdpter;
import com.f5.ourfarm.third.weibo.OWeibo;
import com.f5.ourfarm.third.weibo.OWeiboMessageListener;
import com.f5.ourfarm.util.AccessTokenKeeper;
import com.f5.ourfarm.util.Constants;
import com.f5.ourfarm.util.HttpUtil;
import com.f5.ourfarm.util.Tools;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.umeng.analytics.MobclickAgent;
import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.Weibo;
import com.weibo.sdk.android.WeiboAuthListener;
import com.weibo.sdk.android.WeiboDialogError;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.api.StatusesAPI;
import com.weibo.sdk.android.net.RequestListener;

public class DetailActivity extends BaseActivity implements RequestListener{
	
	//��ʾ�̼�ͼƬ
	OnClickListener showDetailPic = null;
	// �����ײ����4����ť
	OnClickListener lPanelBottom = null;
	// ��ͼ����ʾ
	OnClickListener showOnMap = null;
	// ���Ի�ȡ�ܱ�ũ����
	OnClickListener retryGetAroundFarm = null;
	// �ܱ�ũ����
	OnClickListener aroundFarm = null;
	// ����ũ����
	OnClickListener getMoreFarm = null;
	// ��������
	OnClickListener getMoreComment = null;
	// ���Ի�ȡ�ܱ�ũ����
    OnClickListener retryGetComment = null;
    // д����
    OnClickListener writeComment = null;
	
	String picUrl;
	Bitmap pic ;
	HashMap<String, Bitmap> pics = new HashMap<String ,Bitmap>();
    ArrayList<String> urls = new ArrayList<String>();
    
    //��ϸ��Ϣ
    Destination destination = null;
    DestinationDbAdpter destinationDbAdpter;
    private long destinationId;
    
    //loadingbar
    private LinearLayout loadingLayout;
    private LinearLayout moreFramLoadingbar;
    private LinearLayout commentLoadingbar;
    //��ϸ��Ϣ
    private LinearLayout detailSummaryLayout;
    //�ܱ�ũ����
    private RelativeLayout moreAroundFarmContent;
    //��������
    private LinearLayout commentContent;
    //�·��˵���
    private LinearLayout panelBottom;
    //��д����
    private ImageView writeCommentImageView;
    
    //�����ܱߵ�ũ����
    private ArrayList<MoreAroundFarm> farmList = null;
    //����
    private ArrayList<Comment> commentList = null;
    //���뵽��������Ϣ
    private int detailType;
    
    //��������΢��
    private Weibo mWeibo;
    public static Oauth2AccessToken accessToken ;
    private static final String REDIRECT_URL = "http://www.sina.com";
	// ע�⣡���˴���������appkey��appsecret����λ�ȡ����΢��appkey��appsecret�������ѯ�����Ϣ���˴���������
	private static final String CONSUMER_KEY = "2913772243";// �滻Ϊ�����ߵ�appkey������"1646212860";
	public static final String EXTRA_ACCESS_TOKEN = "com.weibo.android.accesstoken";
	public static final String EXTRA_EXPIRES_IN = "com.weibo.android.token.expires";
	public static final String WEIBO_TEXT = "weibo_text";
	private static final String TAG = "��ϸ��Ϣҳ��:";
	
	private static final String errMsg = "��ȡ������ϸ��Ϣʧ��";
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // ȥ��������
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        //׼��listeners
        this.prepareListeners();
        setContentView(R.layout.activity_destination_detail);
        //����listeners
        this.batchSetListeners();
        initStatus();
        
        new Thread(runnable4detail).start();
        
        //����΢��
        mWeibo = Weibo.getInstance(CONSUMER_KEY, REDIRECT_URL);
        DetailActivity.accessToken=AccessTokenKeeper.readAccessToken(this);
        
    }
	
	/**
	 * ��ʼҳ���״̬
	 */
	private void initStatus() {
		detailType = getIntent().getExtras().getInt("nearbyType");
		//loadingbar
        loadingLayout = (LinearLayout)findViewById(R.id.loadingbar);
        loadingLayout.setVisibility(View.VISIBLE);
        moreFramLoadingbar = (LinearLayout)findViewById(R.id.more_fram_loadingbar);
        moreFramLoadingbar.setVisibility(View.VISIBLE);
        commentLoadingbar = (LinearLayout)findViewById(R.id.comment_loadingbar);
        commentLoadingbar.setVisibility(View.VISIBLE);
        //��ϸ��Ϣ
        detailSummaryLayout = (LinearLayout)findViewById(R.id.LinearLayout_detail_summary);
        detailSummaryLayout.setVisibility(View.GONE);
        //�ܱ�ũ��Ժ����
        moreAroundFarmContent = (RelativeLayout)findViewById(R.id.more_around_farm);
        moreAroundFarmContent.setVisibility(View.GONE);
        //����
        commentContent.setVisibility(View.GONE);
        //�ײ��˵���
        panelBottom = (LinearLayout)findViewById(R.id.Layout_detail_panelBottom);
        panelBottom.setVisibility(View.GONE);
        
        //���������
        if(detailType == 1) {
        	getAroundFarmInit();
        } else if(detailType == 2) {//ũ���������
        	getCommentInit();
        } else if(detailType == 4) {//ũ��Ʒ�����
        	getCommentInit();
        } else {//���������
        	getCommentInit();
        }
        
        //
        destinationDbAdpter = new DestinationDbAdpter(this);
        destinationDbAdpter.open();
	}
	
	/**
	 * ��ȡũ����
	 */
	private void getAroundFarmInit() {
		//��ʾ�ܱ�ũ����ģ��(������Ϣ+�ܱ�ũ��Ժ����)
    	LinearLayout detailAroundFarm = (LinearLayout)findViewById(R.id.LinearLayout_detail_around_farm);
    	detailAroundFarm.setVisibility(View.VISIBLE);
    	findViewById(R.id.LinearLayout_comment).setVisibility(View.GONE);
    	writeCommentImageView.setVisibility(View.GONE);
    	//��ȡ�ܱ�ũ��Ժ
        new Thread(runnable4MoreAroundFarm).start();
	}
	
	/**
	 * ��ȡ����
	 */
	private void getCommentInit() {
		//������ʾģ��(������Ϣ+��������)
		LinearLayout detailComment = (LinearLayout)findViewById(R.id.LinearLayout_comment_block);
		detailComment.setVisibility(View.VISIBLE);
		//��ȡ����
		new Thread(runnable4Comment).start();
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
     * ��ȡ��ϸ��Ϣ
     */
    Runnable runnable4detail = new Runnable(){
        @Override
        public void run() {
            String res = "";
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                destinationId = extras.getLong("destinationId");
                List<NameValuePair> param = new ArrayList<NameValuePair>();
                BasicNameValuePair id = new BasicNameValuePair("destinationId", Long.toString(destinationId));
                param.add(id);
                try {
                    res = HttpUtil.postUrl(NEARBY_DETAIL_URL, param);
                } catch (ClientProtocolException e1) {
                    Log.e(TAG, errMsg, e1);
                } catch (IOException e1) {
                    Log.e(TAG, errMsg, e1);
                }
            }
           
            Message msg = new Message();
            Bundle data = new Bundle();
            data.putString("detail",res);
            msg.setData(data);
            DetailActivity.this.handler4detail.sendMessage(msg);
        }
    };
    
    /**
     * ��ȡ�ܱ�ũ��Ժ��Ϣ
     */
    Runnable runnable4MoreAroundFarm = new Runnable(){
    	@Override
    	public void run() {
    		String res = "";
    		Bundle extras = getIntent().getExtras();
    		if (extras != null) {
    			destinationId = extras.getLong("destinationId");
    			List<NameValuePair> param = new ArrayList<NameValuePair>();
    			BasicNameValuePair id = new BasicNameValuePair("view_id", Long.toString(destinationId));
    			param.add(id);
    			//��1������
    	        BasicNameValuePair count = new BasicNameValuePair("count", "1");
    	        param.add(count);
    			try {
    				res = HttpUtil.postUrl(GET_AROUND_FARM_HOME, param);
    			} catch (ClientProtocolException e1) {
    				Log.e(TAG, errMsg, e1);
    			} catch (IOException e1) {
    				Log.e(TAG, errMsg, e1);
    			}
    		}
    		
    		Message msg = new Message();
    		Bundle data = new Bundle();
    		data.putString("moreAroundFarm",res);
    		msg.setData(data);
    		DetailActivity.this.handler4MoreAroundFarm.sendMessage(msg);
    	}
    };
    /**
     * ��ȡũ��Ժ��������Ϣ
     */
    Runnable runnable4Comment = new Runnable(){
    	@Override
    	public void run() {
    		String res = "";
    		Bundle extras = getIntent().getExtras();
    		if (extras != null) {
    			destinationId = extras.getLong("destinationId");
    			List<NameValuePair> param = new ArrayList<NameValuePair>();
    			BasicNameValuePair id = new BasicNameValuePair("destination_id", Long.toString(destinationId));
    			param.add(id);
    			//��1������
    			BasicNameValuePair count = new BasicNameValuePair("count", "1");
    			param.add(count);
    			try {
    				res = HttpUtil.postUrl(GET_COMMENT, param);
    			} catch (ClientProtocolException e1) {
    				Log.e(TAG, errMsg, e1);
    				showGetCommentError();
    				return;
    			} catch (IOException e1) {
    				Log.e(TAG, errMsg, e1);
    				showGetCommentError();
    				return;
    			}
    		}
    		
    		Message msg = new Message();
    		Bundle data = new Bundle();
    		data.putString("comment",res);
    		msg.setData(data);
    		DetailActivity.this.handler4Comment.sendMessage(msg);
    	}
    };
    
    /**
     * ��ȡ��ϸ��Ϣ
     */
    Handler handler4detail = new  Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String res4detail = data.getString("detail");
            DetailActivity.this.showDetail(res4detail);
            Log.i("mylog","������Ϊ-->" + res4detail);
        }
    };
    
    /**
     * ��ȡ�ܱ�ũ����
     */
    Handler handler4MoreAroundFarm = new  Handler() {
    	@Override
    	public void handleMessage(Message msg) {
    		super.handleMessage(msg);
    		Bundle data = msg.getData();
    		String res4MoreAroundFarm = data.getString("moreAroundFarm");
    		DetailActivity.this.showMoreAroundFarm(res4MoreAroundFarm);
    		Log.d(TAG,"�ܱ�ũ����-->" + res4MoreAroundFarm);
    	}
    };
    
    /**
     * ��ȡ����
     */
    Handler handler4Comment = new  Handler() {
    	@Override
    	public void handleMessage(Message msg) {
    		super.handleMessage(msg);
    		Bundle data = msg.getData();
    		String res4Comment = data.getString("comment");
    		DetailActivity.this.showComment(res4Comment);
    		Log.d(TAG,"��������-->" + res4Comment);
    	}
    };
    
	/**
     * չʾ��ϸ��Ϣ
     */
	private void showDetail(String res4detail) throws JsonSyntaxException{
		if (res4detail != null) {
			try {
				Gson gson = new Gson();
				destination = gson.fromJson(res4detail,
						new TypeToken<Destination>() {
						}.getType());
			} catch (JsonSyntaxException e) {
				Log.d(TAG, "������ϸ��Ϣʧ��", e);
                Tools.showToastShort(DetailActivity.this, errMsg);
                DetailActivity.this.finish();
			}
			
			if (destination != null) {
				// name
				TextView tvName = (TextView) this
						.findViewById(R.id.ListView_destination_name);
				tvName.setText(destination.getScenerySummary().getName());
				// sroce
				RatingBar rbSroce = (RatingBar) this
						.findViewById(R.id.ratingBar_destination_sroce);
				rbSroce.setRating(destination.getScenerySummary().getScore());
				// price info
				TextView tvPrice = (TextView) this
						.findViewById(R.id.ListView_destination_price);
				tvPrice.setText(destination.getScenerySummary().getPriceInfo());
				// address
				TextView tvAdress = (TextView) this
						.findViewById(R.id.TextView_detail_address);
				tvAdress.setText(destination.getScenerySummary().getAddress());
				// tel
				TextView tvTel = (TextView) this
						.findViewById(R.id.TextView_detail_tel);
				tvTel.setText(destination.getScenerySummary().getTel());
				//��ȡ�绰��ʾ��һ��
        		LinearLayout phoneLayout = (LinearLayout) this
        				.findViewById(R.id.LinearLayout_detail_tel);
        		phoneLayout.setTag(destination.getScenerySummary().getTel());
        		phoneLayout.setOnClickListener(phoneCall);
        		
				// introduction_name
				TextView tvName4introduction = (TextView) this
						.findViewById(R.id.TextView_introduction_name);
				tvName4introduction.setText(destination.getScenerySummary()
						.getName());
				// introduction
				TextView tvIntroduction = (TextView) this
						.findViewById(R.id.TextView_introduction_detail);
				tvIntroduction.setText(destination.getIntroduction());
				// preferentialInfo
				TextView tvPreferentialInfo = (TextView) this
						.findViewById(R.id.TextView_preferentialInfo_detail);
				tvPreferentialInfo.setText(destination.getPreferentialInfo());
				//pic
				picUrl = destination.getScenerySummary().getPic();
                new Thread(runnable4pic).start();
                
                //��ʾ���棬����barȥ��
                loadingLayout.setVisibility(View.GONE);
                detailSummaryLayout.setVisibility(View.VISIBLE);
                panelBottom.setVisibility(View.VISIBLE);
			}
		}

	}
	
	/**
     * չʾ�ܱ�ũ������Ϣ
     */
	private void showMoreAroundFarm(String res4MoreAroundFarm) throws JsonSyntaxException{
		if (res4MoreAroundFarm != null) {
			try {
				Gson gson = new Gson();
				farmList = gson.fromJson(res4MoreAroundFarm, 
	            		new TypeToken<List<MoreAroundFarm>>() {}.getType());
			} catch (JsonSyntaxException e) {
				Log.e(TAG, "������ȡ�ܱ�ũ������Ϣʧ��", e);
                showGetFarmError();
                return;
			}
		}
		//û���ҵ�ũ����ʱ
		if(farmList == null || farmList.size() <= 0) {
			showCanNotGetFarm();
			return;
		} else if (farmList.size() < 4) {
			//С��4��ʱ������ʾ���ఴť
			this.findViewById(R.id.get_more_farm).setVisibility(View.GONE);
		}
		
		//TODO ����д���о��������Ҫ�ع�
        for(int i = 0; i < farmList.size(); i++){
        	//ֻ��ʾǰ3��
        	if(i == 3) break;
        	if(i == 0) {
        		setecommendAroundFarm(farmList.get(i), 
        				(ImageView) this.findViewById(R.id.ImageView_recommend_farm_pic1),
        				(TextView) this.findViewById(R.id.TextView_detail_recommend_farm1));
        		this.findViewById(R.id.RelativeLayout_recommend_farm1).setVisibility(View.VISIBLE);
        	} else if(i == 1) {
        		setecommendAroundFarm(farmList.get(i), 
        				(ImageView) this.findViewById(R.id.ImageView_recommend_farm_pic2),
        				(TextView) this.findViewById(R.id.TextView_detail_recommend_farm2));
        		this.findViewById(R.id.RelativeLayout_recommend_farm2).setVisibility(View.VISIBLE);
        	} else if(i == 2) {
        		setecommendAroundFarm(farmList.get(i), 
        				(ImageView) this.findViewById(R.id.ImageView_recommend_farm_pic3),
        				(TextView) this.findViewById(R.id.TextView_detail_recommend_farm3));
        		this.findViewById(R.id.RelativeLayout_recommend_farm3).setVisibility(View.VISIBLE);
        	}
        	
        }
        
        //��ʾ���棬����barȥ��
        moreFramLoadingbar.setVisibility(View.GONE);
        moreAroundFarmContent.setVisibility(View.VISIBLE);
	}
	
	/**
	 * չʾƷ����Ϣ
	 */
	private void showComment(String res4Comment) throws JsonSyntaxException{
		if (res4Comment != null) {
			try {
				Gson gson = new Gson();
				commentList = gson.fromJson(res4Comment, 
						new TypeToken<List<Comment>>() {}.getType());
			} catch (JsonSyntaxException e) {
				Log.e(TAG, "������ȡ������Ϣʧ��", e);
				showGetCommentError();
				return;
			}
		}
		//û���ҵ�����ʱ
		if(commentList == null || commentList.size() <= 0) {
			commentLoadingbar.setVisibility(View.GONE);
			findViewById(R.id.LinearLayout_comment_block).setVisibility(View.GONE);
			return;
		}
		
		//չʾ���һ������
		Comment comment = commentList.get(0);
		//����
		RatingBar ratingBar = (RatingBar)findViewById(R.id.ratingBar_comment_sroce);
		ratingBar.setRating(comment.getComment_score());
		//����
		TextView text = (TextView)findViewById(R.id.TextView_comment_text);
		text.setText(comment.getContent());
		//ʱ��
		TextView time = (TextView)findViewById(R.id.TextView_comment_time);
		time.setText(comment.getComment_time());
		
		//��ʾ���棬����barȥ��
		commentLoadingbar.setVisibility(View.GONE);
		commentContent.setVisibility(View.VISIBLE);
	}
	
	/**
	 * �����Ƽ����ܱ�ũ����
	 * 
	 * @param moreAroundFarm ��ȡ���� moreAroundFarm����
	 * @param farmPic ����ͼƬ
	 * @param farmName ��������
	 */
	private void setecommendAroundFarm(MoreAroundFarm moreAroundFarm, ImageView farmPic, TextView farmName) {
		
		String picUrl = moreAroundFarm.getSummary().getPic();
        Bitmap bitmap = Tools.getBitmapFromUrl(picUrl);
        
        if(bitmap != null){
            farmPic.setImageBitmap(bitmap);
        }
        farmName.setText(moreAroundFarm.getSummary().getName());
        
        farmPic.setTag(moreAroundFarm.getFarmhome_id());
        farmPic.setOnClickListener(aroundFarm);
	}
	
	/**
	 * �����ܱ�û��ũ����
	 */
	private void showCanNotGetFarm() {
		this.findViewById(R.id.cannot_find_fram).setVisibility(View.VISIBLE);
		moreFramLoadingbar.setVisibility(View.GONE);
	}
	
	/**
	 * ��ȡ�ܱ�ũ����ʧ��ʱ��ҳ�������ʾ
	 */
	private void showGetFarmError() {
		this.findViewById(R.id.find_around_farm_error).setVisibility(View.VISIBLE);
		moreFramLoadingbar.setVisibility(View.GONE);
	}
	
	/**
	 * ��ȡ����ʧ��ʱ��ҳ�������ʾ
	 */
	private void showGetCommentError() {
		this.findViewById(R.id.find_comment_error).setVisibility(View.VISIBLE);
		commentLoadingbar.setVisibility(View.GONE);
	}
	
    private void setPic(Bitmap pic) {
        ImageView ivPic = (ImageView) this
                .findViewById(R.id.ImageView_destination_pic);
        ivPic.setImageBitmap(pic);
        ivPic.invalidate();
    }

	/**
	 * �������¼���Ķ�����
	 */
	private void prepareListeners() {
		
		// �̼�ͼƬ
		showDetailPic = new OnClickListener() {
			public void onClick(View v) {
				if(destination.getPics() == null || destination.getPics().length <=0) {
					Tools.showToastLong(DetailActivity.this, Constants.NO_DETAIL_PICTURE);
					return;
				}
				Intent intent=new Intent();
				intent.setClass(DetailActivity.this, DetailGridPicActivity.class);
				//�����̼���ϸ��Ϣ
				intent.putExtra(Constants.DETAIL_PICTURE_SHOW, destination.getPics());
		        
				startActivity(intent);
			}
		};
		
        //�ڵ�ͼ����ʾ���������λ��
        showOnMap = new OnClickListener(){
            public void onClick(View v) {
            	// �ж������Ƿ���ã�������������ʾ�û�������������ת
            	if(!Tools.checkNetworkStatus(DetailActivity.this)) {
            		return;
            	}
            	//��Ҫ��ʾ�����ݴ�����ͼҳ��
                Intent toMapIntent = new Intent(DetailActivity.this, DetailOnMapActivity.class);
                toMapIntent.putExtra(Constants.MAP_SHOW_SPOT, destination);
                startActivity(toMapIntent);
            } 
        };
        
        //���Ի�ȡ�ܱ�ũ����
        retryGetAroundFarm = new OnClickListener(){
            public void onClick(View v) {
            	// �ж������Ƿ���ã�������������ʾ�û�������������ת
            	if(!Tools.checkNetworkStatus(DetailActivity.this)) {
            		return;
            	}
            	DetailActivity.this.findViewById(R.id.find_around_farm_error).setVisibility(View.GONE);
            	moreFramLoadingbar.setVisibility(View.VISIBLE);
            	//��ȡ�ܱ�ũ��Ժ
                new Thread(runnable4MoreAroundFarm).start();
            } 
        };

        //details
        aroundFarm = new OnClickListener(){
            public void onClick(View v) {
            	// �ж������Ƿ���ã�������������ʾ�û�������������ת
            	if(!Tools.checkNetworkStatus(DetailActivity.this)) {
            		return;
            	}
                long destinationId =  (Long) v.getTag(); 
                Intent nearby2detail = new Intent(DetailActivity.this,DetailActivity.class);
                //���������������ݱ�����Intent��ȷ�����ݿ���activity�䴫��
                nearby2detail.putExtra("destinationId", destinationId);
                //�����жϵ��������������
                nearby2detail.putExtra("nearbyType", 2);
                
                startActivity(nearby2detail);
            } 
        };
        //����ũ����
        getMoreFarm = new OnClickListener(){
            public void onClick(View v) {
            	// �ж������Ƿ���ã�������������ʾ�û�������������ת
            	if(!Tools.checkNetworkStatus(DetailActivity.this)) {
            		return;
            	}
            	Intent toAroundFarmIntent = new Intent(DetailActivity.this, AroundFarmActivity.class);
            	toAroundFarmIntent.putExtra(Constants.AROUND_FARM, farmList);
                startActivity(toAroundFarmIntent);
            } 
        };
        
        //��������
        getMoreComment = new OnClickListener(){
            public void onClick(View v) {
            	// �ж������Ƿ���ã�������������ʾ�û�������������ת
            	if(!Tools.checkNetworkStatus(DetailActivity.this)) {
            		return;
            	}
            	Intent toMoreCommentIntent = new Intent(DetailActivity.this, CommentShowActivity.class);
//            	toMoreCommentIntent.putExtra(Constants.MORE_COMMENT, commentList);
            	toMoreCommentIntent.putExtra(Constants.DESTINATION_ID, getIntent().getExtras().getLong("destinationId"));
            	toMoreCommentIntent.putExtra("destinationName", destination.getScenerySummary().getName());
            	toMoreCommentIntent.putExtra("nearbyType", detailType);
                startActivity(toMoreCommentIntent);
            } 
        };
        
        //���Ի�ȡ����
        retryGetComment = new OnClickListener(){
        	public void onClick(View v) {
        		// �ж������Ƿ���ã�������������ʾ�û�������������ת
        		if(!Tools.checkNetworkStatus(DetailActivity.this)) {
        			return;
        		}
        		DetailActivity.this.findViewById(R.id.find_comment_error).setVisibility(View.GONE);
        		commentLoadingbar.setVisibility(View.VISIBLE);
        		//��ȡ����
        		new Thread(runnable4Comment).start();
        	} 
        };
        
        //д����
        writeComment = new OnClickListener(){
            public void onClick(View v) {
            	// �ж������Ƿ���ã�������������ʾ�û�������������ת
            	if(!Tools.checkNetworkStatus(DetailActivity.this)) {
            		return;
            	}
            	Intent toWriteCommentIntent = new Intent(DetailActivity.this, CommentWriteActivity.class);
            	toWriteCommentIntent.putExtra(Constants.DESTINATION_ID, getIntent().getExtras().getLong("destinationId"));
            	toWriteCommentIntent.putExtra("destinationName", destination.getScenerySummary().getName());
            	toWriteCommentIntent.putExtra("nearbyType", detailType);
            	toWriteCommentIntent.putExtra("fromActivity", "detail");
                startActivity(toWriteCommentIntent);
            } 
        };
		
		// �ײ����4����ť���л�
		lPanelBottom = new OnClickListener() {
			public void onClick(View v) {
				//
				int tag = (Integer) v.getTag();
				switch (tag) {
				case 1:
				    destinationDbAdpter.addFavorite(destinationId);
				    //�жϱ����Ƿ�洢
				    Cursor cursor = destinationDbAdpter.getDestination(destinationId);
				    if(cursor.getCount() == 0 && destination != null){  
				        destinationDbAdpter.insertDestination(destination, false);
				    }
				    //��ӳɹ�
				    Tools.showToastShort(DetailActivity.this, Constants.ADD_FAVORITE);
					break;
				case 2:
					addToThisTrip();
					break;
				case 3:
					break;
				case 4:
                	// �ж������Ƿ���ã�������������ʾ�û�������������ת
                	if(!Tools.checkNetworkStatus(DetailActivity.this)) {
                		return;
                	}
					//��������΢��
                	//����΢����Ϣ
            		if(destination != null) {
            			StringBuffer weiboText = new StringBuffer(Constants.WEIBO_TEXT_COMMON);
            			weiboText.append(destination.getScenerySummary().getName()).append("\r\n").
            			    append("��ַ: ").append(destination.getScenerySummary().getAddress()).append("\r\n").
            			    append("�绰: ").append(destination.getScenerySummary().getTel());
            			OWeibo.sendToWeibo(DetailActivity.this,weiboText.toString(),new OWeiboMessageListener(DetailActivity.this));
            		}
                	
					//shearToSinaWeibo();
					break;
				}
			}
		};
	}
	
	/**
	 * ��view�ͼ���
	 */
	private void batchSetListeners() {
		ImageView iback = (ImageView) this
				.findViewById(R.id.ImageView_button_back);
		iback.setOnClickListener(lback);

		//ͼƬ���
        ImageView ivPic = (ImageView) this
                .findViewById(R.id.ImageView_destination_pic);
        ivPic.setOnClickListener(showDetailPic);
        
		//��ȡ��ͼ��ʾ��һ��
		LinearLayout showOnMapLayout = (LinearLayout) this
				.findViewById(R.id.LinearLayout_detail_map);
		showOnMapLayout.setOnClickListener(showOnMap);
		//��ȡ���Ի�ȡ�ܱ�ũ����
		Button retryFarmBtn = (Button) this.findViewById(R.id.retry_around_farm_btn);
		retryFarmBtn.setOnClickListener(retryGetAroundFarm);
		//��ȡ�����ܱ�ũ����
		RelativeLayout moreFarm = (RelativeLayout) this.findViewById(R.id.get_more_farm);
		moreFarm.setOnClickListener(getMoreFarm);
		//��ȡ������������
		Button retryCommentBtn = (Button) this.findViewById(R.id.retry_comment_btn);
		retryCommentBtn.setOnClickListener(retryGetComment);
		
		//��������
		commentContent = (LinearLayout)findViewById(R.id.LinearLayout_comment);
		commentContent.setOnClickListener(getMoreComment);
		
		//��д���۰�ť
        writeCommentImageView = (ImageView)findViewById(R.id.ImageView_write_comment);
        writeCommentImageView.setOnClickListener(writeComment);
		
		// �ײ����4����ť
		TextView tvfavorites = (TextView) this
				.findViewById(R.id.TextView_panelBottom_detail_favorites);
		tvfavorites.setTag(1);
		tvfavorites.setOnClickListener(lPanelBottom);

		TextView tvcheckin = (TextView) this
				.findViewById(R.id.TextView_panelBottom_detail_addto_this_trip);
		tvcheckin.setTag(2);
		tvcheckin.setOnClickListener(lPanelBottom);

		TextView tvshare = (TextView) this
				.findViewById(R.id.TextView_panelBottom_detail_share);
		tvshare.setTag(4);
		tvshare.setOnClickListener(lPanelBottom);
	}
	
	Handler handler4pic = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String res = data.getString("value");
            Log.d("mylog","������Ϊ-->" + res);
            setPic(pic);
        }
        
    };

    Runnable runnable4pic = new Runnable(){
        @Override
        public void run() {
            Log.d("a", "a");
            pic = Tools.getBitmapFromUrl(picUrl);
            Message msg = new Message();
            Bundle data = new Bundle();
            data.putString("value","ok");
            msg.setData(data);
            handler4pic.sendMessage(msg);
        }
        
    };
	
	Handler handler4loadpic = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String res = data.getString("value");
            Log.i("mylog","������Ϊ-->" + res);
        }
    };

    Runnable runnable4loadpic = new Runnable(){
        @Override
        public void run() {
            Log.d("a", "a");
            Iterator<String> iterator = urls.iterator();
            try {
	            while(iterator.hasNext()) {
	                String picUrl = iterator.next();
	                Bitmap bitmap = Tools.getBitmapFromUrl(picUrl);
	                pics.put(picUrl, bitmap);
	            }
	        } catch (Exception e) {
	    		Log.e(TAG, "����ͼƬʱ����", e);
	    	}
            Message msg = new Message();
            Bundle data = new Bundle();
            data.putString("value","ok");
            msg.setData(data);
            handler4loadpic.sendMessage(msg);
        }
        
    };
    
	/**
	 * ��������΢��
	 */
	private void shearToSinaWeibo() {
		//���ƻ�û�й��ڣ����Լ���ʹ��
		if(DetailActivity.accessToken.isSessionValid()){
			// ֱ��ȥ��΢��
			toSharePage();
		} else {//���»������
			mWeibo.authorize(DetailActivity.this, new AuthDialogListener());
		}
		
	}
	
	/**
	 * ��ӵ������г�
	 */
	private void addToThisTrip() {
		//�ж��Ƿ��Ѿ���ӵ��˱����г���
		Cursor mCursor = destinationDbAdpter.getTrisTripWithId(destinationId);
		if(mCursor != null && mCursor.getCount() > 0) {
			//��ӳɹ�
		    Tools.showToastShort(DetailActivity.this, Constants.ADD_THIS_TRIP);
		    return;
		}
		
		int maxSortValue = destinationDbAdpter.getMaxSortThisTrip();
		
		destinationDbAdpter.addThisTrip(destinationId, maxSortValue + 1);
	    //�жϱ����Ƿ�洢�����û�У��ͽ��ü�¼��ӵ�����
	    Cursor cursor = destinationDbAdpter.getDestination(destinationId);
	    if(cursor.getCount() == 0 && destination != null){  
	        destinationDbAdpter.insertDestination(destination, false);
	    }
	    //��ӳɹ�
	    Tools.showToastShort(DetailActivity.this, Constants.ADD_THIS_TRIP);
	}
	
	/**
	 * ǩ��������΢��
	 */
	private void checkInToSinaWeibo() {
//		mWeibo.authorize(DetailActivity.this, new AuthDialogListener());
		//���ƻ�û�й��ڣ����Լ���ʹ��
		if(DetailActivity.accessToken.isSessionValid()){
			// ֱ��ȥ��΢��
			StatusesAPI api = new StatusesAPI(DetailActivity.accessToken);
			if (!TextUtils.isEmpty(DetailActivity.EXTRA_ACCESS_TOKEN)) {
				String mContent = "@���ο�΢�� ������" + destination.getScenerySummary().getAddress();
				
				// Just update a text weibo!
				api.update(mContent, String.valueOf(destination.getScenerySummary().getLat()), 
						String.valueOf(destination.getScenerySummary().getLng()), this);
			} else {
				Toast.makeText(this, this.getString(R.string.weibosdk_please_login),
						Toast.LENGTH_LONG).show();
			}
		} else {//���»������
			mWeibo.authorize(DetailActivity.this, new AuthDialogListener());
		}
	}
    
	class AuthDialogListener implements WeiboAuthListener {

		@Override
		public void onComplete(Bundle values) {
			String token = values.getString("access_token");
			String expires_in = values.getString("expires_in");
			//���¼���token
			DetailActivity.accessToken = new Oauth2AccessToken(token, expires_in);
			if (DetailActivity.accessToken.isSessionValid()) {
				String date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new java.util.Date(DetailActivity.accessToken.getExpiresTime()));
				Log.d(TAG, "Token����ʱ�䣺" + date);
				AccessTokenKeeper.keepAccessToken(DetailActivity.this, accessToken);
				Toast.makeText(DetailActivity.this, "��֤�ɹ�", Toast.LENGTH_SHORT).show();
				//��ת������ҳ��
				toSharePage();
			} else {
				Toast.makeText(DetailActivity.this, "����΢����֤ʧ��", Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		public void onError(WeiboDialogError e) {
			Toast.makeText(getApplicationContext(), "Auth error : " + e.getMessage(),
					Toast.LENGTH_LONG).show();
		}

		@Override
		public void onCancel() {
			Toast.makeText(getApplicationContext(), "Auth cancel", Toast.LENGTH_LONG).show();
		}

		@Override
		public void onWeiboException(WeiboException e) {
			Toast.makeText(getApplicationContext(), "Auth exception : " + e.getMessage(),
					Toast.LENGTH_LONG).show();
		}

	}
	
	/**
	 * ��ת������ҳ��
	 */
	private void toSharePage() {
		Intent intent=new Intent();
		intent.setClass(DetailActivity.this, DetailShareActivity.class);
		intent.putExtra(DetailActivity.EXTRA_ACCESS_TOKEN, DetailActivity.accessToken.getToken());
		intent.putExtra(DetailActivity.EXTRA_EXPIRES_IN, DetailActivity.accessToken.getExpiresTime());
        
		//����΢����Ϣ
		if(destination != null) {
			StringBuffer weiboText = new StringBuffer(Constants.WEIBO_TEXT_COMMON);
			weiboText.append(destination.getScenerySummary().getName()).append("\r\n").
			    append("��ַ: ").append(destination.getScenerySummary().getAddress()).append("\r\n").
			    append("�绰: ").append(destination.getScenerySummary().getTel());
			intent.putExtra(DetailActivity.WEIBO_TEXT, weiboText.toString());
			
			intent.putExtra(Constants.LOCATION_LNG, destination.getScenerySummary().getLat());//γ��
			intent.putExtra(Constants.LOCATION_LAT, destination.getScenerySummary().getLng());//����
		}
		
		startActivity(intent);
	}
	
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

	@Override
	public void onComplete(String arg0) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(DetailActivity.this, R.string.weibosdk_checkin_sucess, Toast.LENGTH_LONG)
						.show();
			}
		});
	}

	@Override
	public void onError(final WeiboException e) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				try {
					Gson gson = new Gson();
					SinaWeiboErrorMsg errmsg = gson.fromJson(e.getMessage(),
							new TypeToken<SinaWeiboErrorMsg>() {}.getType());
					//��������ͬ��΢�������ﴦ��ɷ����ɹ�
					if(errmsg.getError_code() == 20019) {
						Tools.showToastShort(DetailActivity.this, 
								DetailActivity.this.getString(R.string.weibosdk_checkin_sucess) );
					} else {
						Tools.showToastShort(DetailActivity.this, DetailActivity.this.getString(R.string.weibosdk_checkin_failed));
						Log.e(TAG, e.getMessage());
					}
				} catch (JsonSyntaxException e) {
	                Tools.showToastShort(DetailActivity.this, DetailActivity.this.getString(R.string.weibosdk_checkin_failed));
	                Log.e(TAG, e.getMessage());
				}
			}
		});
	}

	@Override
	public void onIOException(IOException arg0) {
		// TODO Auto-generated method stub
		
	}
}
