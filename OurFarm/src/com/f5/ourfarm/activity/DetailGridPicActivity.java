package com.f5.ourfarm.activity;

import static com.f5.ourfarm.util.Constants.PROGRESS_MESSAGE;
import static com.f5.ourfarm.util.Constants.PROGRESS_TITLE;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.f5.ourfarm.R;
import com.f5.ourfarm.util.Constants;
import com.f5.ourfarm.util.Tools;
import com.umeng.analytics.MobclickAgent;

/**
 * 商家图片展示
 * 
 * @author lify
 *
 */
public class DetailGridPicActivity extends Activity {
		
	//后退按钮
	OnClickListener lback = null;
	
	private String[] picsURL;//该景点图片URL
	//异步从网络加载图片，存放到该list中
	List<Bitmap> picsList = new ArrayList<Bitmap>();
    //进度条
    private ProgressDialog progressDialog = null; 
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 去除标题栏
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_destination_detail_grid_pic);
        
        //准备listeners
        this.prepareListeners();
        //获得图片url数组
        Intent in = this.getIntent();
        picsURL = in.getStringArrayExtra(Constants.DETAIL_PICTURE_SHOW);
        batchSetListeners();
       
        progressDialog = ProgressDialog.show(DetailGridPicActivity.this, PROGRESS_TITLE, PROGRESS_MESSAGE, true);
        progressDialog.setCancelable(true);
        //加载图片
        new Thread(runnable4pictury).start();
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
        // 后退按钮detail_grid_picture->detail
        lback = new OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        };
    }
    
    /**
     * 绑定view和监听
     */
    private void batchSetListeners() {
        ImageView iback2home = (ImageView) this.findViewById(R.id.ImageView_button_back);
        iback2home.setOnClickListener(lback);
    }
    
    /**
     * 图片适配器类
     * 
     * @author lify
     *
     */
    private class ImageAdapter extends BaseAdapter {
        private Context mContext;

        public ImageAdapter(Context c) {
            mContext = c;
        }

        public int getCount() {
            return picsList.size();
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {  // if it's not recycled, initialize some attributes
                imageView = new ImageView(mContext);
                imageView.setLayoutParams(new GridView.LayoutParams(200, 150));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setPadding(2, 2, 2, 2);
            } else {
                imageView = (ImageView) convertView;
            }

            //加载到适配器中
            imageView.setImageBitmap(picsList.get(position));
            return imageView;
        }

    }
    
    //异步加载图片
    Handler handler4pictury = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String res = data.getString("value");
            Log.i("mylog","请求结果为-->" + res);
            
            progressDialog.dismiss();
            //从网络加载图片失败
            if(picsList.size() <= 0) {
            	Tools.showToastLong(DetailGridPicActivity.this, Constants.NO_DETAIL_PICTURE);
            	finish();
            	return;
            }
            
            GridView gridview = (GridView) findViewById(R.id.grid_view);
            gridview.setAdapter(new ImageAdapter(DetailGridPicActivity.this));
            
        }
    };
    
    Runnable runnable4pictury = new Runnable() {
        @Override
        public void run() {
            for(String url:picsURL ){
                Bitmap bitmap = Tools.getBitmapFromUrl(url);
                if(bitmap != null) {
                	picsList.add(bitmap);
                }
            }
          
            Message msg = new Message();
            Bundle data = new Bundle();
            data.putString("value","ok");
            msg.setData(data);
            handler4pictury.sendMessage(msg);
        }
    };  
    
}
