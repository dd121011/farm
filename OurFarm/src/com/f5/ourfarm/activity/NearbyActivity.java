package com.f5.ourfarm.activity;

import static com.f5.ourfarm.util.URLConstants.NEARBY_URL;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.baidu.location.LocationClient;
import com.f5.ourfarm.R;
import com.f5.ourfarm.layout.PullToRefreshView;
import com.f5.ourfarm.layout.PullToRefreshView.OnFooterRefreshListener;
import com.f5.ourfarm.model.Destination;
import com.f5.ourfarm.model.LoadWay;
import com.f5.ourfarm.model.Summary;
import com.f5.ourfarm.sqlite.DestinationDbAdpter;
import com.f5.ourfarm.util.Constants;
import com.f5.ourfarm.util.HttpUtil;
import com.f5.ourfarm.util.OurfarmApp;
import com.f5.ourfarm.util.Tools;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.umeng.analytics.MobclickAgent;

/**
 * @author tianhao
 *
 */
public class NearbyActivity extends Activity implements OnFooterRefreshListener{
    double scale = 111.31949079327;//1ά�ȵľ���
    /*���㾭γ�Ȳ���*/
   // $half_distance =($distance)/($scale);
    OnClickListener lback = null;
    OnClickListener details = null;
    OnClickListener showMap = null;
    OnClickListener lrefresh = null;
    
    //loadingbar
    private LinearLayout loadingLayout;
    //��Ҫ��Ϣ
    private LinearLayout summaryLayout;
    //�Ϸ�������
    private RelativeLayout spinner;
    private Spinner spinnerDistance;
    private Spinner spinnerSort;
    //����ˢ��
    PullToRefreshView mPullToRefreshView;
    
    //�Զ��������
    ProgressBar progressBar;
    //��ʾ��ǰλ��
    TextView localAddress;
    
    //�û�����λ����Ϣ
    private double lat;
    private double lng;
    //�洢ѡ��ľ���
    private int selectDistance = 1;
    //���ش�ͼƬ��Ϣ
    HashMap<Long, Bitmap> pics = new HashMap<Long ,Bitmap>();
    //��ʾ����
    HashMap<Long, Summary> lds = new HashMap<Long ,Summary>();
   
    //sqlite 
    DestinationDbAdpter destinationDbAdpter;
    //TODO Ĭ�Ͼ�����5km 100ֻ�ǲ�����
    private static String DEFAULE_DISTANCE = "100";
    //��¼�ڼ�������ÿ����һ�Σ�����10����¼
    private int queryCounts = 1;
    
    //��ȡ��ǰλ��
    private LocationClient mLocClient;
    //���ڴ�Ŷ�λ��Ϣ
  	private Map<String, Object> localMap = new HashMap<String, Object>();
  	
  	private static String TAG = "����ҳ��";
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); 
        // ȥ��������
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //׼��listeners
        this.prepareListeners();
        setContentView(R.layout.activity_nearby);
        
        initStatus();
        //��õ�ǰλ��
        initLocalAddress();
        
        //����listeners
        this.batchSetListeners();
        //����spinner
        this.createSpinners();
        //�����ݿ�
        destinationDbAdpter = new DestinationDbAdpter(this);
        destinationDbAdpter.open();
        
        Log.d("�ٴμ���", String.valueOf(lds.entrySet().size()));
         //�ȼ��ر�������
        this.showLocalList();
        //TODO ��Ҫ���ص����ݲ���
