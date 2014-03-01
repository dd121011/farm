package com.f5.ourfarm.activity;

import java.util.List;
import java.util.Map;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.SimpleAdapter;

import com.f5.ourfarm.R;
import com.f5.ourfarm.service.AreaService;
import com.umeng.analytics.MobclickAgent;


/**
 * @author tianhao
 *
 */
public class AreaActivity extends ListActivity {

	//区域列表
	private List<Map<String, Object>> areaList;
    OnClickListener lback = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 去除标题栏
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //准备listeners
        this.prepareListeners();
        setContentView(R.layout.activity_area);
        //设置listeners
        this.batchSetListeners();
        //设置listview
        this.creatListView();
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
        //nearby->home
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
     * 点击一个区域
     */
	@Override
	protected void onListItemClick(android.widget.ListView listView, View view,
			int position, long id) {
		super.onListItemClick(listView, view, position, id);
		int regionId = (Integer) areaList.get(position).get("region_id");
		String regionName = (String) areaList.get(position).get("region_name");
		Intent intent = new Intent(AreaActivity.this, FindActivity.class);

		// 将服务器返回数据保存入Intent，确保数据可在activity间传递
		intent.putExtra("regionId", String.valueOf(regionId));
		//区域名字
		intent.putExtra("regionName", regionName);
		//TODO 暂时没有使用
		intent.putExtra("labels", "");
		//0查非经典，1：经典  传空默认为0，区域查询的是全部，为非经典数据，传空
		intent.putExtra("classicFlag", "");
		startActivity(intent);
	}

    /**
     * 显示区域
     */
    private void creatListView(){
    	//调用区域service，得到区域列表
        AreaService service = new AreaService();
        areaList = service.getAllRegionForListView();
        SimpleAdapter simpleadapter = new SimpleAdapter(this, areaList, 
        		R.layout.activity_area_list_item, new String[]{"region_name"}, new int[]{R.id.region_name});
        setListAdapter(simpleadapter);
    }
    
}
