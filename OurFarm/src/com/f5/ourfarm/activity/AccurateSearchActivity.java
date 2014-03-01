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
 * 高级搜索
 *
 */
public class AccurateSearchActivity extends Activity implements OnClickListener{
 
    
    
    private OnClickListener lback = null;
    //区域适配器
    private AreaAdapter areaAdapter;
    //区域列表
    private ListView list4Area;  
    //监听是否展开arealist
    private OnClickListener larea;
    private boolean visable4Area = false;
    //区域全选checkbox
    private CheckBox checkall4Area;
    //区域列表监听器，监听列表item
    private OnItemClickListener itemListener4Area ;
    //区域列表holder
    private ListItemViews holderViews4Area; 
    
  //label适配器
    private LabelAdapter labelAdapter;
    //label列表
    private ListView list4label; 
    //
  //监听是否展开labellist
    private OnClickListener llabel;
    private boolean visable4label = false;
    //label全选checkbox
    private CheckBox checkall4label;
    //区域列表监听器，监听列表item
    private OnItemClickListener itemListener4label ;
    //区域列表holder
    private ListItemViews holderViews4label;
    
    //count适配器
    private CountAdapter countAdapter;
    //count列表
    private ListView list4count;  
    //监听是否展开countlist
    private OnClickListener lcount;
    private boolean visable4count = false;
    //区域列表监听器，监听列表item
    private OnItemClickListener itemListener4count ;
    
    
    //
    private CheckBox check4classic;
    //默认显示非经典
    private String classic = "0";
    
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
    
