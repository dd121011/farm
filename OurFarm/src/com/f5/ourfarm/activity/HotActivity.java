package com.f5.ourfarm.activity;

import static com.f5.ourfarm.util.Constants.PROGRESS_MESSAGE;
import static com.f5.ourfarm.util.Constants.PROGRESS_TITLE;
import static com.f5.ourfarm.util.URLConstants.HOTTOP_URL;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.f5.ourfarm.R;
import com.f5.ourfarm.layout.PullToRefreshView;
import com.f5.ourfarm.layout.PullToRefreshView.OnFooterRefreshListener;
import com.f5.ourfarm.model.LoadWay;
import com.f5.ourfarm.model.Summary;
import com.f5.ourfarm.util.HttpUtil;
import com.f5.ourfarm.util.Tools;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.umeng.analytics.MobclickAgent;


/**
 * ����ָ��
 * 
 * @author tianhao
 */
public class HotActivity extends Activity implements OnFooterRefreshListener {

    private static final String TAG = "����ָ��ҳ��:";
    private static final String errMsg = "��ȡ����ָ����Ϣʧ��";
    
    OnClickListener lback = null;
    OnClickListener details = null;
    HashMap<String, Bitmap> pics = new HashMap<String ,Bitmap>();
    ArrayList<String> urls = new ArrayList<String>();
    
    //������
    ProgressDialog progressDialog = null;
    
    //��¼�ڼ�������ÿ����һ�Σ�����10����¼
    private int queryCounts = 1;
    //����ˢ��
    PullToRefreshView mPullToRefreshView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // ȥ��������
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_hot);
        //��ʼ������ʾ�����ǵ�һ��
        queryCounts = 1;
        mPullToRefreshView = (PullToRefreshView) findViewById(R.id.main_pull_refresh_view);
        
        //׼��listeners
        this.prepareListeners();
        //����listeners
        this.batchSetListeners();
        progressDialog = ProgressDialog.show(HotActivity.this, PROGRESS_TITLE, PROGRESS_MESSAGE, true);
        progressDialog.setCancelable(true);
        //����listview
        new Thread(runnable4hot).start();
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
        //nearby->home
        lback = new OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        };
        //details
        details = new OnClickListener(){
            public void onClick(View v) {
                long destinationId = (Long) v.getTag();
                Intent intent = new Intent(HotActivity.this,DetailActivity.class);
                intent.putExtra("destinationId", destinationId);
                startActivity(intent);
            } 
        };
    }
    
    /**
     * ��view�ͼ���
     */
    private void batchSetListeners() {
        ImageView iback2home = (ImageView) this.findViewById(R.id.ImageView_button_back);
        iback2home.setOnClickListener(lback);
        //����ˢ��
        mPullToRefreshView.setOnFooterRefreshListener(this);
    }
    
    /**
     * ��ȡ�����б�����
     */
    private void creatListView(String res, int loadWay){

        List<Summary> lds = null;
        try{
	        Gson gson = new Gson();
	        lds = gson.fromJson(res, new TypeToken<List<Summary>>() {}.getType());
		} catch (JsonSyntaxException e) {
	        Tools.showToastShort(HotActivity.this, errMsg);
	        HotActivity.this.finish();
		}
        if(lds == null || lds.size() == 0 ) {
        	//����ˢ��ʱ��û�м��ص�����
        	if(loadWay == LoadWay.POLLUP_LOAD.ordinal()) {
        		Tools.showToastLong(HotActivity.this, "û�и���ľ����ˡ�");
        		mPullToRefreshView.onFooterRefreshComplete();
        	}
	        return;
        }
        LayoutInflater flater = LayoutInflater.from(this);
        LinearLayout list = (LinearLayout) this.findViewById(R.id.ListView_main);
        // ��ȡ��ѯ�����ѭ�������Ҫ�б�
        for (Summary ds : lds) {
            View v = flater.inflate(R.layout.listview_child_nearby, null);
            // pic
            ImageView ivPic = (ImageView) v.findViewById(R.id.ImageView_destination_pic);
            ivPic.setTag(ds.getPic());
            urls.add(ds.getPic());
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
            //���ؾ���
            v.findViewById(R.id.ListView_distance_pic).setVisibility(View.GONE);
            v.findViewById(R.id.ListView_destination_distance).setVisibility(View.GONE);
            
            // add view
            list.addView(v);
            // ���ü���
            long destinationId = ds.getDestinationId();
            v.setTag(destinationId);
            v.setOnClickListener(details);
        }
        
        //������ɼ���
        mPullToRefreshView.onFooterRefreshComplete();
        new Thread(runnable4loadpic).start();
    }
    
    private void addPics2ListView() {
        LinearLayout list = (LinearLayout) this
                .findViewById(R.id.ListView_main);
        int count = list.getChildCount();
        for (int i = 0; i < count; i++) {
            LinearLayout ly = (LinearLayout) list.getChildAt(i);
            ImageView iv = (ImageView) ly
                    .findViewById(R.id.ImageView_destination_pic);
            String url = (String) iv.getTag();
            Bitmap bm = (Bitmap) pics.get(url);
            iv.setImageBitmap(bm);
            iv.invalidate();
            ly.invalidate();
        }
        list.invalidate();
    }
    
    Handler handler4hot = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String res = data.getString("value");
            Log.i("mylog","������Ϊ-->" + res);
            progressDialog.dismiss();
            creatListView(res, data.getInt("loadway"));
        }
    };

    Runnable runnable4hot = new RunnableImp(LoadWay.INIT_LOAD);
    
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
            List<NameValuePair> param = new ArrayList<NameValuePair>();
            BasicNameValuePair count  = new BasicNameValuePair("count", String.valueOf(queryCounts));
            param.add(count);
            String res = null;
            try {
                res = HttpUtil.postUrl(HOTTOP_URL, param);
            } catch (ClientProtocolException e1) {
                Log.e(TAG, errMsg, e1);
            } catch (IOException e1) {
                Log.e(TAG, errMsg, e1);
            }
            Message msg = new Message();
            Bundle data = new Bundle();
            data.putString("value",res);
            //�����������
            data.putInt("loadway", loadWay.ordinal());
            msg.setData(data);
            handler4hot.sendMessage(msg);
        }
    }
    
    Handler handler4loadpic = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String res = data.getString("value");
            Log.i("mylog","������Ϊ-->" + res);
            addPics2ListView();
        }
    };

    Runnable runnable4loadpic = new Runnable(){
        @Override
        public void run() {
            Log.d(TAG, "����ָ����ѯ");
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
     * ����ˢ�´���
     */
	@Override
	public void onFooterRefresh(PullToRefreshView view) {
		++queryCounts;
		Log.d("�������", queryCounts + "");
		new Thread(new RunnableImp(LoadWay.POLLUP_LOAD)).start();
	}
}
