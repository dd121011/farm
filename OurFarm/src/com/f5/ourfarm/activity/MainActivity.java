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
	// ����������4����ť
	OnClickListener lNearby = null;
	// �ƻ������µİ�ť
	OnClickListener lPlan = null;
	// �����ƻ������¾���·��
	OnClickListener lClassicLine = null;
	// �����ƻ������°����������
	OnClickListener lArea = null;
	// ���������
	OnClickListener lActiviy = null;

	// ������ͨ��ѯ�����
	OnClickListener lCommonSearch = null;

	private static final String TAG = "��ҳ";
	String errRecommendMsg = "��ȡ�Ƽ���Ϣʧ��";
	// ���һ�ΰ����ؼ���ʱ��
	private long lastBackTime = 0;
	// ���ΰ����ļ������λms
	private long TIME_DIFF = 2 * 1000;
	// �Ƽ���ϸ��Ϣ
	private List<Summary> summaryList = null;
	private Version version = null;
	// ��ȡ��ǰλ��
	private LocationClient mLocClient;

	private int requestCode = 0;
	private int interval = Constants.PULL_INTERVAL;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// ȥ��������
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		// ׼��listeners
		this.prepareListeners();
		setContentView(R.layout.activity_main);
		// ���õײ��л���ɫ
		ImageView ivHome = (ImageView) this
				.findViewById(R.id.ImageView_home_main);
		ivHome.setImageResource(R.drawable.home_home_active);
		// ����listeners
		this.batchSetListeners();

		new Thread(new Runnable() {
			public void run() {
				// �ж����ݿ��Ƿ����
				boolean dbExist = checkDataBase();
				if (dbExist) {
					Log.i("MainActivity", "db�Ѵ��ڣ�");
				} else {// �����ھͰ�raw������ݿ�д���ֻ�
					try {
						copyDataBase();
					} catch (IOException e) {
						throw new Error("Error copying database");
					}
				}
				// TODO ȷ�ϵ���λ�ã�������Ӧ�õ���ҳ��Ϳ�ʼ���ж�λ�������ڵ�������·��Ĳ˵�ʱ��λ����
				initLocation();
				// �������ͷ���
				startAlarm(0, PullService.class);
			}

		}).start();
		//��ȡ�汾��Ϣ
		new Thread(runnable4version).start();
		// ��ȫ�ֱ����л�ȡ�Ƽ�����
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
	 * �ж����ݿ��Ƿ����
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
			Log.w("sqlite�쳣", "���ݿ�" + DATABASE_NAME + "������!");
		} finally {
			if (checkDB != null) {
				checkDB.close();
			}
		}
		return checkDB != null ? true : false;
	}

	/**
	 * �������ݿ⵽�ֻ�ָ���ļ�����
	 * 
	 * @throws IOException
	 */
	public void copyDataBase() throws IOException {
		String databaseFilenames = DATABASE_PATH + DATABASE_NAME;
		File dir = new File(DATABASE_PATH);
		if (!dir.exists())// �ж��ļ����Ƿ���ڣ������ھ��½�һ��
			dir.mkdir();
		FileOutputStream os = null;
		try {
			os = new FileOutputStream(databaseFilenames);// �õ����ݿ��ļ���д����
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		InputStream is = this.getResources().openRawResource(R.raw.ourfarm);// �õ����ݿ��ļ���������
		byte[] buffer = new byte[8192];
		int count = 0;
		try {
			while ((count = is.read(buffer)) > 0) {
				os.write(buffer, 0, count);
				os.flush();
			}
		} catch (IOException e) {
			// TODO ��ô�����쳣?
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
	 * �������¼���Ķ���
	 */
	private void prepareListeners() {
		// ��ͨ��ѯ�����
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
		// ������4����ť��ÿ����ťһ����ǩ����ť����������ݱ�ǩ�ж���δ���
		lNearby = new OnClickListener() {
			public void onClick(View v) {
				if (!Tools.checkNetworkStatus(MainActivity.this)) {
					return;
				}

				Map<String, Object> localMap = ((OurfarmApp) getApplication()).localMap;

				// ��ʼ����ǰλ��
				try {
					// �ڽ�����ҳ��ʱ���Ѿ������˶�λ�������λ���ɹ����ͼ�������λ����
					if (!getLocationSuccess(localMap)) {

						Toast.makeText(MainActivity.this, "����ȷ�����λ��...",
								Toast.LENGTH_LONG).show();

						// �ȴ���λ��Ϣ�Ļ�ȡ,��6��λ������
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
						// ��λ�ɹ����ɹ�
						if (waint == waintCount) {
							cannotGetLocShow();
							return;
						}
					}

				} catch (Exception e) {
					Log.d(TAG, "��ȡλ��ʧ��", e);
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
					// ���������������ݱ�����Intent��ȷ�����ݿ���activity�䴫��
					home2play.putExtra("nearbyType", tag);
					startActivity(home2play);
				}
			}
		};

		// �ƻ�����
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

		// ���뾭��·��
		lClassicLine = new OnClickListener() {
			public void onClick(View v) {
				if (!Tools.checkNetworkStatus(MainActivity.this)) {
					return;
				}
				Intent home2classicLine = new Intent(MainActivity.this,
						ClassicLineActivity.class);
				// TODO ��ȡ����·�ߣ�
				String res = "";
				home2classicLine.putExtra("res", res);
				startActivity(home2classicLine);
			}
		};
		// ���밴���������
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

		// ��������
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
	 * ��view�ͼ���
	 */
	private void batchSetListeners() {
		// ����ͨ��ѯ
		TextView search = (TextView) this
				.findViewById(R.id.common_search_input_click);
		search.setOnClickListener(lCommonSearch);

		// �� ������4����ť��ÿ����ť����һ����ǩ����ť����������ݱ�ǩ�ж���δ���
		// ����
		ImageView iHomePlay = (ImageView) findViewById(R.id.ImageView_home_play);
		iHomePlay.setTag(1);
		iHomePlay.setOnClickListener(lNearby);
		// ũ����
		ImageView iHomefood = (ImageView) findViewById(R.id.ImageView_home_food);
		iHomefood.setTag(2);
		iHomefood.setOnClickListener(lNearby);
		// ����ɽׯ
		ImageView iHomehotel = (ImageView) findViewById(R.id.ImageView_home_hotel);
		iHomehotel.setTag(3);
		iHomehotel.setOnClickListener(lNearby);
		// ũ��Ʒ
		ImageView iHomespherical = (ImageView) findViewById(R.id.ImageView_home_spherical);
		iHomespherical.setTag(4);
		iHomespherical.setOnClickListener(lNearby);

		// ����ƻ���������İ�ť
		// �ȼ�����
		ImageView iHomeResort = (ImageView) findViewById(R.id.ImageView_home_resort);
		iHomeResort.setTag(20);
		iHomeResort.setTag(R.string.tag_first,
				iHomeResort.getContentDescription());
		iHomeResort.setOnClickListener(lPlan);
		// ����м�
		ImageView iHomeMarket = (ImageView) findViewById(R.id.ImageView_home_market);
		iHomeMarket.setTag(21);
		iHomeMarket.setTag(R.string.tag_first,
				iHomeMarket.getContentDescription());
		iHomeMarket.setOnClickListener(lPlan);
		// ����
		ImageView iHomeChild = (ImageView) findViewById(R.id.ImageView_home_parent_child);
		iHomeChild.setTag(7);
		iHomeChild.setTag(R.string.tag_first,
				iHomeChild.getContentDescription());
		iHomeChild.setOnClickListener(lPlan);
		// ��ժ
		ImageView iHomePick = (ImageView) findViewById(R.id.ImageView_home_pick);
		iHomePick.setTag(4);
		iHomePick.setTag(R.string.tag_first, iHomePick.getContentDescription());
		iHomePick.setOnClickListener(lPlan);
		// ��ɽ
		ImageView iHomeMountain = (ImageView) findViewById(R.id.ImageView_home_mountain);
		iHomeMountain.setTag(2);
		iHomeMountain.setTag(R.string.tag_first,
				iHomeMountain.getContentDescription());
		iHomeMountain.setOnClickListener(lPlan);
		// ����
		ImageView iHomeFishing = (ImageView) findViewById(R.id.ImageView_home_fishing);
		iHomeFishing.setTag(5);
		iHomeFishing.setTag(R.string.tag_first,
				iHomeFishing.getContentDescription());
		iHomeFishing.setOnClickListener(lPlan);

		// �󶨾���·��
		ImageView iclassicLine = (ImageView) this
				.findViewById(R.id.ImageView_home_drive_line);
		iclassicLine.setOnClickListener(lClassicLine);

		// �󶨰����������
		ImageView ivArea = (ImageView) this
				.findViewById(R.id.ImageView_home_area_spots);
		ivArea.setOnClickListener(lArea);

		// ���������
		RelativeLayout rActiviy = (RelativeLayout) this
				.findViewById(R.id.Layout_home_promote_sales);
		rActiviy.setOnClickListener(lActiviy);

		// �ײ����4����ť
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
	 * �����ؼ��˳�����
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			long now = new Date().getTime();
			if (now - lastBackTime < TIME_DIFF) {
				Log.d(TAG, "�������ΰ��·��ؼ��������˳�");

				// �˳�Ӧ�ã��Ƽ��������
				((OurfarmApp) getApplication()).recommentSummary = null;
				return super.onKeyDown(keyCode, event);
			} else {
				lastBackTime = now;
				Log.d(TAG, "����һ�η��ؼ���������ʾ");
				// �ٰ�һ���˳�ũ����
				Tools.showToastShort(MainActivity.this,
						Constants.CLICK_WILL_EXIT_PROJECT);
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * ��Ҫע������⣺
	 * 
	 * ��λSDK����ע��GPS�������ʹ��Ȩ�ޡ� ʹ�ö�λSDK�뱣֤��������ͨ����GPS��λ��ʽ����Ҫ��������
	 * ����ǿ�ҽ����������Լ���prodName�������ܺã�������������Ϊ���ṩ���õĶ�λ���� ����Ҫ���صĶ�λ����������ַ��Ϣ���뱣֤�������ӡ�
	 * ��λSDK���Է���bd09��bd09ll��gcj02�����������꣬����Ҫ����λ���λ��ͨ���ٶ�Android��ͼ
	 * SDK���е�ͼչʾ���뷵��bd09ll������ƫ��ĵ����ڰٶȵ�ͼ�ϡ�
	 * �е��ƶ��豸������Ϊ��ʡ����Զ��ر��������ӣ���ʱ���綨λģʽ�Ķ�λʧЧ�����⣬�������ƶ��豸������cpu���ߣ���ʱ��λ����ҲʧЧ��
	 * ������Ҫʵ����cpu����״̬���趨ʱ��λ��������alarmManager ʵ��1��cpu�ɽ��ѵ�timer����ʱ����λ��
	 */
	/**
	 * ��ʼ����ǰλ��
	 * 
	 * @return
	 */
	private void initLocation() {
		mLocClient = ((OurfarmApp) getApplication()).mLocationClient;
		// ���ö�λ��ز���
		mLocClient.setLocOption(Tools.getLocationOption());

		if (mLocClient != null && mLocClient.isStarted()) {
			mLocClient.requestLocation();
		} else {
			Log.d("LocSDK3", "locClient is null or not started");
			mLocClient.start();
			mLocClient.requestLocation();
		}
	}

	// �����Ƽ�����
	Runnable runnable4detail = new Runnable() {
		@Override
		public void run() {
			String res = "";
			try {
				// ������ҳ��ʾ����Ϣ
				List<NameValuePair> param = new ArrayList<NameValuePair>();
				param.add(new BasicNameValuePair("type", String.valueOf("1")));
				param.add(new BasicNameValuePair("isused", String.valueOf("1")));

				res = HttpUtil.postUrl(RECOMMEND_URL, param);
			} catch (ClientProtocolException e1) {
				Log.e(TAG, errRecommendMsg, e1);
			} catch (IOException e1) {
				Log.e(TAG, errRecommendMsg, e1);
			}

			// TODO ��ͼƬ�����ڱ��ء�
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

	// ����汾��Ϣ
	Runnable runnable4version = new Runnable() {
		@Override
		public void run() {
			String res = "";
			try {
				// ������ҳ��ʾ����Ϣ
				List<NameValuePair> param = new ArrayList<NameValuePair>();
				param.add(new BasicNameValuePair("type", String.valueOf("0")));
				res = HttpUtil.postUrl(VERSION_URL, param);
			} catch (ClientProtocolException e1) {
				Log.e(TAG, errRecommendMsg, e1);
			} catch (IOException e1) {
				Log.e(TAG, errRecommendMsg, e1);
			}
			Log.i("version", res);
			// TODO ��ͼƬ�����ڱ��ء�
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
				// �Ƽ����ݷŵ�ȫ�ֱ�����
				((OurfarmApp) getApplication()).recommentSummary = summaryList;
				// ��ʾ�Ƽ�����
				showRecommend(summaryList);
			}

			Log.i(TAG, "�Ƽ�������Ϊ-->" + res4detail);
		}
	};

	public void showUpdateDialog() {
		this.buildDialog().show();
	}

	public Dialog buildDialog() {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		// builder.setIcon(R.drawable.alert_dialog_icon);
		builder.setTitle("����");
		builder.setCancelable(false);
		builder.setPositiveButton("����", new DialogInterface.OnClickListener() {

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
	 * ��ʾ�Ƽ�����
	 * 
	 * @param summaryList
	 *            �Ƽ�����list
	 */
	private void showRecommend(List<Summary> summaryList) {
		// ������ʾ�Ƽ�������
		ViewFlipper viewFlipper = (ViewFlipper) findViewById(R.id.flipper_scrollTitle);
		// �����ԭ���Ƽ�������
		viewFlipper.removeAllViews();

		for (Summary sum : summaryList) {
			// �����Ƽ���ʾ�����ݺ͸�ʽ
			TextView textView = new TextView(MainActivity.this);
			textView.setText("�ر��Ƽ���  " + sum.getName());
			textView.setPadding(40, 10, 0, 10);
			textView.setTextColor(Color.parseColor("#666666"));
			textView.setTextSize(12f);
			LayoutParams lp = new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			viewFlipper.addView(textView, lp);

			// ����Ƽ�����ʱ�������Ƽ�ҳ��
			textView.setOnClickListener(new NoticeTitleOnClickListener(sum));
		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// ͣ��
		if (mLocClient != null && mLocClient.isStarted()) {
			mLocClient.stop();
			mLocClient = null;
		}
	}

	/**
	 * �޷���ȡ��ǰλ��
	 */
	private void cannotGetLocShow() {
		Tools.showToastLong(MainActivity.this, Constants.CANNOT_GET_LOC);
	}

	/**
	 * �ж��Ƿ�λ�ɹ�
	 * 
	 * @param localMap
	 * @return true����λ�ɹ���false����λʧ��
	 */
	private boolean getLocationSuccess(Map<String, Object> localMap) {

		try {
			long lastGetLocTime = (Long) localMap.get(Constants.LOC_GET_TIME);
			double lat = (Double) localMap.get(Constants.LOC_LAT);
			double lng = (Double) localMap.get(Constants.LOC_LNG);
			// TODO Ҫ��֤ʧ��ʱ�ľ��巵��ֵ
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

			// �жϵ�ǰʱ����ϴζ�λʱ��Ĳ�ֵ�������趨��ֵʱ����Ҫ���¶�λ
			if (System.currentTimeMillis() - lastGetLocTime > Constants.LOC_INTERVAL_TIME) {
				Log.e(TAG, "��ȡλ��ʱ�䳬ʱ��Ӧ��������λ�á�������");
				return false;
			}

			Log.d(TAG + " : ��ȡ��γ�ȣ� ",
					String.valueOf(lat) + " " + String.valueOf(lng));
		} catch (Exception e) {
			Log.e(TAG, "��ȡ��ǰλ��ʧ��,������");
			return false;
		}

		return true;
	}

	/**
	 * ����Ƽ���Ϣʱ�򴥷�
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
			// ���������������ݱ�����Intent��ȷ�����ݿ���activity�䴫��
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
