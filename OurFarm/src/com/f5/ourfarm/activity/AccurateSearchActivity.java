package com.f5.ourfarm.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract.Contacts;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.f5.ourfarm.R;
import com.f5.ourfarm.service.AreaService;
import com.f5.ourfarm.util.Tools;
import com.umeng.analytics.MobclickAgent;


/**
 * @author tianhao
 * �߼�����
 *
 */
public class AccurateSearchActivity extends Activity implements OnClickListener{
 
    
    
    private OnClickListener lback = null;
    //����������
    private AreaAdapter areaAdapter;
    //�����б�
    private ListView list4Area;  
    //�����Ƿ�չ��arealist
    private OnClickListener larea;
    private boolean visable4Area = false;
    //����ȫѡcheckbox
    private CheckBox checkall4Area;
    //�����б�������������б�item
    private OnItemClickListener itemListener4Area ;
    //�����б�holder
    private ListItemViews holderViews4Area; 
    
  //label������
    private LabelAdapter labelAdapter;
    //label�б�
    private ListView list4label; 
    //
  //�����Ƿ�չ��labellist
    private OnClickListener llabel;
    private boolean visable4label = false;
    //labelȫѡcheckbox
    private CheckBox checkall4label;
    //�����б�������������б�item
    private OnItemClickListener itemListener4label ;
    //�����б�holder
    private ListItemViews holderViews4label;
    
    //count������
    private CountAdapter countAdapter;
    //count�б�
    private ListView list4count;  
    //�����Ƿ�չ��countlist
    private OnClickListener lcount;
    private boolean visable4count = false;
    //�����б�������������б�item
    private OnItemClickListener itemListener4count ;
    
    
    //
    private CheckBox check4classic;
    //Ĭ����ʾ�Ǿ���
    private String classic = "0";
    
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
    
