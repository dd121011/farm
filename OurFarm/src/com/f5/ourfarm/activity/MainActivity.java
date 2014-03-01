package com.f5.ourfarm.activity;

import static com.f5.ourfarm.util.DbConstants.DATABASE_NAME;
import static com.f5.ourfarm.util.DbConstants.DATABASE_PATH;
import static com.f5.ourfarm.util.URLConstants.RECOMMEND_URL;
import static com.f5.ourfarm.util.URLConstants.VERSION_URL;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.baidu.location.LocationClient;
import com.f5.ourfarm.R;
import com.f5.ourfarm.model.Summary;
import com.f5.ourfarm.model.Version;
import com.f5.ourfarm.service.PullService;
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
public class MainActivity extends BaseActivity {
	// 监听附近下4个按钮
	OnClickListener lNearby = null;
	// 计划出行下的按钮
	OnClickListener lPlan = null;
	// 监听计划出行下经典路线
	OnClickListener lClassicLine = null;
	// 监听计划出行下按照区域查找
	OnClickListener lArea = null;
	// 监听活动促销
	OnClickListener lActiviy = null;

	// 监听普通查询输入框
	OnClickListener lCommonSearch = null;

	private static final String TAG = "首页";
	String errRecommendMsg = "获取推荐信息失败";
	// 最后一次按返回键的时间
	private long lastBackTime = 0;
	// 两次按键的间隔，单位ms
	private long TIME_DIFF = 2 * 1000;
	// 推荐明细信息
	private List<Summary> summaryList = null;
	private Version version = null;
	// 获取当前位置
	private LocationClient mLocClient;

	private int requestCode = 0;
	private int interval = Constants.PULL_INTERVAL;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 去除标题栏
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 准备listeners
		this.prepareListeners();
		setContentView(R.layout.activity_main);
		// 设置底部切换颜色
		ImageView ivHome = (ImageView) this
				.findViewById(R.id.ImageView_home_main);
		ivHome.setImageResource(R.drawable.home_home_active);
		// 设置listeners
		this.batchSetListeners();

		new Thread(new Runnable() {
			public void run() {
				// 判断数据库是否存在
				boolean dbExist = checkDataBase();
				if (dbExist) {
					Log.i("MainActivity", "db已存在！");
				} else {// 不存在就把raw里的数据库写入手机
					try {
						copyDataBase();
					} catch (IOException e) {
						throw new Error("Error copying database");
					}
				}
				// TODO 确认当期位置，这样在应用到主页后就开始进行定位，还是在点击附近下方的菜单时定位合理？
				initLocation();
				// 开启推送服务
				startAlarm(0, PullService.class);
			}

		}).start();
		//读取版本信息
		new Thread(runnable4version).start();
		// 从全局变量中获取推荐内容
		summaryList = ((OurfarmApp) getApplication()).recommentSummary;
		if (summaryList != null && summaryList.size() > 0) {
			showRecommend(summaryList);
		} else {
			new Thread(runnable4detail).start();
		}

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
	 * 判断数据库是否存在
	 * 
	 * @return false or true
	 */
	public boolean checkDataBase() {
		SQLiteDatabase checkDB = null;
		try {
			String databaseFilename = DATABASE_PATH + DATABASE_NAME;
			checkDB = SQLiteDatabase.openDatabase(databaseFilename, null,
					SQLiteDatabase.OPEN_READONLY);
		} catch (SQLiteException e) {
			Log.w("sqlite异常", "数据库" + DATABASE_NAME + "不存在!");
		} finally {
			if (checkDB != null) {
				checkDB.close();
			}
		}
		return checkDB != null ? true : false;
	}

