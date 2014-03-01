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

	//�����б�
	private List<Map<String, Object>> areaList;
    OnClickListener lback = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // ȥ��������
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //׼��listeners
        this.prepareListeners();
        setContentView(R.layout.activity_area);
        //����listeners
        this.batchSetListeners();
        //����listview
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
     * �������¼���Ķ�����
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
     * ��view�ͼ���
     */
    private void batchSetListeners() {
        ImageView iback2home = (ImageView) this.findViewById(R.id.ImageView_button_back);
        iback2home.setOnClickListener(lback);
        
    }
    
    /**
     * ���һ������
     */
	@Override
	protected void onListItemClick(android.widget.ListView listView, View view,
			int position, long id) {
		super.onListItemClick(listView, view, position, id);
		int regionId = (Integer) areaList.get(position).get("region_id");
		String regionName = (String) areaList.get(position).get("region_name");
		Intent intent = new Intent(AreaActivity.this, FindActivity.class);

		// ���������������ݱ�����Intent��ȷ�����ݿ���activity�䴫��
		intent.putExtra("regionId", String.valueOf(regionId));
		//��������
		intent.putExtra("regionName", regionName);
		//TODO ��ʱû��ʹ��
		intent.putExtra("labels", "");
		//0��Ǿ��䣬1������  ����Ĭ��Ϊ0�������ѯ����ȫ����Ϊ�Ǿ������ݣ�����
		intent.putExtra("classicFlag", "");
		startActivity(intent);
	}

    /**
     * ��ʾ����
     */
    private void creatListView(){
    	//��������service���õ������б�
        AreaService service = new AreaService();
        areaList = service.getAllRegionForListView();
        SimpleAdapter simpleadapter = new SimpleAdapter(this, areaList, 
        		R.layout.activity_area_list_item, new String[]{"region_name"}, new int[]{R.id.region_name});
        setListAdapter(simpleadapter);
    }
    
}