    //通用列表item类
    private final class ListItemViews {  
        TextView nameView;  
        CheckBox checkBox;  
    } 

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); 
        // 去除标题栏
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
        //准备listeners
        this.prepareListeners();
        //设置listeners
        this.batchSetListeners();
        
        
        
    }

    /**
     * 监听到事件后的动作；
     */
    private void prepareListeners() {
        //nearby->home
        lback = new OnClickListener() {
            public void onClick(View v) {
            	finish();
            }
        };
        
        //展开或折叠list
        larea = new OnClickListener() {
            public void onClick(View v) {
                //重置标识
                visable4Area = visable4Area?false:true;
                //设置是否可见
                ViewGroup.LayoutParams params = list4Area.getLayoutParams();
                if(visable4Area ){
                  //动态设置list高度
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
        
        //展开或折叠list
        llabel = new OnClickListener() {
            public void onClick(View v) {
                //重置标识
                visable4label = visable4label?false:true;
                //设置是否可见
                ViewGroup.LayoutParams params = list4label.getLayoutParams();
                if(visable4label ){
                  //动态设置list高度
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
        
      //展开或折叠list
        lcount = new OnClickListener() {
            public void onClick(View v) {
                //重置标识
                visable4count = visable4count?false:true;
                //设置是否可见
                ViewGroup.LayoutParams params = list4count.getLayoutParams();
                if(visable4count ){
                  //动态设置list高度
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
        
     // list 监听
        itemListener4Area = new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                ListItemViews views = (ListItemViews) view.getTag();
                views.checkBox.toggle();
                areaAdapter.selectedMap4Area.put(position,
                        views.checkBox.isChecked());
                // 判断是否有记录没被选中，以便修改全选CheckBox勾选状态
                if (areaAdapter.selectedMap4Area.containsValue(false)) {
                    checkall4Area.setChecked(false);
                } else {
                    checkall4Area.setChecked(true);
                }
                areaAdapter.notifyDataSetChanged();
            }

        };
      //设置label监听，
        itemListener4label = new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                ListItemViews views = (ListItemViews) view.getTag();
                views.checkBox.toggle();
                labelAdapter.selectedMap4label.put(position,
                        views.checkBox.isChecked());
                // 判断是否有记录没被选中，以便修改全选CheckBox勾选状态
                if (labelAdapter.selectedMap4label.containsValue(false)) {
                    checkall4label.setChecked(false);
                } else {
                    checkall4label.setChecked(true);
                }
                labelAdapter.notifyDataSetChanged();
            }

        };
        //设置count监听，
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
                // 判断是否有记录没被选中，以便修改全选CheckBox勾选状态
                countAdapter.notifyDataSetChanged();
            }

        };
    }
    
    /**
     * 绑定view和监听
     */
    private void batchSetListeners() {
        // back
        ImageView iback2home = (ImageView) this.findViewById(R.id.ImageView_button_back);
        iback2home.setOnClickListener(lback);

        //监听search
        TextView tv4search = (TextView) this.findViewById(R.id.TextView_accurate_search);
        tv4search.setOnClickListener(this);
        
        //监听是否展开area list
        TextView tv4area = (TextView) this.findViewById(R.id.TextView_accurate_area);
        tv4area.setOnClickListener(larea);
        
        //设置区域监听，
        list4Area.setOnItemClickListener(itemListener4Area);
        // check all 监听
        checkall4Area = (CheckBox) this
                .findViewById(R.id.CheckBox_accurate_area);
        checkall4Area.setOnClickListener(this);
        
        //监听是否展开label list
        TextView tv4label = (TextView) this.findViewById(R.id.TextView_accurate_label);
        tv4label.setOnClickListener(llabel);
        
        // list 监听
        list4label.setOnItemClickListener(itemListener4label);
        // check all 监听
        checkall4label = (CheckBox) this
                .findViewById(R.id.CheckBox_accurate_label);
        checkall4label.setOnClickListener(this);
        
      //监听是否展开count list
        TextView tv4count = (TextView) this.findViewById(R.id.TextView_accurate_count);
        tv4count.setOnClickListener(lcount);
     // count  监听
        list4count.setOnItemClickListener(itemListener4count);
        
       //classic  监听
        check4classic = (CheckBox) this
                .findViewById(R.id.CheckBox_accurate_classic);
        check4classic.setOnClickListener(this);
     
    }

    @Override
    public void onClick(View v) {
	    switch (v.getId()) { 
	        //全选区域
	        case R.id.CheckBox_accurate_area: 
	            if(checkall4Area.isChecked()){
	                for (int i = 0; i < areaAdapter.getCount(); i++) {
	                    //更改结果
	                    areaAdapter.selectedMap4Area.put(i, true); 
	                } 
	            }else {  
	                for (int i = 0; i < areaAdapter.getCount(); i++) {  
	                    areaAdapter.selectedMap4Area.put(i, false); 
	                } 
	            } 
	            //通知变更
	            areaAdapter.notifyDataSetChanged();
	            break; 
	            //仅显示经典
	        case R.id.CheckBox_accurate_classic: 
	            if(check4classic.isChecked()){
	                classic = "1";
	            }else{
	                classic = "0";
	            }
	            break; 
	            //全选label
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
	            //开始search
	        case R.id.TextView_accurate_search: {
	            // 构造区域参数
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
	            // 构造标签参数
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
	            //数目
	            String count = "1";
	            //
	            Intent intent = new Intent(AccurateSearchActivity.this,FindActivity.class);
	            // 将服务器返回数据保存入Intent，确保数据可在activity间传递
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
        // 保存每条记录是否被选中的状态
         Map<Integer, Boolean> selectedMap4Area = new HashMap<Integer, Boolean>();
     // 保存被选中记录作数据库表中的Id  
         Set<String> idSet4Area = new HashSet<String>();
        
        public AreaAdapter(Context context) {
            //调用区域service，得到区域列表
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
            // 保存记录Id  
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
        // 保存每条记录是否被选中的状态
         Map<Integer, Boolean> selectedMap4label = new HashMap<Integer, Boolean>();
     // 保存被选中记录作数据库表中的Id  
         Set<String> idSet4label = new HashSet<String>();
         List< Map<String, Object>> labelList = new ArrayList< Map<String, Object>>();
         
        public LabelAdapter(Context context) {
            Map<String, Object> a = new HashMap<String, Object>();
            a.put("label_name", "爬山");
            labelList.add(a);
            Map<String, Object> b = new HashMap<String, Object>();
            b.put("label_name", "玩水");
            labelList.add(b);
            Map<String, Object> c = new HashMap<String, Object>();
            c.put("label_name", "探险");
            labelList.add(c);
            Map<String, Object> d = new HashMap<String, Object>();
            d.put("label_name", "休闲");
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
            // 保存记录Id  
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
        
     // 保存被选中记录作数据库表中的Id  
         String count = "1";
      // 保存每条记录是否被选中的状态
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
