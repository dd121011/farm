package com.f5.ourfarm.util;

public class Constants {
	// APP名称
	public static final String APP_NAME = "郊游客";
	
	// 进度条显示内容
	public static final String PROGRESS_TITLE = "郊游客";
	public static final String PROGRESS_MESSAGE = "加载中...";
	
	public static final String CLICK_WILL_EXIT_PROJECT = "再按一次退出郊游客";
	public static final String NETWORK_NOT_AVAILABLE = "目前网络连接不可用";
	public static final String FUNCTION_UNREALIZED = "该功能暂未实现";
	public static final String DATA_SYNC_SUCCESS = "数据同步成功";
	public static final String CANNOT_GET_LOC = "无法获取您的具体位置，请重试。或先使用其他功能。";
	public static final String REFERESH_FAILD_GET_LOC = "无法获取您的具体位置。";
	public static final String ADD_FAVORITE= "已收藏！";
	public static final String ADD_THIS_TRIP= "已加入本次行程！";
	public static final String NONE_FAVORITE= "你还没有收藏内容！";
	public static final String NONE_THIS_TRIP= "你还没有添加内容到本次行程！";
	public static final String MORE_CLEAN_DATA= "缓存已清除";
	public static final String MORE_UPDATE_DATA= "本地数据已更新";
	
	//SharedPreferences存储当期位置key值
	public static final String LOCATION_INFOS = "LOCATION_INFOS";
	public static final String LOCATION_LNG = "LOCATION_LNG";//经度
	public static final String LOCATION_LAT = "LOCATION_LAT";//纬度
	
	public static final String DETAIL_PICTURE_SHOW = "DETAIL_PICTURE_SHOW";//显示商家图片
	public static final String NO_DETAIL_PICTURE = "无其他图片";//显示商家图片
	
	//百度地图key
	public static final String BAIDU_MAP_KEY = "CB5EFB673940B9B41260FFCE47614F4AF9B6F4A6";
	
	//定位信息存储MapKey
	public static final String LOC_TYPE_CODE = "locTypeCode";//返回code
	public static final String LOC_LAT = "locLat";//纬度
	public static final String LOC_LNG = "locLng";//经度
	public static final String LOC_ADDR = "locAddr";//地址
	public static final String LOC_GET_TIME = "locTime";//获取位置时的时间
	public static final long LOC_INTERVAL_TIME = 300000;//判断上次定位时间，单位为毫秒，一分钟=60000毫秒
	
	//地图显示景点
	public static final String MAP_SHOW_SPOT = "mapShowSpot";
	//周边农家乐
	public static final String AROUND_FARM = "aroundFarm";
	//更多评论
	public static final String MORE_COMMENT = "moreComment";
	//根据选择的距离来确定地图的级别
	public static final String MAP_SHOW_DISTANCE = "mapShowDistance";
	//景点ID
	public static final String DESTINATION_ID = "destinationId";
	
	/**第一次加载导航*/
	public static final String PREFERENCES_NAME = "navigation";
	public static final String FIRST_RUN = "FirstRunFarm";
	
	//微博信息
	public static final String WEIBO_TEXT_COMMON = "@郊游客微博 我在郊游客发现一个很好玩的地方，快来看看吧：";
	
	//请求推送消息间隔,单位：毫秒
	public static final int PULL_INTERVAL = 1200000;
	//推送消息的Id
	public static final String SAVE_PUSH_MESSAGE = "save_push_message";
	public static final String PUSH_MESSAGE_ID = "push_message_id";
	//第三方接入
	public static final String SINA_WEIBO_TOKEN = "weibo_token";
	public static final String SINA_WEIBO_TIME = "weibo_expiresTime";
	public static final String SINA_WEIBO_BAND = "weibo_isBand";
	public static final String QQ_WEIBO_TOKEN = "qq_token";
	public static final String QQ_WEIBO_TIME = "qq_expiresTime";
	public static final String QQ_WEIBO_BAND = "qq_isBand";
}
