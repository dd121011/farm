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
 * �̼�ͼƬչʾ
 * 
 * @author lify
 *
 */
public class DetailGridPicActivity extends Activity {
		
	//���˰�ť
	OnClickListener lback = null;
	
	private String[] picsURL;//�þ���ͼƬURL
	//�첽���������ͼƬ����ŵ���list��
	List<Bitmap> picsList = new ArrayList<Bitmap>();
    //������
    private ProgressDialog progressDialog = null; 
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // ȥ��������
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_destination_detail_grid_pic);
        
        //׼��listeners
        this.prepareListeners();
        //���ͼƬurl����
        Intent in = this.getIntent();
        picsURL = in.getStringArrayExtra(Constants.DETAIL_PICTURE_SHOW);
        batchSetListeners();
       
        progressDialog = ProgressDialog.show(DetailGridPicActivity.this, PROGRESS_TITLE, PROGRESS_MESSAGE, true);
        progressDialog.setCancelable(true);
        //����ͼƬ
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
     * �������¼���Ķ�����
     */
    private void prepareListeners() {
        // ���˰�ťdetail_grid_picture->detail
        lback = new OnClickListener() {
            public void onClick(View v) {
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
    
    /**
     * ͼƬ��������
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

            //���ص���������
            imageView.setImageBitmap(picsList.get(position));
            return imageView;
        }

    }
    
    //�첽����ͼƬ
    Handler handler4pictury = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String res = data.getString("value");
            Log.i("mylog","������Ϊ-->" + res);
            
            progressDialog.dismiss();
            //���������ͼƬʧ��
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