    //ͨ���б�item��
    private final class ListItemViews {  
        TextView nameView;  
        CheckBox checkBox;  
    } 

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); 
        // ȥ��������
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_accurate_search);
        //
        areaAdapter = new AreaAdapter(this);
        list4Area = (ListView) this
                .findViewById(R.id.ListView_accurate_area);
        list4Area.setAdapter(areaAdapter);
        //
        labelAdapter = new LabelAdapter(this);
        list4label = (ListView) this
                .findViewById(R.id.ListView_accurate_label);
        list4label.setAdapter(labelAdapter);
        //
        countAdapter = new CountAdapter(this);
        list4count = (ListView) this
                .findViewById(R.id.ListView_accurate_count);
        list4count.setAdapter(countAdapter);
        //׼��listeners
        this.prepareListeners();
        //����listeners
        this.batchSetListeners();
        
        
        
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
        
        //չ�����۵�list
        larea = new OnClickListener() {
            public void onClick(View v) {
                //���ñ�ʶ
                visable4Area = visable4Area?false:true;
                //�����Ƿ�ɼ�
                ViewGroup.LayoutParams params = list4Area.getLayoutParams();
                if(visable4Area ){
                  //��̬����list�߶�
                    int num = list4Area.getAdapter().getCount();
                    int totalHeight = num*30;
                    params.height = totalHeight;
                    list4Area.setLayoutParams(params);
                }else{
                    params.height = 0;
                    list4Area.setLayoutParams(params);
                }
            }
        };
        
        //չ�����۵�list
        llabel = new OnClickListener() {
            public void onClick(View v) {
                //���ñ�ʶ
                visable4label = visable4label?false:true;
                //�����Ƿ�ɼ�
                ViewGroup.LayoutParams params = list4label.getLayoutParams();
                if(visable4label ){
                  //��̬����list�߶�
                    int num = list4label.getAdapter().getCount();
                    int totalHeight = num*50;
                    params.height = totalHeight;
                    list4label.setLayoutParams(params);
                }else{
                    params.height = 0;
                    list4label.setLayoutParams(params);
                }
            }
        };
        
      //չ�����۵�list
        lcount = new OnClickListener() {
            public void onClick(View v) {
                //���ñ�ʶ
                visable4count = visable4count?false:true;
                //�����Ƿ�ɼ�
                ViewGroup.LayoutParams params = list4count.getLayoutParams();
                if(visable4count ){
                  //��̬����list�߶�
                    int num = list4count.getAdapter().getCount();
                    int totalHeight = num*50;
                    params.height = totalHeight;
                    list4count.setLayoutParams(params);
                }else{
                    params.height = 0;
                    list4count.setLayoutParams(params);
                }
            }
        };
        
     // list ����
        itemListener4Area = new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                ListItemViews views = (ListItemViews) view.getTag();
                views.checkBox.toggle();
                areaAdapter.selectedMap4Area.put(position,
                        views.checkBox.isChecked());
                // �ж��Ƿ��м�¼û��ѡ�У��Ա��޸�ȫѡCheckBox��ѡ״̬
                if (areaAdapter.selectedMap4Area.containsValue(false)) {
                    checkall4Area.setChecked(false);
                } else {
                    checkall4Area.setChecked(true);
                }
                areaAdapter.notifyDataSetChanged();
            }

        };
      //����label������
        itemListener4label = new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                ListItemViews views = (ListItemViews) view.getTag();
                views.checkBox.toggle();
                labelAdapter.selectedMap4label.put(position,
                        views.checkBox.isChecked());
                // �ж��Ƿ��м�¼û��ѡ�У��Ա��޸�ȫѡCheckBox��ѡ״̬
                if (labelAdapter.selectedMap4label.containsValue(false)) {
                    checkall4label.setChecked(false);
                } else {
                    checkall4label.setChecked(true);
                }
                labelAdapter.notifyDataSetChanged();
            }

        };
        //����count������
        itemListener4count = new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                ListItemViews views = (ListItemViews) view.getTag();
                views.checkBox.toggle();
                //
                for (int i = 0; i < countAdapter.getCount(); i++) {  
                    countAdapter.selectedMap4count.put(i, false);  
                } 
                countAdapter.selectedMap4count.put(position,
                        views.checkBox.isChecked());
                // �ж��Ƿ��м�¼û��ѡ�У��Ա��޸�ȫѡCheckBox��ѡ״̬
                countAdapter.notifyDataSetChanged();
            }

        };
    }
    
    /**
     * ��view�ͼ���
     */
    private void batchSetListeners() {
        // back
        ImageView iback2home = (ImageView) this.findViewById(R.id.ImageView_button_back);
        iback2home.setOnClickListener(lback);

        //����search
        TextView tv4search = (TextView) this.findViewById(R.id.TextView_accurate_search);
        tv4search.setOnClickListener(this);
        
        //�����Ƿ�չ��area list
        TextView tv4area = (TextView) this.findViewById(R.id.TextView_accurate_area);
        tv4area.setOnClickListener(larea);
        
        //�������������
        list4Area.setOnItemClickListener(itemListener4Area);
        // check all ����
        checkall4Area = (CheckBox) this
                .findViewById(R.id.CheckBox_accurate_area);
        checkall4Area.setOnClickListener(this);
        
        //�����Ƿ�չ��label list
        TextView tv4label = (TextView) this.findViewById(R.id.TextView_accurate_label);
        tv4label.setOnClickListener(llabel);
        
        // list ����
        list4label.setOnItemClickListener(itemListener4label);
        // check all ����
        checkall4label = (CheckBox) this
                .findViewById(R.id.CheckBox_accurate_label);
        checkall4label.setOnClickListener(this);
        
      //�����Ƿ�չ��count list
        TextView tv4count = (TextView) this.findViewById(R.id.TextView_accurate_count);
        tv4count.setOnClickListener(lcount);
     // count  ����
        list4count.setOnItemClickListener(itemListener4count);
        
       //classic  ����
        check4classic = (CheckBox) this
                .findViewById(R.id.CheckBox_accurate_classic);
        check4classic.setOnClickListener(this);
     
    }

    @Override
    public void onClick(View v) {
	    switch (v.getId()) { 
	        //ȫѡ����
	        case R.id.CheckBox_accurate_area: 
	            if(checkall4Area.isChecked()){
	                for (int i = 0; i < areaAdapter.getCount(); i++) {
	                    //���Ľ��
	                    areaAdapter.selectedMap4Area.put(i, true); 
	                } 
	            }else {  
	                for (int i = 0; i < areaAdapter.getCount(); i++) {  
	                    areaAdapter.selectedMap4Area.put(i, false); 
	                } 
	            } 
	            //֪ͨ���
	            areaAdapter.notifyDataSetChanged();
	            break; 
	            //����ʾ����
	        case R.id.CheckBox_accurate_classic: 
	            if(check4classic.isChecked()){
	                classic = "1";
	            }else{
	                classic = "0";
	            }
	            break; 
	            //ȫѡlabel
	        case R.id.CheckBox_accurate_label: 
	            if(checkall4label.isChecked()){
	                for (int i = 0; i < labelAdapter.getCount(); i++) {
	                    //
	                    labelAdapter.selectedMap4label.put(i, true); 
	                } 
	            }else {  
	                for (int i = 0; i < areaAdapter.getCount(); i++) {  
	                    labelAdapter.selectedMap4label.put(i, false); 
	                } 
	            } 
	            labelAdapter.notifyDataSetChanged();
	            break; 
	            //��ʼsearch
	        case R.id.TextView_accurate_search: {
	            // �����������
	            String regionCode = "";
	            if(checkall4Area.isChecked()){
	            	regionCode = "0";
	            }else{
	            	Iterator<String> iterator = areaAdapter.idSet4Area.iterator();
	                int size = areaAdapter.idSet4Area.size();
	                if (size >= 1) {
	                    regionCode = iterator.next();
	                }
	                for (int i = 2; i <= size; i++) {
	                    regionCode = regionCode + "&" + iterator.next();
	                }
	            }
	            
	            Log.i("regionCode is ", regionCode);
	            //
	            // �����ǩ����
	            String labels = "";
	            if(checkall4label.isChecked()){
	            	labels = "0";
	            }else{
	            	Iterator<String> iterator4label = labelAdapter.idSet4label.iterator();
	                int size = labelAdapter.idSet4label.size();
	                if (size >= 1) {
	                    labels = iterator4label.next();
	                }
	                for (int i = 2; i <= size; i++) {
	                    labels = labels + "&" + iterator4label.next();
	                }
	            }
	            
	            Log.i("labels is ", labels);
	            //��Ŀ
	            String count = "1";
	            //
	            Intent intent = new Intent(AccurateSearchActivity.this,FindActivity.class);
	            // ���������������ݱ�����Intent��ȷ�����ݿ���activity�䴫��
	            intent.putExtra("regionId", String.valueOf(regionCode));
	            intent.putExtra("classicFlag", classic);
	            intent.putExtra("labels", labels);
	            intent.putExtra("count", count);
	            startActivity(intent);
	
	        }
	    }
    }
    
    class AreaAdapter extends BaseAdapter {
        SimpleAdapter simpleadapter;
        List<Map<String, Object>> areaList;
        // ����ÿ����¼�Ƿ�ѡ�е�״̬
         Map<Integer, Boolean> selectedMap4Area = new HashMap<Integer, Boolean>();
     // ���汻ѡ�м�¼�����ݿ���е�Id  
         Set<String> idSet4Area = new HashSet<String>();
        
        public AreaAdapter(Context context) {
            //��������service���õ������б�
            AreaService service = new AreaService();
            areaList = service.getAllRegionForListView();
            simpleadapter = new SimpleAdapter(AccurateSearchActivity.this, areaList, 
                    R.layout.activity_accurate_list_item, new String[]{"region_name"}, new int[]{R.id.TextView_item});
            for (int i = 0; i < simpleadapter.getCount(); i++) {  
                selectedMap4Area.put(i, false);  
            }  
        }

        @Override
        public int getCount() {
            return  simpleadapter.getCount();
        }

        @Override
        public Object getItem(int position) {
            return simpleadapter.getItem(position);
        }

        @Override
        public long getItemId(int position) {
            return simpleadapter.getItemId(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {  
                convertView = LayoutInflater.from(AccurateSearchActivity.this).inflate(R.layout.activity_accurate_list_item, null);  
                holderViews4Area = new ListItemViews();  
                holderViews4Area.nameView = (TextView) convertView  
                        .findViewById(R.id.TextView_item);  
                holderViews4Area.checkBox = (CheckBox) convertView  
                        .findViewById(R.id.CheckBox_item);  
                convertView.setTag(holderViews4Area);  
            } 
            ListItemViews views = (ListItemViews) convertView.getTag(); 
            Map<String, Object> o = (Map<String, Object>) simpleadapter.getItem(position);
            views.nameView.setText(o.get("region_name").toString());  
            views.checkBox.setChecked(selectedMap4Area.get(position));  
            // �����¼Id  
            if (selectedMap4Area.get(position)) {  
                idSet4Area.add(o.get("region_id").toString());  
            } else {  
                idSet4Area.remove(o.get("region_id").toString());  
            }  
            return convertView;  
        }
    }
    
    class LabelAdapter extends BaseAdapter {
        SimpleAdapter  simpleadapter;
        // ����ÿ����¼�Ƿ�ѡ�е�״̬
         Map<Integer, Boolean> selectedMap4label = new HashMap<Integer, Boolean>();
     // ���汻ѡ�м�¼�����ݿ���е�Id  
         Set<String> idSet4label = new HashSet<String>();
         List< Map<String, Object>> labelList = new ArrayList< Map<String, Object>>();
         
        public LabelAdapter(Context context) {
            Map<String, Object> a = new HashMap<String, Object>();
            a.put("label_name", "��ɽ");
            labelList.add(a);
            Map<String, Object> b = new HashMap<String, Object>();
            b.put("label_name", "��ˮ");
            labelList.add(b);
            Map<String, Object> c = new HashMap<String, Object>();
            c.put("label_name", "̽��");
            labelList.add(c);
            Map<String, Object> d = new HashMap<String, Object>();
            d.put("label_name", "����");
            labelList.add(d);
            simpleadapter = new SimpleAdapter(AccurateSearchActivity.this, labelList, 
                    R.layout.activity_accurate_list_item, new String[]{"label_name"}, new int[]{R.id.TextView_item});
            for (int i = 0; i < simpleadapter.getCount(); i++) {  
                selectedMap4label.put(i, false);  
            }  
        }

        @Override
        public int getCount() {
            return  simpleadapter.getCount();
        }

        @Override
        public Object getItem(int position) {
            return simpleadapter.getItem(position);
        }

        @Override
        public long getItemId(int position) {
            return simpleadapter.getItemId(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {  
                convertView = LayoutInflater.from(AccurateSearchActivity.this).inflate(R.layout.activity_accurate_list_item, null);  
                holderViews4label = new ListItemViews();  
                holderViews4label.nameView = (TextView) convertView  
                        .findViewById(R.id.TextView_item);  
                holderViews4label.checkBox = (CheckBox) convertView  
                        .findViewById(R.id.CheckBox_item);  
                convertView.setTag(holderViews4label);  
            } 
            ListItemViews views = (ListItemViews) convertView.getTag(); 
            Map<String, Object> o = (Map<String, Object>) simpleadapter.getItem(position);
            views.nameView.setText(o.get("label_name").toString());  
            views.checkBox.setChecked(selectedMap4label.get(position));  
            // �����¼Id  
            if (selectedMap4label.get(position)) {  
                idSet4label.add(o.get("label_name").toString());  
            } else {  
                idSet4label.remove(o.get("label_name").toString());  
            }  
            return convertView;  
        }
    }
    class CountAdapter extends BaseAdapter {
        ArrayAdapter<CharSequence>   simpleadapter;
        
     // ���汻ѡ�м�¼�����ݿ���е�Id  
         String count = "1";
      // ����ÿ����¼�Ƿ�ѡ�е�״̬
         Map<Integer, Boolean> selectedMap4count = new HashMap<Integer, Boolean>();
         
        public CountAdapter(Context context) {
            simpleadapter = ArrayAdapter.createFromResource(AccurateSearchActivity.this, R.array.list_count,
                    R.drawable.near_distance_hover);
            for (int i = 0; i < simpleadapter.getCount(); i++) {  
                selectedMap4count.put(i, false);  
            } 
        }

        @Override
        public int getCount() {
            return  simpleadapter.getCount();
        }

        @Override
        public Object getItem(int position) {
            return simpleadapter.getItem(position);
        }

        @Override
        public long getItemId(int position) {
            return simpleadapter.getItemId(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {  
                convertView = LayoutInflater.from(AccurateSearchActivity.this).inflate(R.layout.activity_accurate_list_item, null);  
                holderViews4label = new ListItemViews();  
                holderViews4label.nameView = (TextView) convertView  
                        .findViewById(R.id.TextView_item);  
                holderViews4label.checkBox = (CheckBox) convertView  
                        .findViewById(R.id.CheckBox_item);  
                convertView.setTag(holderViews4label);  
            } 
            ListItemViews views = (ListItemViews) convertView.getTag(); 
            views.nameView.setText(simpleadapter.getItem(position).toString());  
            views.checkBox.setChecked(selectedMap4count.get(position));  
            if (selectedMap4count.get(position)) {  
                count =  String.valueOf(position+1);
            } 
            return convertView;  
        }
    }
    
}
