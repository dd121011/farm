package com.f5.ourfarm.activity;

import static com.f5.ourfarm.util.Constants.PROGRESS_MESSAGE;
import static com.f5.ourfarm.util.Constants.PROGRESS_TITLE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.R.color;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.f5.ourfarm.R;
import com.f5.ourfarm.activity.AroundFarmActivity.RunnableImp;
import com.f5.ourfarm.model.ClassicItineraries;
import com.f5.ourfarm.model.Line;
import com.f5.ourfarm.model.LoadWay;
import com.f5.ourfarm.model.Summary;
import com.f5.ourfarm.util.Tools;
import com.umeng.analytics.MobclickAgent;

@SuppressLint("ParserError")
public class ClassicLineDetailActivity extends Activity {
	OnClickListener lback = null;
	OnClickListener details = null;
	//����map����ʾ·����ϸ��Ϣ�б�
	OnClickListener showDetail4lines = null;
	OnClickListener hideDetail4lines = null;
	// �����ײ����4����ť
	OnClickListener lPanelBottom = null;

	Bitmap pic ;

	Bitmap picMap ;
	double[] randoms  ;
	
	//������
    private ProgressDialog progressDialog = null; 
	
	//·�����ݶ���
	ClassicItineraries ci;
	
	//�첽���������ͼƬ����ŵ���list��
	HashMap<Long, List<Bitmap>> pics4point = new HashMap<Long, List<Bitmap>>();
	
	
    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// ȥ��������
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		// ׼��listeners
		this.prepareListeners();
		setContentView(R.layout.activity_classicline_detail);
		// ����listeners
		this.batchSetListeners();
		//
		this.showDetail();
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
     * 
     */
    private void showDetail() {
        Bundle extras = getIntent().getExtras();
        ci = (ClassicItineraries) extras.getSerializable("detail");

        if (ci != null) {
            // name
            TextView tvName = (TextView) this
                    .findViewById(R.id.TextView_classicline_name);
            tvName.setText(ci.getName());
            // Summary
            TextView tvsummary = (TextView) this.findViewById(R.id.TextView_line_summary);
            tvsummary.setText(ci.getItinerarySummary());
            
            progressDialog = ProgressDialog.show(ClassicLineDetailActivity.this, PROGRESS_TITLE, PROGRESS_MESSAGE, true);
            progressDialog.setCancelable(true);
            new Thread(runnable4loadpic).start();
           
        }
    }
    
    /**
     * ��ʾ·����ϸ��Ϣ�б�
     */
    private void showDetails4lines() {
        LinearLayout list = (LinearLayout) this
                .findViewById(R.id.ListView_Line_main);
       if (list.getChildCount() < 1){
           Bundle extras = getIntent().getExtras();
           ci = (ClassicItineraries) extras
                   .getSerializable("detail");
           List<Line>  lines = ci.getLine();
           
           LayoutInflater flater = LayoutInflater.from(this);
           int step = 1;
           for (Line line : lines) {
               View v = flater.inflate(R.layout.listview_child_lines, null);
               //step
               TextView tvStepNum = (TextView) v.findViewById(R.id.TextView_step_num);
               tvStepNum.setText("��"+(step)+"վ��");
               //step name
               TextView tvStepName = (TextView) v.findViewById(R.id.TextView_step_name);
               tvStepName.setText(line.getDestinationName());
               //line content
               TextView tvContent = (TextView) v.findViewById(R.id.TextView_content);
               tvContent.setText(line.getContent());
               //line characteristic
               TextView tvCharacteristic = (TextView) v.findViewById(R.id.TextView_characteristic);
               tvCharacteristic.setText(line.getCharacteristic());
               /** pics ���ؾ���ͼƬ*/
   				LinearLayout lypics = (LinearLayout) v
   					.findViewById(R.id.LinearLayout_pic_Grid);
   				View view4pics = flater.inflate(R.layout.activity_grid_pic, null);
   				lypics.addView(view4pics);
   				lypics.invalidate(); 
               // add view
               list.addView(v, (step++-1));
               // ���ü���
               long destinationId = line.getDestinationId();
               v.setTag(destinationId);
               v.setOnClickListener(details);
           }
           new Thread(new Runnable4loadViewpicImpl()).start();
       }else{
           list.setVisibility(View.VISIBLE); 
       }
    }
    