	/**
	 * 复制数据库到手机指定文件夹下
	 * 
	 * @throws IOException
	 */
	public void copyDataBase() throws IOException {
		String databaseFilenames = DATABASE_PATH + DATABASE_NAME;
		File dir = new File(DATABASE_PATH);
		if (!dir.exists())// 判断文件夹是否存在，不存在就新建一个
			dir.mkdir();
		FileOutputStream os = null;
		try {
			os = new FileOutputStream(databaseFilenames);// 得到数据库文件的写入流
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		InputStream is = this.getResources().openRawResource(R.raw.ourfarm);// 得到数据库文件的数据流
		byte[] buffer = new byte[8192];
		int count = 0;
		try {
			while ((count = is.read(buffer)) > 0) {
				os.write(buffer, 0, count);
				os.flush();
			}
		} catch (IOException e) {
			// TODO 怎么处理异常?
		} finally {
			try {
				is.close();
				os.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 监听到事件后的动作
	 */
	private void prepareListeners() {
		// 普通查询输入框
		lCommonSearch = new OnClickListener() {
			public void onClick(View v) {
				if (!Tools.checkNetworkStatus(MainActivity.this)) {
					return;
				}
				Intent home2search = new Intent(MainActivity.this,
						SearchActivity.class);
				startActivity(home2search);
			}
		};
		// 附近下4个按钮，每个按钮一个标签，按钮被触发后根据标签判断如何处理
		lNearby = new OnClickListener() {
			public void onClick(View v) {
				if (!Tools.checkNetworkStatus(MainActivity.this)) {
					return;
				}

				Map<String, Object> localMap = ((OurfarmApp) getApplication()).localMap;

				// 初始化当前位置
				try {
					// 在进入首页的时候，已经进行了定位，如果定位不成功，就继续做定位操作
					if (!getLocationSuccess(localMap)) {

						Toast.makeText(MainActivity.this, "正在确认你的位置...",
								Toast.LENGTH_LONG).show();

						// 等待定位信息的获取,做6次位置请求
						int waint = 0;
						int waintCount = 6;
						for (; waint < waintCount; waint++) {
							initLocation();
							Object locTypeCode = localMap
									.get(Constants.LOC_TYPE_CODE);
							if (locTypeCode != null) {
								break;
							}
						}
						// 定位成功不成功
						if (waint == waintCount) {
							cannotGetLocShow();
							return;
						}
					}

				} catch (Exception e) {
					Log.d(TAG, "获取位置失败", e);
					cannotGetLocShow();
					return;
				}

				int tag = (Integer) v.getTag();
				if (tag == 4) {
					Intent home2farmproduce = new Intent(MainActivity.this,
							FarmProduceActivity.class);
					startActivity(home2farmproduce);
				} else {
					Intent home2play = new Intent(MainActivity.this,
							NearbyActivity.class);
					// 将服务器返回数据保存入Intent，确保数据可在activity间传递
					home2play.putExtra("nearbyType", tag);
					startActivity(home2play);
				}
			}
		};

		// 计划出行
		lPlan = new OnClickListener() {
			public void onClick(View v) {
				if (!Tools.checkNetworkStatus(MainActivity.this)) {
					return;
				}

				int tag = (Integer) v.getTag();
				Intent home2play = new Intent(MainActivity.this,
						PlanActivity.class);
				home2play.putExtra("planType", tag);
				home2play.putExtra("planName",
						String.valueOf(v.getTag(R.string.tag_first)));
				startActivity(home2play);
			}
		};

		// 进入经典路线
		lClassicLine = new OnClickListener() {
			public void onClick(View v) {
				if (!Tools.checkNetworkStatus(MainActivity.this)) {
					return;
				}
				Intent home2classicLine = new Intent(MainActivity.this,
						ClassicLineActivity.class);
				// TODO 获取经典路线，
				String res = "";
				home2classicLine.putExtra("res", res);
				startActivity(home2classicLine);
			}
		};
		// 进入按照区域查找
		lArea = new OnClickListener() {
			public void onClick(View v) {
				if (!Tools.checkNetworkStatus(MainActivity.this)) {
					return;
				}
				Intent home2area = new Intent(MainActivity.this,
						AreaActivity.class);
				startActivity(home2area);
			}
		};

		// 进入活动促销
		lActiviy = new OnClickListener() {
			public void onClick(View v) {
				if (!Tools.checkNetworkStatus(MainActivity.this)) {
					return;
				}
				Intent home2promotions = new Intent(MainActivity.this,
						PromotionsShowActivity.class);
				startActivity(home2promotions);
			}
		};
	}

	/**
	 * 绑定view和监听
	 */
	private void batchSetListeners() {
		// 绑定普通查询
		TextView search = (TextView) this
				.findViewById(R.id.common_search_input_click);
		search.setOnClickListener(lCommonSearch);

		// 绑定 附近下4个按钮，每个按钮设置一个标签，按钮被触发后根据标签判断如何处理
		// 景点
		ImageView iHomePlay = (ImageView) findViewById(R.id.ImageView_home_play);
		iHomePlay.setTag(1);
		iHomePlay.setOnClickListener(lNearby);
		// 农家乐
		ImageView iHomefood = (ImageView) findViewById(R.id.ImageView_home_food);
		iHomefood.setTag(2);
		iHomefood.setOnClickListener(lNearby);
		// 休闲山庄
		ImageView iHomehotel = (ImageView) findViewById(R.id.ImageView_home_hotel);
		iHomehotel.setTag(3);
		iHomehotel.setOnClickListener(lNearby);
		// 农产品
		ImageView iHomespherical = (ImageView) findViewById(R.id.ImageView_home_spherical);
		iHomespherical.setTag(4);
		iHomespherical.setOnClickListener(lNearby);

		// 点击计划出行下面的按钮
		// 度假休闲
		ImageView iHomeResort = (ImageView) findViewById(R.id.ImageView_home_resort);
		iHomeResort.setTag(20);
		iHomeResort.setTag(R.string.tag_first,
				iHomeResort.getContentDescription());
		iHomeResort.setOnClickListener(lPlan);
		// 乡村市集
		ImageView iHomeMarket = (ImageView) findViewById(R.id.ImageView_home_market);
		iHomeMarket.setTag(21);
		iHomeMarket.setTag(R.string.tag_first,
				iHomeMarket.getContentDescription());
		iHomeMarket.setOnClickListener(lPlan);
		// 亲子
		ImageView iHomeChild = (ImageView) findViewById(R.id.ImageView_home_parent_child);
		iHomeChild.setTag(7);
		iHomeChild.setTag(R.string.tag_first,
				iHomeChild.getContentDescription());
		iHomeChild.setOnClickListener(lPlan);
		// 采摘
		ImageView iHomePick = (ImageView) findViewById(R.id.ImageView_home_pick);
		iHomePick.setTag(4);
		iHomePick.setTag(R.string.tag_first, iHomePick.getContentDescription());
		iHomePick.setOnClickListener(lPlan);
		// 爬山
		ImageView iHomeMountain = (ImageView) findViewById(R.id.ImageView_home_mountain);
		iHomeMountain.setTag(2);
		iHomeMountain.setTag(R.string.tag_first,
				iHomeMountain.getContentDescription());
		iHomeMountain.setOnClickListener(lPlan);
		// 垂钓
		ImageView iHomeFishing = (ImageView) findViewById(R.id.ImageView_home_fishing);
		iHomeFishing.setTag(5);
		iHomeFishing.setTag(R.string.tag_first,
				iHomeFishing.getContentDescription());
		iHomeFishing.setOnClickListener(lPlan);

		// 绑定经典路线
		ImageView iclassicLine = (ImageView) this
				.findViewById(R.id.ImageView_home_drive_line);
		iclassicLine.setOnClickListener(lClassicLine);

		// 绑定按照区域查找
		ImageView ivArea = (ImageView) this
				.findViewById(R.id.ImageView_home_area_spots);
		ivArea.setOnClickListener(lArea);

		// 监听活动促销
		RelativeLayout rActiviy = (RelativeLayout) this
				.findViewById(R.id.Layout_home_promote_sales);
		rActiviy.setOnClickListener(lActiviy);

		// 底部面板4个按钮
		RelativeLayout rHome = (RelativeLayout) this
				.findViewById(R.id.Layout_home_panelBottom_home);
		rHome.setTag(1);
		rHome.setOnClickListener(lPanelBottom);

		RelativeLayout rCheckin = (RelativeLayout) this
				.findViewById(R.id.Layout_home_panelBottom_checkin);
		rCheckin.setTag(2);
		rCheckin.setOnClickListener(lPanelBottom);

		RelativeLayout rMy = (RelativeLayout) this
				.findViewById(R.id.Layout_home_panelBottom_my);
		rMy.setTag(3);
		rMy.setOnClickListener(lPanelBottom);

		RelativeLayout rMore = (RelativeLayout) this
				.findViewById(R.id.Layout_home_panelBottom_more);
		rMore.setTag(4);
		rMore.setOnClickListener(lPanelBottom);
	}

	/**
	 * 按返回键退出提醒
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			long now = new Date().getTime();
			if (now - lastBackTime < TIME_DIFF) {
				Log.d(TAG, "连续两次按下返回键，程序退出");

				// 退出应用，推荐内容清空
				((OurfarmApp) getApplication()).recommentSummary = null;
				return super.onKeyDown(keyCode, event);
			} else {
				lastBackTime = now;
				Log.d(TAG, "按下一次返回键，进行提示");
				// 再按一次退出农家乐
				Tools.showToastShort(MainActivity.this,
						Constants.CLICK_WILL_EXIT_PROJECT);
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 需要注意的问题：
	 * 
	 * 定位SDK必须注册GPS和网络的使用权限。 使用定位SDK请保证网络连接通畅（GPS定位方式不需要连网）。
	 * 我们强烈建议您设置自己的prodName，并保管好，这样方便我们为您提供更好的定位服务。 若需要返回的定位结果里包含地址信息，请保证网络连接。
	 * 定位SDK可以返回bd09、bd09ll、gcj02三种类型坐标，若需要将定位点的位置通过百度Android地图
	 * SDK进行地图展示，请返回bd09ll，将无偏差的叠加在百度地图上。
	 * 有的移动设备锁屏后为了省电会自动关闭网络连接，此时网络定位模式的定位失效。此外，锁屏后移动设备若进入cpu休眠，定时定位功能也失效。
	 * 若您需要实现在cpu休眠状态仍需定时定位，可以用alarmManager 实现1个cpu可叫醒的timer，定时请求定位。
	 */
	/**
	 * 初始化当前位置
	 * 
	 * @return
	 */
	private void initLocation() {
		mLocClient = ((OurfarmApp) getApplication()).mLocationClient;
		// 设置定位相关参数
		mLocClient.setLocOption(Tools.getLocationOption());

		if (mLocClient != null && mLocClient.isStarted()) {
			mLocClient.requestLocation();
		} else {
			Log.d("LocSDK3", "locClient is null or not started");
			mLocClient.start();
			mLocClient.requestLocation();
		}
	}

	// 请求推荐内容
	Runnable runnable4detail = new Runnable() {
		@Override
		public void run() {
			String res = "";
			try {
				// 请求首页显示的消息
				List<NameValuePair> param = new ArrayList<NameValuePair>();
				param.add(new BasicNameValuePair("type", String.valueOf("1")));
				param.add(new BasicNameValuePair("isused", String.valueOf("1")));

				res = HttpUtil.postUrl(RECOMMEND_URL, param);
			} catch (ClientProtocolException e1) {
				Log.e(TAG, errRecommendMsg, e1);
			} catch (IOException e1) {
				Log.e(TAG, errRecommendMsg, e1);
			}

			// TODO 将图片保存在本地。
			Message msg = new Message();
			Bundle data = new Bundle();
			data.putString("detail", res);
			msg.setData(data);
			MainActivity.this.handler4detail.sendMessage(msg);
		}
	};
	Handler handler4version = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Bundle data = msg.getData();
			String res4version = data.getString("version");
			try {
				Gson gson = new Gson();
				version = gson.fromJson(res4version, new TypeToken<Version>() {
				}.getType());
			} catch (JsonSyntaxException e) {
				/*
				 * TextView textView = (TextView)
				 * findViewById(R.id.TextView_home_recommend);
				 * textView.setText(errRecommendMsg);
				 */
				return;
			}
			if (!getVersion().equals(version.getVersion())) {
				showUpdateDialog();
			}
		}
	};

	// 请求版本信息
	Runnable runnable4version = new Runnable() {
		@Override
		public void run() {
			String res = "";
			try {
				// 请求首页显示的消息
				List<NameValuePair> param = new ArrayList<NameValuePair>();
				param.add(new BasicNameValuePair("type", String.valueOf("0")));
				res = HttpUtil.postUrl(VERSION_URL, param);
			} catch (ClientProtocolException e1) {
				Log.e(TAG, errRecommendMsg, e1);
			} catch (IOException e1) {
				Log.e(TAG, errRecommendMsg, e1);
			}
			Log.i("version", res);
			// TODO 将图片保存在本地。
			Message msg = new Message();
			Bundle data = new Bundle();
			data.putString("version", res);
			msg.setData(data);
			MainActivity.this.handler4version.sendMessage(msg);
		}
	};
	Handler handler4detail = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Bundle data = msg.getData();
			String res4detail = data.getString("detail");
			try {
				Gson gson = new Gson();
				summaryList = gson.fromJson(res4detail,
						new TypeToken<List<Summary>>() {
						}.getType());
			} catch (JsonSyntaxException e) {
				TextView textView = (TextView) findViewById(R.id.TextView_home_recommend);
				textView.setText(errRecommendMsg);
				return;
			}

			if (summaryList != null && summaryList.size() > 0) {
				// 推荐内容放到全局变量中
				((OurfarmApp) getApplication()).recommentSummary = summaryList;
				// 显示推荐内容
				showRecommend(summaryList);
			}

			Log.i(TAG, "推荐请求结果为-->" + res4detail);
		}
	};

