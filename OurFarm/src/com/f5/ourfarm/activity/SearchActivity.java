package com.f5.ourfarm.activity;

import static com.f5.ourfarm.util.URLConstants.COMMON_SEARCH_URL;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.baidu.location.LocationClient;
import com.f5.ourfarm.R;
import com.f5.ourfarm.layout.PullToRefreshView;
import com.f5.ourfarm.layout.PullToRefreshView.OnFooterRefreshListener;
import com.f5.ourfarm.model.LoadWay;
import com.f5.ourfarm.model.Summary;
import com.f5.ourfarm.util.Constants;
import com.f5.ourfarm.util.HttpUtil;
import com.f5.ourfarm.util.OurfarmApp;
import com.f5.ourfarm.util.Tools;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.umeng.analytics.MobclickAgent;


/**
 * ��ͨ��������
 * 
 * @author lfy
 */
public class SearchActivity extends Activity implements OnFooterRefreshListener {
    
	//����
    OnClickListener lback = null;
    //����
    OnClickListener lSearch = null;
    //�������������
    OnClickListener lClearButton = null;
    //�鿴��ϸ
    OnClickListener details = null;
    //�������������
    private EditText searchValue = null;
    //������հ�ť
    private Button clearButton = null;
    
    public static final String TAG = "SearchActivity";
    public static final String ERRMSG = "����ʱ��������";
    
    //���ش�ͼƬ��Ϣ
    HashMap<Long, Bitmap> pics = new HashMap<Long ,Bitmap>();
    
    //�û�����λ����Ϣ
    private double lat;
    private double lng;
    //���ڴ�Ŷ�λ��Ϣ
  	private Map<String, Object> localMap = new HashMap<String, Object>();
    //��ȡ��ǰλ��
    private LocationClient mLocClient;
    
    //��¼�ڼ�������ÿ����һ�Σ�����10����¼
    private int queryCounts = 1;
    //����ˢ��
    PullToRefreshView mPullToRefreshView;
    