//        new Thread(runnable4nearby).start();
    }
    
    /**
     * ��ʼҳ��ʱ�Ŀؼ�״̬�趨
     */
    private void initStatus() {
    	localAddress = (TextView)this.findViewById(R.id.TextView_nearby_local_address);
        progressBar = (ProgressBar) findViewById(R.id.textprogressbar);
        mPullToRefreshView = (PullToRefreshView) findViewById(R.id.main_pull_refresh_view);
        //��������ҳ��
        mPullToRefreshView.lock();
        
        //loadingbar
        loadingLayout = (LinearLayout)findViewById(R.id.nearby_loadingbar);
        //��Ҫ��Ϣ
        summaryLayout = (LinearLayout)findViewById(R.id.ListView_main);
        spinner = (RelativeLayout)findViewById(R.id.spinner);
        
        spinnerDistance = (Spinner)findViewById(R.id.spinner_nearby_distance);
        spinnerSort = (Spinner)findViewById(R.id.spinner_nearby_sort);
        
        //������ʾ���
        loadingLayout.setVisibility(View.VISIBLE);
        summaryLayout.setVisibility(View.GONE);
        spinner.setVisibility(View.GONE);
        //��ʼ������ʾ�����ǵ�һ��
        queryCounts = 1;
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
     * ���þ���������Ƿ�ɵ����״̬
     * 
     * @param clickable 
     */
    private void setSpinnerClickable(boolean clickable) {
    	spinnerDistance.setClickable(clickable);
        spinnerSort.setClickable(clickable);
    }
    
    /**
     * ��ʼ������λ��
     */
    private void initLocalAddress() {
        localAddress.setVisibility(View.VISIBLE);
        //��õ�ǰλ��
        this.localMap = ((OurfarmApp)getApplication()).localMap;
        
        try{
        	lat = (Double)localMap.get(Constants.LOC_LAT);
        	lng = (Double)localMap.get(Constants.LOC_LNG);
        } catch(Exception e) {
			Log.d(TAG, Constants.REFERESH_FAILD_GET_LOC, e);
			localAddress.setText(Constants.REFERESH_FAILD_GET_LOC);
			return;
        }
        showLocalAddress();
    }
    
    /**
     * ��ʾ����λ��
     */
    private void showLocalAddress() {
        //����е�ַ����ʾ��������ʾ��γ��
        if(localMap.get(Constants.LOC_ADDR) != null) {
        	localAddress.setText(localMap.get(Constants.LOC_ADDR).toString());
        } else {
        	localAddress.setText(lat + "  " + lng);
        }
    }

    /**
     * �ȼ��ر�������
     */
    private void showLocalList() {
        // ѡ�񸽽������ͣ�1������ 2����ʳ 3��ס�� 4���ز�
        Bundle extras = getIntent().getExtras();
        int naerbyType = extras.getInt("nearbyType");
        try {
            //����param���� ��ѡȡ��������
            Cursor cursor = destinationDbAdpter.getNearbyDestination(lat, lng, Double.valueOf(DEFAULE_DISTANCE), naerbyType);
            if (cursor != null && cursor.getCount() > 0) {
                Gson gson = new Gson();
                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                    String json = cursor.getString(1);
                    Destination destination = gson.fromJson(json, new TypeToken<Destination>() {}.getType());
                    Summary summary = destination.getScenerySummary();
                    double distance = Tools.getDistance(summary.getLng(), summary.getLat(), lng, lat);
                    summary.setDistance(distance);
                    if (distance < Double.valueOf(DEFAULE_DISTANCE) && summary.getType() == naerbyType) {
                        lds.put(summary.getDestinationId(), summary);
                        // ��ȡ����ͼƬ
                        Bitmap bitmap = destinationDbAdpter
                                .getHeadBitmap(summary.getDestinationId());
                        if (bitmap != null) {
                            pics.put(summary.getDestinationId(), bitmap);
                        }
                    }

                }
            }
            // ��ʾ�б�  
            // TODO  �жϱ��������Ƿ�Ϊnull����Ϊnull��ȡ��̨����
            if(lds.size() == 0){
            	new Thread(runnable4nearby).start();
            } else {
            	//��������ʧ
                if (loadingLayout != null) {
                     loadingLayout.setVisibility(View.GONE);
                }
                summaryLayout.setVisibility(View.VISIBLE);
                spinner.setVisibility(View.VISIBLE);
                mPullToRefreshView.unlock();
            	//ֻ��ʾ��������
            	this.showList(sortByHotspot(lds));
            }
        } catch (JsonSyntaxException e) {
            Log.e("jsonת��ʧ��", "json 2 Destination" + e.getMessage());
        } catch (SQLException e) {
            Log.e("��ȡͼƬ���ݴ���", "��ȡͼƬ���ݴ���");
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
        //details
        details = new OnClickListener(){
            public void onClick(View v) {
            	// �ж������Ƿ���ã�������������ʾ�û�������������ת
            	if(!Tools.checkNetworkStatus(NearbyActivity.this)) {
            		return;
            	}
                long destinationId =  (Long) v.getTag(); 
                Log.i("res", ""+destinationId);
                int nearbyType = getIntent().getExtras().getInt("nearbyType");
                //����ɽׯ
                if(nearbyType == 3) {
                	Intent nearby2mountainVilla = new Intent(NearbyActivity.this,MountainVillaActivity.class);
                	//���������������ݱ�����Intent��ȷ�����ݿ���activity�䴫��
                	nearby2mountainVilla.putExtra("destinationId", destinationId);
                	
                	startActivity(nearby2mountainVilla);
                } else {//����3������
                	Intent nearby2detail = new Intent(NearbyActivity.this,DetailActivity.class);
                	//���������������ݱ�����Intent��ȷ�����ݿ���activity�䴫��
                	nearby2detail.putExtra("destinationId", destinationId);
                	//�����жϵ��������������
                	nearby2detail.putExtra("nearbyType", nearbyType);
                	
                	startActivity(nearby2detail);
                }
            } 
        };
        //map
        showMap = new OnClickListener(){
            public void onClick(View v) {
            	// �ж������Ƿ���ã�������������ʾ�û�������������ת
            	if(!Tools.checkNetworkStatus(NearbyActivity.this)) {
            		return;
            	}
            	//��Ҫ��ʾ�����ݴ�����ͼҳ��
                Intent toMapIntent = new Intent(NearbyActivity.this, NearbyMapActivity.class);
                toMapIntent.putExtra(Constants.MAP_SHOW_SPOT, lds);
                toMapIntent.putExtra(Constants.MAP_SHOW_DISTANCE, selectDistance);//ѡ��ľ��룬�����жϵ�ͼ����
                startActivity(toMapIntent);
            } 
        };
        //refresh
        lrefresh = new OnClickListener(){
            public void onClick(View v) {
            	// �ж������Ƿ���ã�������������ʾ�û�������������ת
            	if(!Tools.checkNetworkStatus(NearbyActivity.this)) {
            		return;
            	}
            	//�Ϸ�԰����Ȧ���ص�ʱ��,��ˢ��
            	if(loadingLayout.getVisibility() == View.VISIBLE
            		|| mPullToRefreshView.getLock() == false) {
            		return;
            	}
            	
            	queryCounts = 1;
            	
//            	Toast.makeText(NearbyActivity.this, "����ȷ�����λ��...", Toast.LENGTH_LONG).show();
                //��ʼ����ǰλ��
        	    try {
        	    	retryGetLocation();
        		} catch (Exception e) {
        			Log.d(TAG, Constants.REFERESH_FAILD_GET_LOC, e);
//        			Tools.showToastLong(NearbyActivity.this, "Exception");
        			localAddress.setText(Constants.REFERESH_FAILD_GET_LOC);
        			return;
        		}
            	
            	//���ؽ�����
            	progressBar.setVisibility(View.VISIBLE);
            	progressBar.setProgress(0);
            	localAddress.setVisibility(View.GONE);
            	mPullToRefreshView.lock();
            	
                //���غ�̨
                new Thread(runnable4nearby).start();
            }
        };
    }
    
    /**
     * ��γ��Ի�ȡ����λ��
     */
    private void retryGetLocation() {
    	//γ��ֵ
    	Object locTypeCode = null;
    	//�ȴ���λ��Ϣ�Ļ�ȡ,��10��λ������
    	int waint = 0;
    	int waintCount = 10;
    	for(;waint < waintCount; waint++) {
    		initLocation();
    		locTypeCode = localMap.get(Constants.LOC_TYPE_CODE);
    		if(locTypeCode != null) {
    			break;
    		}
    	}
    	if(waint == waintCount) {
    		Log.d(TAG, "wait"); 
    		//û�л����λ�ã�ҳ�治ˢ��
//    		cannotGetLocShow();
    		return;
    	} else {
    		//��λ�ɹ�
    		if("61".equals(locTypeCode.toString()) ||
    				"68".equals(locTypeCode.toString()) || "161".equals(locTypeCode.toString())) {
    	        lat = (Double)localMap.get(Constants.LOC_LAT);
    	        lng = (Double)localMap.get(Constants.LOC_LNG);
    	        //���LOC_TYPE_CODE�������ж���һ��λ������
    	        localMap.put(Constants.LOC_TYPE_CODE, null);
    	        Log.d(TAG, "lat:" + lat + " lng:" + lng );
    	        //��ʾ����λ��
    	        showLocalAddress();
    		} else {
    			Log.d(TAG, "get_loc_false"); 
    			localAddress.setText(Constants.REFERESH_FAILD_GET_LOC);
    			return;
    		}
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
     * ��view�ͼ���
     */
    private void batchSetListeners() {
        // back
        ImageView iback2home = (ImageView) this.findViewById(R.id.ImageView_button_back);
        iback2home.setOnClickListener(lback);
        //map
        ImageView i2map = (ImageView) this.findViewById(R.id.ImageView_button_map);
        i2map.setOnClickListener(showMap);
        //refresh
        ImageView irefresh = (ImageView) this.findViewById(R.id.ImageView_destination_refresh);
        irefresh.setOnClickListener(lrefresh);
        
        mPullToRefreshView.setOnFooterRefreshListener(this);
    }
    
    /**
     * ��������������������
     */
    private void createSpinners() {
        //����Spinner
        Spinner spinnerDistance = (Spinner) this.findViewById(R.id.spinner_nearby_distance);
        ArrayAdapter<CharSequence> adapterDistance = ArrayAdapter.createFromResource(this, R.array.spinner_nearby_distance,
                R.drawable.near_distance_hover);
        adapterDistance.setDropDownViewResource(R.drawable.near_distance_items);
        spinnerDistance.setAdapter(adapterDistance);
        spinnerDistance.setOnItemSelectedListener(new OnItemSelectedListenerImpl(true));
        spinnerDistance.setSelection(1);//Ĭ��ѡ��5ǧ��
        
        //����Spinner
        Spinner spinnerSort = (Spinner) this.findViewById(R.id.spinner_nearby_sort);
        ArrayAdapter<CharSequence> adapterSort = ArrayAdapter.createFromResource(this, R.array.spinner_nearby_sort,
                R.drawable.near_distance_hover);
        adapterSort.setDropDownViewResource(R.drawable.near_distance_items);
        spinnerSort.setAdapter(adapterSort);
        spinnerSort.setOnItemSelectedListener(new OnItemSortListenerImpl(true));
        
    }
    
    /**
     * ѡ�о����������д������¼�
     * 
     * @author lify
     *
     */
    private class OnItemSelectedListenerImpl implements OnItemSelectedListener {

        //�ж��ǲ���ҳ���ʼ��ʱ���õ�
        private boolean initLoad;
        OnItemSelectedListenerImpl(boolean initLoad) {
            this.initLoad = initLoad;
        }
        /**
         * ��ѡ������ݸı�ʱ��Ҫ�Բ�ѯ�����������
         */
        @Override
        public void onItemSelected(AdapterView<?> diatance, View view, int position,
                long id) {
        	queryCounts = 1;
            //��ҳ����ص�ʱ�򣬻ᴥ�����¼������ִ��new Thread��������䣬������̳߳�ͻ
            if(!initLoad) {
            	// �ж������Ƿ���ã�������������ʾ�û�������������ת
            	if(!Tools.checkNetworkStatus(NearbyActivity.this)) {
            		return;
            	}
                String diatanceStr = (String) diatance.getItemAtPosition(position);// �õ�ѡ�е�ѡ��
                Log.i("select spinel: ", diatanceStr);
                Log.i("spinel id: ", String.valueOf(id));
                selectDistance = getDistance(diatanceStr);
                /** ���ڷ�Ϊ���ش洢��������أ�ÿ��������Ҫ���´�������أ�û���ж��Ƿ�ˢ�¹� */
            	//���ؽ�����
            	progressBar.setVisibility(View.VISIBLE);
            	progressBar.setProgress(0);
            	localAddress.setVisibility(View.GONE);
            	
            	mPullToRefreshView.lock();
                new Thread(new RunnableImp(String.valueOf(selectDistance), LoadWay.SPINNER_LOAD)).start();
                
            } else {
                initLoad = false;
                Log.d("loadOrder", "init load ");
            }
        }
        
        /**
         * ��ȡѡ��ľ���
         * 
         * @return ѡ��ľ���ֵ
         */
        private int getDistance(String selectDistance) {
            //Ĭ��Ϊ1
            int distance = 1;
            try {
                distance = Integer.valueOf(selectDistance.substring(2, selectDistance.indexOf("k")));
            } catch (Exception e) {
                Log.e("select distance", "ѡ��ľ���ת�������ִ���", e);
            }
            return distance;
        }

        /**
         * ʲôҲ��ѡʱ�Ĵ���
         */
        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub
            Log.d("selectSpinne", "û��ѡ�����");
        }

    }
    
    /**
     * ѡ���ұ������������д������¼������ڰ����ȶȡ��۸�
     * 
     * @author lify
     *
     */
    private class OnItemSortListenerImpl implements OnItemSelectedListener {

        //�ж��ǲ���ҳ���ʼ��ʱ���õ�
        private boolean initLoad;
        OnItemSortListenerImpl(boolean initLoad) {
            this.initLoad = initLoad;
        }
        /**
         * ��ѡ������ݸı�ʱ��Ҫ�Բ�ѯ�����������
         */
        @Override
        public void onItemSelected(AdapterView<?> diatance, View view, int position,
                long id) {
            //��ҳ����ص�ʱ�򣬻�������¼������ִ��new Thread��������䣬������̳߳�ͻ
            if(!initLoad) {
            	List<Map.Entry<Long, Summary>> list = new ArrayList<Map.Entry<Long, Summary>>();
                String diatanceStr = (String) diatance.getItemAtPosition(position);// �õ�ѡ�е�ѡ��
                if(diatanceStr.contains("�ȶ�")) {
                	list = sortByHotspot(lds);
                } else if(diatanceStr.contains("�۸�")) {
                	list = sortByPrice(lds);
                }
                showList(list);         
            } else {//��ʼ���أ�Ĭ��Ϊ�����ȶ�����
                initLoad = false;
            }
        }
        
        /**
         * ʲôҲ��ѡʱ�Ĵ���
         */
        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub
            Log.d("selectSpinne", "û��ѡ�����");
        }

    }
    
    /**
     * �����ȶ�����
     */
    private List<Map.Entry<Long, Summary>> sortByHotspot(HashMap<Long, Summary> sortMap) {
    	List<Map.Entry<Long, Summary>> infoIds = new ArrayList<Map.Entry<Long, Summary>>(sortMap.entrySet());
		
		Collections.sort(infoIds, new Comparator<Map.Entry<Long, Summary>>() {
			public int compare(Map.Entry<Long, Summary> arg0,
					Map.Entry<Long, Summary> arg1) {
				return arg0.getValue().getHot() > arg1.getValue().getHot() ? -1 : 1;
			}
		});
		
		return infoIds;
    }
    
    /**
     * ���ռ۸�����
     */
    private List<Map.Entry<Long, Summary>> sortByPrice(HashMap<Long, Summary> sortMap) {
    	List<Map.Entry<Long, Summary>> infoIds = new ArrayList<Map.Entry<Long, Summary>>(sortMap.entrySet());
    	
		Collections.sort(infoIds, new Comparator<Map.Entry<Long, Summary>>() {
			public int compare(Map.Entry<Long, Summary> arg0,
					Map.Entry<Long, Summary> arg1) {
				return arg0.getValue().getPrice() < arg1.getValue().getPrice() ? -1 : 1;
			}
		});
		
		return infoIds;
    }
    
    /**
     * ���츽���б�
     * ���ݲ�ѯ�������Ҹ������ݣ�Ȼ�����ݸ�Ҫ��Ϣ��ʾ������
     */
    private void showList(List<Map.Entry<Long, Summary>> lds) {
        LayoutInflater flater = LayoutInflater.from(this);
        LinearLayout list = (LinearLayout) this
                .findViewById(R.id.ListView_main);
        list.removeAllViews();
        
        // ��ȡ��ѯ�����ѭ�������Ҫ�б�
        for(Entry<Long, Summary> entry: lds){
            Long  destinationId = entry.getKey();
            Summary ds = entry.getValue();
            View v = flater.inflate(R.layout.listview_child_nearby, null);
//            // pic
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
            tvDistance.setText(Tools.getDistanceFormat(ds.getDistance()));
            // add view
            list.addView(v);
            // ���ü���
            v.setTag(destinationId);
            v.setOnClickListener(details);
        
        }
        //
        list.invalidate();
    }

    /**
     * ��������Ҫ��Ϣjsonת���ɶ���,Ȼ����뱾���б�
     * 
     * @param res ��Ҫ��Ϣjson��
     * @return �¼��صĸ���
     */
    private int addSummarys(String res) {
        try {
            Gson gson = new Gson();
            List<Summary> listSummary =  gson.fromJson(res, 
            		new TypeToken<List<Summary>>() {}.getType());
            Iterator<Summary> iterator = listSummary.iterator();
            while(iterator.hasNext()) {
                Summary summary = iterator.next();
                lds.put(summary.getDestinationId(),summary);
            }
            return listSummary.size();
        } catch(Exception e) {
            Log.e(TAG, "��Ҫ��Ϣjsonת������ʧ��");
        }
        return 0;
    }
    
    Handler handler4nearby = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String res = data.getString("value");
            Log.i("mylog","������Ϊ-->" + res);
            
            //�����������ʧ
            if (progressBar != null) {
            	progressBar.setVisibility(View.GONE);
            	localAddress.setVisibility(View.VISIBLE);
            }
            TextView tvTop = (TextView) NearbyActivity.this.findViewById(R.id.TextView_toppanel_title);
            //�Ϸ�ԲȦ��������ʧ
            if (loadingLayout != null) {
                 loadingLayout.setVisibility(View.GONE);
            }
            
            if(lds.size() == 0){
            	// �����̨����ҲΪ�գ�����ʾ�û����ľ�������
                tvTop.setText("δ�ҵ������Ϣ");
            }else{
            	summaryLayout.setVisibility(View.VISIBLE);
            	//��̨�߳���ɺ������ʾ���
                showList(sortByHotspot(lds));
                tvTop.setText(R.string.title_activity_nearby);
                //������ɼ���
                mPullToRefreshView.onFooterRefreshComplete();
                ++queryCounts;
            }
            
            //��������ˢ��
            mPullToRefreshView.unlock();
            spinner.setVisibility(View.VISIBLE);
            setSpinnerClickable(true);
        }
    };
    
    Runnable runnable4nearby = new RunnableImp(DEFAULE_DISTANCE, LoadWay.INIT_LOAD);
    /**
     * Runnableʵ���࣬�������󾰵�list
     */
    class RunnableImp implements Runnable {
    	//ѡ��ľ���
        String distance;
        //���ݼ��ط�ʽ��1����ʼ����, 2��ѡ��spinner��ʽ, 3��ѡ���������ط�ʽ
        LoadWay loadWay;
        RunnableImp(String distance, LoadWay loadWay) {
            this.distance = distance;
            this.loadWay = loadWay;
        }
        
        @Override
        public void run() {
        	Message msg = new Message();
            Bundle data = new Bundle();
            data.putString("value","ok");
            msg.setData(data);
            
            String Tag = "NearbyService";
            String errMsg = "��ȡ�����Ҫ��Ϣ����";
            String res = null;
            try {
                res = HttpUtil.postUrl(NEARBY_URL, getParameters(distance));
                //��������ʱû�����ݣ�������ʾ
                if(addSummarys(res) <= 0 && loadWay == LoadWay.POLLUP_LOAD) {
                	Tools.showToastLong(NearbyActivity.this, "û�и���ľ����ˡ�");
                	handler4nearby.sendMessage(msg);
                	return;
                }
                
                //�������
                int loadCount = 0;
                if(lds.size() <= 0) {//û������ʱ��ֱ�Ӽ��ص�100%
                	progressBar.setProgress(100);
                }
                for(Entry<Long, Summary> entry: lds.entrySet() ){
                    Long  destinationId = entry.getKey();
                    Summary summary = entry.getValue();
                    String picUrl = summary.getPic();
                    Bitmap bitmap = Tools.getBitmapFromUrl(picUrl);
                    pics.put(destinationId, bitmap);
                    //���հٷֱ���ʾ����
                    progressBar.setProgress(Tools.getProcessValue(++loadCount, lds.size()));
                }
            } catch (ClientProtocolException e1) {
                Log.e(Tag, errMsg, e1);
            } catch (IOException e1) {
                Log.e(Tag, errMsg, e1);
            } catch (Exception e1) {
            	Log.e(Tag, errMsg, e1);
            }
            
            handler4nearby.sendMessage(msg);
        }
    }
    
    /**
     * ���󾰵��б�ʱ������Ĳ���
     * 
     * @param defaultDistance �����ľ���
     * @return �������������list
     */
    private List<NameValuePair> getParameters(String defaultDistance) {
        List<NameValuePair> param = new ArrayList<NameValuePair>();
        //ѡ�񸽽������ͣ�1������ 2��ũ���� 3��ɽׯ 4��ũ��Ʒ
        Bundle extras = getIntent().getExtras();
        int naerbyType = extras.getInt("nearbyType");
        int typeValue = extras.getInt("typeValue");
        
        // ��ȡ�û���ǰ��ַ lat��lng
        param.add(new BasicNameValuePair("lat", String.valueOf(this.lat)));
        param.add(new BasicNameValuePair("lng", String.valueOf(this.lng)));
        //��������
        BasicNameValuePair distance = new BasicNameValuePair("distance", defaultDistance);
        param.add(distance);
        //�ڼ�������
        BasicNameValuePair count = new BasicNameValuePair("count", String.valueOf(queryCounts));
        param.add(count);
        //ѡ�񸽽�������
        BasicNameValuePair type = new BasicNameValuePair("type", String.valueOf(naerbyType));
        param.add(type);
        //ѡ���������
        BasicNameValuePair value = new BasicNameValuePair("type_value", String.valueOf(typeValue));
        param.add(value);
        
        return param;
    }

    /**
     * ����ˢ�´���
     */
	@Override
	public void onFooterRefresh(PullToRefreshView view) {
		Log.d("�������", queryCounts + "");
		setSpinnerClickable(false);
		new Thread(new RunnableImp(String.valueOf(selectDistance), LoadWay.POLLUP_LOAD)).start();
	}
	
}
