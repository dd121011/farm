package com.f5.ourfarm.activity;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.URLUtil;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.f5.ourfarm.R;
import com.f5.ourfarm.activity.RankingShowActivity.RunnableImp;
import com.f5.ourfarm.adapter.GroupAdapter;
import com.f5.ourfarm.model.DestinationType;
import com.f5.ourfarm.model.LoadWay;
import com.f5.ourfarm.model.RankingParams;
import com.f5.ourfarm.util.Constants;
import com.f5.ourfarm.util.Tools;

public class BaseActivity extends Activity{
	//���˰�ť
    protected OnClickListener lback = null;
    // �����绰��������
 	OnClickListener phoneCall = null;
 	// ����URL����¼�
 	OnClickListener netCall = null;
    //�����ײ����4����ť
    protected OnClickListener lPanelBottom = null;
    
    //��������
    protected PopupWindow popupWindow;  
    //�������ڵ�ҳ�沼��
    private View rankingLayout;  
    //���������б�
    private ListView listViewGroup; 
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// ȥ��������
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        prepareListeners();
	}
	
	/**
	 * �������¼���Ķ�����
	 */
	private void prepareListeners() {
		// ���˰�ť
		lback = new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		};
		
		//�绰�����¼�
		phoneCall = new OnClickListener() {
			public void onClick(View v) {
				//�绰����
				String callee = (String)v.getTag();
				//�жϵ绰�����Ƿ����
				if (PhoneNumberUtils.isGlobalPhoneNumber(callee)) {
					//�������Ż��棬���û�ȷ���Ƿ񲦺�
					Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel://" + callee));
					startActivity(callIntent);
				} else {
					Toast.makeText(getApplicationContext(), R.string.detail_error_phoneno, Toast.LENGTH_LONG).show();
				}
			}
		};
		
		//URL����¼�
		netCall = new OnClickListener() {
			public void onClick(View v) {
				//Url��ַ
				String url = (String)v.getTag();
				//�����վ�ĺϷ���
			    if(URLUtil.isNetworkUrl(url)){
			    	Intent intent= new Intent();        
			        intent.setAction("android.intent.action.VIEW");    
			        intent.setData(Uri.parse(url));  
			        startActivity(intent);
			    } else {
			    	Toast.makeText(getApplicationContext(), R.string.detail_error_url, Toast.LENGTH_LONG).show();
			    }
			}
		};
		
		//�ײ����4����ť���л�
        lPanelBottom = new OnClickListener() {
            public void onClick(View v) {
                //�жϰ������ĸ���ť
                int tag = (Integer) v.getTag();
                switch (tag) {
	                case 1:
	                	if(!Tools.checkNetworkStatus(BaseActivity.this)) {
	                		return;
	                	}
	                	
	                    Intent home2play = new Intent(getApplicationContext(),PlanActivity.class);
	                    home2play.putExtra("planType", 20);
	                    home2play.putExtra("planName", "�ȼ�����");
	                    startActivity(home2play);
	                    break;
	                case 2://����
	                	if(!Tools.checkNetworkStatus(BaseActivity.this)) {
	                		return;
	                	}
	                	Intent i2Ranking = new Intent(getApplicationContext(), RankingShowActivity.class);
	                    startActivity(i2Ranking);
	                    break;
	                case 3://�ҵ�
	                    Intent i2my = new Intent(getApplicationContext(), HomeMyActivity.class);
	                    startActivity(i2my);
	                    break;
	                case 4://����
	                    Intent i2more = new Intent(getApplicationContext(), HomeMoreActivity.class);
	                    startActivity(i2more);
	                    break;
	                case 5://����
	                    Intent i2main = new Intent(getApplicationContext(), MainActivity.class);
	                    startActivity(i2main);
	                    break;
                }
            }
        };
	}
	
	/**
	 * ��������ʧ��
	 */
	protected void getDataError() {
		Tools.showToastLong(BaseActivity.this, "���ݼ���ʧ�ܡ�");
	}
	
	/**
	 * �������ʱ�ĵ�������
	 * 
	 * @param clickView ����������
	 * @param groups ��ʾ��list
	 * @param listener �����������¼�
	 */
	protected void showPopupWindow(View clickView, List<String> groups, OnItemClickListener listener) {  
  	  
    	//��ȡ��Ļ�Ŀ�Ⱥ͸߶�
    	WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
    	int screanWidth = windowManager.getDefaultDisplay().getWidth();
    	int screanHeight = windowManager.getDefaultDisplay().getHeight();
    	
        if (popupWindow == null) {  
            LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
            rankingLayout = layoutInflater.inflate(R.layout.listgroup_ranking, null);  
            listViewGroup = (ListView) rankingLayout.findViewById(R.id.ListViewGroup);  
  
            GroupAdapter groupAdapter = new GroupAdapter(this, groups);  
            listViewGroup.setAdapter(groupAdapter);  
            // ����һ��PopuWidow���� ,�߶�Ϊ��Ļ��7/8,���Ϊ3/4
            popupWindow = new PopupWindow(rankingLayout, screanWidth * 7 / 8, screanHeight * 3 / 4);
        }  
  
        // ʹ��ۼ�  
        popupWindow.setFocusable(true);  
        // ����������������ʧ  
        popupWindow.setOutsideTouchable(true);
        popupWindow.setAnimationStyle(R.anim.popup_window);
        // �����Ϊ�˵��������Back��Ҳ��ʹ����ʧ�����Ҳ�����Ӱ����ı���  
        popupWindow.setBackgroundDrawable(new BitmapDrawable()); 
        
        //�������ڵĲ��յ�
        LinearLayout title = (LinearLayout) this.findViewById(R.id.toppanel);
        //��ʾ��λ��Ϊ:��Ļ�Ŀ�ȵ�һ��-PopupWindow�Ŀ�ȵ�һ��  
        popupWindow.showAsDropDown(title, (screanWidth - popupWindow.getWidth()) / 2, 5);  
  
        listViewGroup.setOnItemClickListener(listener);  
    }  
	
}

