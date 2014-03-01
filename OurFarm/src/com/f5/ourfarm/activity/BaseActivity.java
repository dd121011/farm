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
	//后退按钮
    protected OnClickListener lback = null;
    // 监听电话弹出拨号
 	OnClickListener phoneCall = null;
 	// 监听URL点击事件
 	OnClickListener netCall = null;
    //监听底部面板4个按钮
    protected OnClickListener lPanelBottom = null;
    
    //弹出窗口
    protected PopupWindow popupWindow;  
    //弹出窗口的页面布局
    private View rankingLayout;  
    //弹出窗口列表
    private ListView listViewGroup; 
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 去除标题栏
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        prepareListeners();
	}
	
	/**
	 * 监听到事件后的动作；
	 */
	private void prepareListeners() {
		// 后退按钮
		lback = new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		};
		
		//电话拨号事件
		phoneCall = new OnClickListener() {
			public void onClick(View v) {
				//电话号码
				String callee = (String)v.getTag();
				//判断电话号码是否合理
				if (PhoneNumberUtils.isGlobalPhoneNumber(callee)) {
					//弹出拨号画面，待用户确认是否拨号
					Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel://" + callee));
					startActivity(callIntent);
				} else {
					Toast.makeText(getApplicationContext(), R.string.detail_error_phoneno, Toast.LENGTH_LONG).show();
				}
			}
		};
		
		//URL点击事件
		netCall = new OnClickListener() {
			public void onClick(View v) {
				//Url地址
				String url = (String)v.getTag();
				//检测网站的合法性
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
		
		//底部面板4个按钮间切换
        lPanelBottom = new OnClickListener() {
            public void onClick(View v) {
                //判断按的是哪个按钮
                int tag = (Integer) v.getTag();
                switch (tag) {
	                case 1:
	                	if(!Tools.checkNetworkStatus(BaseActivity.this)) {
	                		return;
	                	}
	                	
	                    Intent home2play = new Intent(getApplicationContext(),PlanActivity.class);
	                    home2play.putExtra("planType", 20);
	                    home2play.putExtra("planName", "度假休闲");
	                    startActivity(home2play);
	                    break;
	                case 2://排名
	                	if(!Tools.checkNetworkStatus(BaseActivity.this)) {
	                		return;
	                	}
	                	Intent i2Ranking = new Intent(getApplicationContext(), RankingShowActivity.class);
	                    startActivity(i2Ranking);
	                    break;
	                case 3://我的
	                    Intent i2my = new Intent(getApplicationContext(), HomeMyActivity.class);
	                    startActivity(i2my);
	                    break;
	                case 4://更多
	                    Intent i2more = new Intent(getApplicationContext(), HomeMoreActivity.class);
	                    startActivity(i2more);
	                    break;
	                case 5://更多
	                    Intent i2main = new Intent(getApplicationContext(), MainActivity.class);
	                    startActivity(i2main);
	                    break;
                }
            }
        };
	}
	
	/**
	 * 请求数据失败
	 */
	protected void getDataError() {
		Tools.showToastLong(BaseActivity.this, "数据加载失败。");
	}
	
	/**
	 * 点击分类时的弹出窗口
	 * 
	 * @param clickView 被点击的组件
	 * @param groups 显示的list
	 * @param listener 点击后出发的事件
	 */
	protected void showPopupWindow(View clickView, List<String> groups, OnItemClickListener listener) {  
  	  
    	//获取屏幕的宽度和高度
    	WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
    	int screanWidth = windowManager.getDefaultDisplay().getWidth();
    	int screanHeight = windowManager.getDefaultDisplay().getHeight();
    	
        if (popupWindow == null) {  
            LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
            rankingLayout = layoutInflater.inflate(R.layout.listgroup_ranking, null);  
            listViewGroup = (ListView) rankingLayout.findViewById(R.id.ListViewGroup);  
  
            GroupAdapter groupAdapter = new GroupAdapter(this, groups);  
            listViewGroup.setAdapter(groupAdapter);  
            // 创建一个PopuWidow对象 ,高度为屏幕的7/8,宽度为3/4
            popupWindow = new PopupWindow(rankingLayout, screanWidth * 7 / 8, screanHeight * 3 / 4);
        }  
  
        // 使其聚集  
        popupWindow.setFocusable(true);  
        // 设置允许在外点击消失  
        popupWindow.setOutsideTouchable(true);
        popupWindow.setAnimationStyle(R.anim.popup_window);
        // 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景  
        popupWindow.setBackgroundDrawable(new BitmapDrawable()); 
        
        //弹出窗口的参照点
        LinearLayout title = (LinearLayout) this.findViewById(R.id.toppanel);
        //显示的位置为:屏幕的宽度的一半-PopupWindow的宽度的一半  
        popupWindow.showAsDropDown(title, (screanWidth - popupWindow.getWidth()) / 2, 5);  
  
        listViewGroup.setOnItemClickListener(listener);  
    }  
	
}