    /**
	 * ���ؾ���ͼƬ
	 */
	private void initViewPic() {
		LinearLayout list = (LinearLayout) this
                .findViewById(R.id.ListView_Line_main);
        List<Line>  lines = ci.getLine();
        
       // LayoutInflater flater = LayoutInflater.from(this);
        int step = 0;
		for (Line line : lines) {
			LinearLayout lypics = (LinearLayout)list.getChildAt(step++);
			/***/
			List<Bitmap> listbitmap = pics4point.get(line.getDestinationId());
			/** pics ���ؾ���ͼƬ */
			View view4pics = lypics.findViewById(R.id.LinearLayout_pic_Grid);
			if (listbitmap.size() >= 1) {
				ImageView iv1 = (ImageView) view4pics
						.findViewById(R.id.ImageView_recommend_farm_pic1);
				iv1.setImageBitmap(listbitmap.get(0));
				iv1.setVisibility(View.VISIBLE);
			}
			if (listbitmap.size() >= 2) {
				ImageView iv2 = (ImageView) view4pics
						.findViewById(R.id.ImageView_recommend_farm_pic2);
				iv2.setImageBitmap(listbitmap.get(1));
				iv2.setVisibility(View.VISIBLE);
			}
			if (listbitmap.size() >= 3) {
				ImageView iv3 = (ImageView) view4pics
						.findViewById(R.id.ImageView_recommend_farm_pic3);
				iv3.setImageBitmap(listbitmap.get(2));
				iv3.setVisibility(View.VISIBLE);
			}
			if (listbitmap.size() >= 4) {
				ImageView iv4 = (ImageView) view4pics
						.findViewById(R.id.ImageView_recommend_farm_pic4);
				iv4.setImageBitmap(listbitmap.get(3));
				iv4.setVisibility(View.VISIBLE);
			}
			lypics.findViewById(R.id.more_fram_loadingbar).setVisibility(View.GONE);
			lypics.findViewById(R.id.more_around_farm).setVisibility(View.VISIBLE);
			view4pics.invalidate();
			lypics.invalidate();
		}
		list.invalidate();
	}

	/**
	 * �������¼���Ķ�����
	 */
	private void prepareListeners() {
		// nearby->home
		lback = new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		};
		
		//details
        details = new OnClickListener(){
            public void onClick(View v) {
                long destinationId =  (Long) v.getTag(); 
                Log.i("res", ""+destinationId);
                Intent nearby2detail = new Intent(ClassicLineDetailActivity.this,DetailActivity.class);
                //���������������ݱ�����Intent��ȷ�����ݿ���activity�䴫��
                nearby2detail.putExtra("destinationId", destinationId);
                nearby2detail.putExtra("nearbyType", 1);//��������
                startActivity(nearby2detail);
            } 
        };
		
        //share with weibo
        // TODO
		
	}

	/**
	 * ��view�ͼ���
	 */
	private void batchSetListeners() {
		ImageView iback = (ImageView) this
				.findViewById(R.id.ImageView_button_back);
		iback.setOnClickListener(lback);
	}
	
	/**����·�ߵ�ͼ*/
	Handler handler4loadpic = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String res = data.getString("value");
            Log.i("mylog","������Ϊ-->" + res);
          
            progressDialog.dismiss();// ���ؽ�����
            
            ImageView iv = (ImageView) findViewById(R.id.ImageView_line_map);
            iv.setImageBitmap(picMap);
            /**��ʾ·������;���ͼƬ*/
            iv.invalidate();
            showDetails4lines();
        }
    };

    /**�첽����·�ߵ�ͼ����ҳͼ*/
    Runnable runnable4loadpic = new Runnable() {
        @Override
        public void run() {
        	 /**�첽������ҳͼ*/
//            pic = Tools.getBitmapFromUrl(picUrl);
//            setPic(pic);
        	/**�첽����·�ߵ�ͼ*/
            picMap = Tools.getBitmapFromUrl(ci.getPicMap());
            setPicMap(picMap);
            
            Message msg = new Message();
            Bundle data = new Bundle();
            data.putString("value", "ok");
            msg.setData(data);
            handler4loadpic.sendMessage(msg);
        }
    };
    
    /**����·�ߵ�ͼ*/
	Handler handler4loadViewpic = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            initViewPic();
        }
    };

    /**�첽����·�ߵ�ͼ����ҳͼ*/
   // Runnable runnable4loadViewpic = new Runnable4loadViewpicImpl(long destinationId ) ;
    
    class Runnable4loadViewpicImpl implements Runnable {
    	
    	
    	
    	
        @Override
		public void run() {
        	/**�첽���ؾ���ͼ*/
            List<Line>  lines = ci.getLine();
            int step = 1;
            for (Line line : lines) {
            	String[] pics4view =line.getPics();
            	int length = pics4view.length;
            	List<Bitmap> bitmaps = new ArrayList<Bitmap> ();
            	for(int i=0;i<length;i++){
            		String picStr = pics4view[i];
            		bitmaps.add(Tools.getBitmapFromUrl(picStr));
            	}
            	pics4point.put(line.getDestinationId(), bitmaps);
            }

			Message msg = new Message();
			Bundle data = new Bundle();
			data.putString("res", "ok");
			msg.setData(data);
			handler4loadViewpic.sendMessage(msg);
		}
    };
    
    
    public Bitmap getPic() {
        return pic;
    }
    public void setPic(Bitmap pic) {
        this.pic = pic;
    }



	public Bitmap getPicMap() {
		return picMap;
	}

	public void setPicMap(Bitmap picMap) {
		this.picMap = picMap;
	}

}
