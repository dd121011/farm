package com.f5.ourfarm.activity;

import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.f5.ourfarm.R;
import com.f5.ourfarm.model.SinaWeiboErrorMsg;
import com.f5.ourfarm.util.Constants;
import com.f5.ourfarm.util.Tools;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.umeng.analytics.MobclickAgent;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.api.StatusesAPI;
import com.weibo.sdk.android.net.RequestListener;

public class DetailShareActivity extends Activity implements OnClickListener, RequestListener  {
    
    OnClickListener lback = null;
    
    TextView mResult;
    
    //����΢��
    private TextView mTextNum;
	private Button mSend,mPhoto;
	private EditText mEdit;
	private FrameLayout mPiclayout;
	private ImageView mImage ;
	private String mContent = "";
	private String weiboLat = "";
	private String weiboLng = "";
    private String mAccessToken = "";
    
	public static final String EXTRA_ACCESS_TOKEN = "com.weibo.android.accesstoken";
	public static final String EXTRA_EXPIRES_IN = "com.weibo.android.token.expires";
	public static final String WEIBO_TEXT = "weibo_text";
	
    public static final int WEIBO_MAX_LENGTH = 140;
    private static final String TAG = "����΢��ҳ��";
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
     // ȥ��������
       /* this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //׼��listeners
        this.prepareListeners();
        setContentView(R.layout.activity_share);
        //���ñ���
        TextView tvTitle = (TextView) this.findViewById(R.id.TextView_toppanel_title);
        tvTitle.setText(R.string.detail_share);
      //����listeners
        this.batchSetListeners();
        
        //����΢��
        Intent in = this.getIntent();
		mAccessToken = in.getStringExtra(EXTRA_ACCESS_TOKEN);
		mContent = in.getStringExtra(WEIBO_TEXT);
		weiboLat = in.getStringExtra(Constants.LOCATION_LAT);
		weiboLng = in.getStringExtra(Constants.LOCATION_LNG);
				

		Button close = (Button) findViewById(R.id.weibosdk_btnClose);
		close.setOnClickListener(this);
		mSend = (Button) this.findViewById(R.id.weibosdk_btnSend);
		mSend.setOnClickListener(this);
		LinearLayout total = (LinearLayout) this.findViewById(R.id.weibosdk_ll_text_limit_unit);
		total.setOnClickListener(this);
		mTextNum = (TextView) this.findViewById(R.id.weibosdk_tv_text_limit);
		ImageView picture = (ImageView) this.findViewById(R.id.weibosdk_ivDelPic);
		picture.setOnClickListener(this);

		mEdit = (EditText) this.findViewById(R.id.weibosdk_etEdit);
		mEdit.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s) {
			}

			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String mText = mEdit.getText().toString();
				int len = mText.length();
				if (len <= WEIBO_MAX_LENGTH) {
					len = WEIBO_MAX_LENGTH - len;
					//mTextNum.setTextColor(R.color.weibosdk_text_num_gray);
					if (!mSend.isEnabled())
						mSend.setEnabled(true);
				} else {
					len = len - WEIBO_MAX_LENGTH;

					mTextNum.setTextColor(Color.RED);
					if (mSend.isEnabled())
						mSend.setEnabled(false);
				}
				mTextNum.setText(String.valueOf(len));
			}
		});
		mEdit.setText(mContent);
		mPiclayout = (FrameLayout) DetailShareActivity.this.findViewById(R.id.weibosdk_flPic);
		mPiclayout.setVisibility(View.GONE);*/
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
        // nearby->home
        lback = new OnClickListener() {
            public void onClick(View v) {
                // TODO back
                finish();
            }
        };
    }
    
    /**
     * ��view�ͼ���
     */
    private void batchSetListeners() {
        ImageView iback2home = (ImageView) this.findViewById(R.id.ImageView_button_back);
        iback2home.setOnClickListener(lback);
    }

	@Override
	public void onComplete(String arg0) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(DetailShareActivity.this, R.string.weibosdk_send_sucess, Toast.LENGTH_LONG)
						.show();
			}
		});

		this.finish();
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
						Tools.showToastShort(DetailShareActivity.this, 
								DetailShareActivity.this.getString(R.string.weibosdk_send_sucess) );
						finish();
					} else {
						Tools.showToastShort(DetailShareActivity.this, DetailShareActivity.this.getString(R.string.weibosdk_send_failed));
						Log.e(TAG, e.getMessage());
					}
				} catch (JsonSyntaxException e) {
	                Tools.showToastShort(DetailShareActivity.this, DetailShareActivity.this.getString(R.string.weibosdk_send_failed));
	                Log.e(TAG, e.getMessage());
				}
			}
		});
	}

	@Override
	public void onIOException(IOException arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onClick(View v) {
		/*int viewId = v.getId();

		if (viewId == R.id.weibosdk_btnClose) {
			finish();
		} else if (viewId == R.id.weibosdk_btnSend) {
			StatusesAPI api = new StatusesAPI(DetailActivity.accessToken);
			if (!TextUtils.isEmpty(mAccessToken)) {
				this.mContent = mEdit.getText().toString();
				if(TextUtils.isEmpty(mContent)){
				    Toast.makeText(this, "����������!",
                            Toast.LENGTH_LONG).show();
				    return;
				}
				// Just update a text weibo!
				api.update( this.mContent, weiboLat, weiboLng, this);
			} else {
				Toast.makeText(this, this.getString(R.string.weibosdk_please_login),
						Toast.LENGTH_LONG).show();
			}

		} else if (viewId == R.id.weibosdk_ll_text_limit_unit) {
			Dialog dialog = new AlertDialog.Builder(this).setTitle(R.string.weibosdk_attention)
					.setMessage(R.string.weibosdk_delete_all)
					.setPositiveButton(R.string.weibosdk_ok, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							mEdit.setText("");
						}
					}).setNegativeButton(R.string.weibosdk_cancel, null).create();
			dialog.show();
		} else if (viewId == R.id.weibosdk_ivDelPic) {
			Dialog dialog = new AlertDialog.Builder(this).setTitle(R.string.weibosdk_attention)
					.setMessage(R.string.weibosdk_del_pic)
					.setPositiveButton(R.string.weibosdk_ok, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							mPiclayout.setVisibility(View.GONE);
						}
					}).setNegativeButton(R.string.weibosdk_cancel, null).create();
			dialog.show();
		}*/
	}

    
}