	public void showUpdateDialog() {
		this.buildDialog().show();
	}

	public Dialog buildDialog() {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		// builder.setIcon(R.drawable.alert_dialog_icon);
		builder.setTitle("更新");
		builder.setCancelable(false);
		builder.setPositiveButton("更新", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

				// TODO Auto-generated method stub
				Uri uri = Uri.parse("market://details?id=com.f5.ourfarm");
				Intent it = new Intent(Intent.ACTION_VIEW, uri);
				MainActivity.this.startActivity(it);
			}

		});

		return builder.create();

	}

	private String getVersion() {
		try {
			PackageManager manager = this.getPackageManager();
			PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
			String version = info.versionName;
			return version;
		} catch (Exception e) {
			e.printStackTrace();
			return "error";
		}
	}

	/**
	 * 显示推荐内容
	 * 
	 * @param summaryList
	 *            推荐内容list
	 */
	private void showRecommend(List<Summary> summaryList) {
		// 交替显示推荐的内容
		ViewFlipper viewFlipper = (ViewFlipper) findViewById(R.id.flipper_scrollTitle);
		// 先清空原来推荐的内容
		viewFlipper.removeAllViews();

		for (Summary sum : summaryList) {
			// 设置推荐显示的内容和格式
			TextView textView = new TextView(MainActivity.this);
			textView.setText("特别推荐：  " + sum.getName());
			textView.setPadding(40, 10, 0, 10);
			textView.setTextColor(Color.parseColor("#666666"));
			textView.setTextSize(12f);
			LayoutParams lp = new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			viewFlipper.addView(textView, lp);

			// 点击推荐内容时，进入推荐页面
			textView.setOnClickListener(new NoticeTitleOnClickListener(sum));
		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 停掉
		if (mLocClient != null && mLocClient.isStarted()) {
			mLocClient.stop();
			mLocClient = null;
		}
	}

	/**
	 * 无法获取当前位置
	 */
	private void cannotGetLocShow() {
		Tools.showToastLong(MainActivity.this, Constants.CANNOT_GET_LOC);
	}

	/**
	 * 判断是否定位成功
	 * 
	 * @param localMap
	 * @return true：定位成功；false：定位失败
	 */
	private boolean getLocationSuccess(Map<String, Object> localMap) {

		try {
			long lastGetLocTime = (Long) localMap.get(Constants.LOC_GET_TIME);
			double lat = (Double) localMap.get(Constants.LOC_LAT);
			double lng = (Double) localMap.get(Constants.LOC_LNG);
			// TODO 要验证失败时的具体返回值
			if (lat <= 0 || lng <= 0) {
				return false;
			}

			// SimpleDateFormat simpleDateFormat = new
			// SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			// // TODO Auto-generated method stub
			// long t1 = System.currentTimeMillis();
			//
			// Date date1 = new Date(t1);
			// System.out.println(simpleDateFormat.format(date1));
			// Date date2 = new Date(lastGetLocTime);
			// System.out.println(simpleDateFormat.format(date2));

			// 判断当前时间和上次定位时间的差值，大于设定差值时，需要重新定位
			if (System.currentTimeMillis() - lastGetLocTime > Constants.LOC_INTERVAL_TIME) {
				Log.e(TAG, "获取位置时间超时，应重新请求位置。。。。");
				return false;
			}

			Log.d(TAG + " : 获取经纬度： ",
					String.valueOf(lat) + " " + String.valueOf(lng));
		} catch (Exception e) {
			Log.e(TAG, "获取当前位置失败,将重试");
			return false;
		}

		return true;
	}

	/**
	 * 点击推荐信息时候触发
	 * 
	 * @author lify
	 * 
	 */
	class NoticeTitleOnClickListener implements OnClickListener {
		private Summary summary;

		public NoticeTitleOnClickListener(Summary summary) {
			this.summary = summary;
		}

		public void onClick(View v) {
			if (!Tools.checkNetworkStatus(MainActivity.this)) {
				return;
			}
			Intent nearby2detail = new Intent(MainActivity.this,
					DetailActivity.class);
			// 将服务器返回数据保存入Intent，确保数据可在activity间传递
			nearby2detail.putExtra("destinationId", summary.getDestinationId());
			startActivity(nearby2detail);
		}

	}

	public ConnectivityManager getConnectivityManager() {
		return (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
	}

	public void startAlarm(int delayTime, Class<?> cls) {
		AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
		Intent intent = new Intent(getApplicationContext(), cls);
		PendingIntent pendingIntent = PendingIntent.getService(
				MainActivity.this, requestCode, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		long triggerAtTime = SystemClock.elapsedRealtime() + delayTime;
		manager.setRepeating(AlarmManager.ELAPSED_REALTIME, triggerAtTime,
				interval, pendingIntent);
	}

	public void stopAlarm(Class<?> cls) {
		AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
		Intent intent = new Intent(getApplicationContext(), cls);
		PendingIntent pendingIntent = PendingIntent.getService(
				MainActivity.this, requestCode, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		manager.cancel(pendingIntent);
	}
}