    //loadingbar
    private LinearLayout loadingLayout;
    //��Ҫ��Ϣ
    private LinearLayout summaryLayout;
    //û�����������ʾ
    private LinearLayout searchNotFind;
    //��������
    private String value;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // ȥ��������
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_search);
        //loadingbar
        loadingLayout = (LinearLayout)findViewById(R.id.common_search_loadingbar);
        //��Ҫ��Ϣ
        summaryLayout = (LinearLayout)findViewById(R.id.common_search_result);
        //���������ʾ
        searchNotFind = (LinearLayout)findViewById(R.id.common_search_not_find);
        
        //��ʼ������ʾ�����ǵ�һ��
        queryCounts = 1;
        mPullToRefreshView = (PullToRefreshView) findViewById(R.id.main_pull_refresh_view);
        //����������ˢ��
        mPullToRefreshView.lock();
        
        //��õ�ǰλ��
        initLocalAddress();
        
        //׼��listeners
        this.prepareListeners();
        
        //���û����¼����ϵİ���ʱ�������Զ�����������
        setDefaultKeyMode(DEFAULT_KEYS_SEARCH_LOCAL);
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
     * ��ʼ������λ��
     */
    private void initLocalAddress() {
        //��õ�ǰλ��
        this.localMap = ((OurfarmApp)getApplication()).localMap;
        
        try{
        	lat = (Double)localMap.get(Constants.LOC_LAT);
        	lng = (Double)localMap.get(Constants.LOC_LNG);
        } catch(Exception e) {
        	Log.e(TAG, "��ȡ��ǰλ��ʧ�ܣ���������һ�λ�ȡ...");
        	// ʧ�ܺ��ٽ���һ��λ�û�ȡ
        	initLocation();
        }
    }
    
    /**
     * ��ʼ����ǰλ��
     * 
     * @return
     */
    private void initLocation() {
    	mLocClient = ((OurfarmApp)getApplication()).mLocationClient;
    	((OurfarmApp)getApplication()).localMap = this.localMap;

    	//���ö�λ��ز���
    	mLocClient.setLocOption(Tools.getLocationOption());
    	
        if (mLocClient != null && mLocClient.isStarted()){
        	mLocClient.requestLocation();
        } else {
        	Log.d("LocSDK3", "locClient is null or not started");
        	mLocClient.start();
        	mLocClient.requestLocation();
        }
    }
    
    /**
     * �������¼���Ķ�����
     */
    private void prepareListeners() {
        //nearby->home
        lback = new OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        };
        //������ť
        lSearch = new OnClickListener() {
            public void onClick(View v) {
            	//����������ť����Ϊ��һ�μ���
            	queryCounts = 1;
            	//����������ˢ��
                mPullToRefreshView.lock();
            	//���ԭ�����������
            	LinearLayout list = (LinearLayout) SearchActivity.this.findViewById(R.id.common_search_result);
                list.removeAllViews();
                
            	value = searchValue.getText().toString();
            	//������ʱֱ�ӷ���
            	if(value == null || value.trim().equals("")) {
            		return;
            	}
                value = value.trim();
                
                try{
                	lat = (Double)localMap.get(Constants.LOC_LAT);
                	lng = (Double)localMap.get(Constants.LOC_LNG);
                } catch(Exception e) {
                	Log.e(TAG, "��ȡ��ǰλ��ʧ��");
                }
                //������ʾ���
                loadingLayout.setVisibility(View.VISIBLE);
                summaryLayout.setVisibility(View.GONE);
                searchNotFind.setVisibility(View.GONE);
                //���ԭ�����������
                pics.clear();
            	//������ѯ
                new Thread(runnable4search).start();
            }
        };
        //�������������
        lClearButton = new OnClickListener() {
        	@Override
        	public void onClick(View v) {
        	    searchValue.setText("");
        	}
        };
        
        //details
        details = new OnClickListener(){
            public void onClick(View v) {
                long destinationId =  (Long) v.getTag(); 
                Log.i("res", ""+destinationId);
                Intent nearby2detail = new Intent(SearchActivity.this,DetailActivity.class);
                //���������������ݱ�����Intent��ȷ�����ݿ���activity�䴫��
                nearby2detail.putExtra("destinationId", destinationId);
                nearby2detail.putExtra("nearbyType", (Integer)v.getTag(R.string.tag_first));//��������
                startActivity(nearby2detail);
            }
        };
       
    }
    
    /**
     * ��view�ͼ���
     */
    private void batchSetListeners() {
    	//����
        ImageView iback2home = (ImageView) this.findViewById(R.id.ImageView_button_back);
        iback2home.setOnClickListener(lback);
        //����
        Button commonSearch = (Button) this.findViewById(R.id.common_search_button);
        commonSearch.setOnClickListener(lSearch);
        
        //������
        searchValue = (EditText)findViewById(R.id.common_search_input);
        searchValue.addTextChangedListener(mTextWatcher);
        //�����ť
        clearButton = (Button)findViewById(R.id.common_search_button_clear);
        clearButton.setOnClickListener(lClearButton);
        
        //����ˢ��
        mPullToRefreshView.setOnFooterRefreshListener(this);
    }
    
    /**
     * ����û������ʱ�Ĵ���
     * 
     * @param loadWay ���ط�ʽ����ʼ��������������
     */
    private void loadNoData(int loadWay) {
    	if(loadWay == LoadWay.POLLUP_LOAD.ordinal()) {
    		Tools.showToastLong(SearchActivity.this, "û�и���ľ����ˡ�");
    		mPullToRefreshView.onFooterRefreshComplete();
    	} else {
    		//���������ť��û�в�ѯ�����ݣ�������ʾ
    		searchNotFind.setVisibility(View.VISIBLE);
    	}
    }
    
    /**
     * �����ѯ�б�
     * 
     * ���ݲ�ѯ�����������ݣ�Ȼ�����ݸ�Ҫ��Ϣ��ʾ������
     */
    private void creatListView(String res, int loadWay) {
    	 //��������ʧ
        if (loadingLayout != null) {
            loadingLayout.setVisibility(View.GONE);
        }
        
        //��ʾ����
        List<Summary> lds = null;
        try{
            Gson gson = new Gson();
            lds = gson.fromJson(res, new TypeToken<List<Summary>>() {}.getType());
        } catch(Exception e) {
        	loadNoData(loadWay);
            Log.e(TAG, "����ʱ,��Ҫ��Ϣjsonת������ʧ��");
            return;
        }
        
        if(lds == null || lds.size() == 0){
        	loadNoData(loadWay);
        	return;
        } else{
        	summaryLayout.setVisibility(View.VISIBLE);
        	//���������
			((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
					.hideSoftInputFromWindow(SearchActivity.this.getCurrentFocus().getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);
        }
    	
        LayoutInflater flater = LayoutInflater.from(this);
        LinearLayout list = (LinearLayout) this.findViewById(R.id.common_search_result);
        
        // ��ȡ��ѯ�����ѭ�������Ҫ�б�
        for(Summary ds: lds){
            View v = flater.inflate(R.layout.listview_child_nearby, null);
            // pic
            ImageView ivPic = (ImageView) v
                    .findViewById(R.id.ImageView_destination_pic);
            Bitmap bm = (Bitmap) pics.get(ds.getDestinationId());
            if(bm != null){
                ivPic.setImageBitmap(bm);
            }
            
            // name
            TextView tvName = (TextView) v
                    .findViewById(R.id.ListView_destination_name);
            tvName.setText(ds.getName());
            // sroce
            RatingBar rbSroce = (RatingBar) v
                    .findViewById(R.id.ratingBar_destination_sroce);
            rbSroce.setRating(ds.getScore());
            // price info
            TextView tvPrice = (TextView) v
                    .findViewById(R.id.ListView_destination_price);
            tvPrice.setText(ds.getPriceInfo());
            // hot
            TextView tvHot = (TextView) v
                    .findViewById(R.id.ListView_destination_hot);
            tvHot.setText("����ָ��:" + ds.getHot());
            // Characteristic
            TextView tvCharacteristic = (TextView) v
                    .findViewById(R.id.ListView_destination_characteristic);
            tvCharacteristic.setText("��ɫ:" + ds.getCharacteristic());
            // distance
            TextView tvDistance = (TextView) v
                    .findViewById(R.id.ListView_destination_distance);
            tvDistance.setText(Tools.getDistanceFormat(ds.getLng(), ds.getLat(), lng, lat));
            // add view
            list.addView(v);
            // ���ü���
            long destinationId = ds.getDestinationId();
            v.setTag(destinationId);
            v.setTag(R.string.tag_first, ds.getType());
            v.setOnClickListener(details);
        
        }
        list.invalidate();
        //������ɼ���
        mPullToRefreshView.onFooterRefreshComplete();
        //��������ˢ��
        mPullToRefreshView.unlock();
    }
    
    Runnable runnable4search = new RunnableImp(LoadWay.INIT_LOAD);
    
    /**
     * Runnableʵ���࣬�������󾰵�list
     */
    class RunnableImp implements Runnable {
        //���ݼ��ط�ʽ��1����ʼ����, 2��ѡ��spinner��ʽ, 3��ѡ���������ط�ʽ
        LoadWay loadWay;
        RunnableImp(LoadWay loadWay) {
            this.loadWay = loadWay;
        }
        
        @Override
        public void run() {
        	String res = null;
            try {
                res = HttpUtil.postUrl(COMMON_SEARCH_URL, getParameters());
            } catch (ClientProtocolException e1) {
                Log.e(TAG, ERRMSG, e1);
            } catch (IOException e1) {
                Log.e(TAG, ERRMSG, e1);
            } catch (Exception e) {
            	Log.e(TAG, ERRMSG, e);
            }
        
	        Message msg = new Message();
	        Bundle data = new Bundle();
	        data.putString("value",res);
	        //�����������
            data.putInt("loadway", loadWay.ordinal());
	        msg.setData(data);
	        handler4Search.sendMessage(msg);
        }
    }
    
    Handler handler4Search = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String res = data.getString("value");
            Log.i(TAG, "������Ϊ-->" + res);
            
            creatListView(res, data.getInt("loadway"));
        }
    };

    /**
     * ���󾰵��б�ʱ������Ĳ���
     * 
     * @param defaultDistance �����ľ���
     * @return �������������list
     */
    private List<NameValuePair> getParameters() {
        List<NameValuePair> param = new ArrayList<NameValuePair>();
        
        // ��ȡ�û���ǰ��ַ lat��lng
        param.add(new BasicNameValuePair("lat", String.valueOf(this.lat)));
        param.add(new BasicNameValuePair("lng", String.valueOf(this.lng)));
        //��������
        BasicNameValuePair searchValue = new BasicNameValuePair("name", value);
        param.add(searchValue);
        //������صĴ���
        BasicNameValuePair count = new BasicNameValuePair("count", String.valueOf(queryCounts));
        param.add(count);
        
        return param;
    }
    
    /**
     * ���������������Ƿ��������֣����ж��ұߵ������ť�Ƿ���ʾ
     */
	TextWatcher mTextWatcher = new TextWatcher() {
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			// TODO Auto-generated method stub
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			// TODO Auto-generated method stub
		}

		@Override
		public void afterTextChanged(Editable s) {
			if (searchValue.getText().toString() != null && !searchValue.getText().toString().equals("")) {
				clearButton.setVisibility(View.VISIBLE);
			} else {
				clearButton.setVisibility(View.INVISIBLE);
			}
		}
	};
 
    /**
     * ����ˢ�´���
     */
	@Override
	public void onFooterRefresh(PullToRefreshView view) {
		++queryCounts;
		Log.d("�������", queryCounts + "");
		new Thread(new RunnableImp(LoadWay.POLLUP_LOAD)).start();
	}
}
