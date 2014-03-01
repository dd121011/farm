package com.f5.ourfarm.util;

/**
 * 请求URL常量类
 * 
 * @author lify
 *
 */
public class URLConstants {
	//服务器地址
    //private static String ServerUrl = "http://172.20.50.110/travel_server";//本地
	private static String ServerUrl = "http://framehomedemo.sinaapp.com";//sina sae
	//附近景点请求URL
    public static final String NEARBY_URL = ServerUrl + "/web/Nearby.php";
    //景点详细页面
    public static final String NEARBY_DETAIL_URL = ServerUrl + "/web/Details.php";
    //景点周边农家乐
    public static final String GET_AROUND_FARM_HOME = ServerUrl + "/web/GetFarmhome.php";
    //增加评论
    public static final String ADD_COMMENT = ServerUrl + "/web/Comment.php";
    //获取评论
    public static final String GET_COMMENT = ServerUrl + "/web/GetComment.php";
    //获取活动
    public static final String GET_ACTIVITY = ServerUrl + "/web/Activity.php";
    //获取排行
    public static final String GET_TOP = ServerUrl + "/web/Top.php";
    //首页推荐页面
    public static final String RECOMMEND_URL = ServerUrl + "/web/Recommend.php";
    //推送消息
    public static final String PUSH_MESSAGE_URL = ServerUrl + "/web/Message.php";
    //经典内容请求URL
    public static final String CLASSIC_URL = ServerUrl + "/web/Classic.php";
    //普通查询请求URL
    public static final String COMMON_SEARCH_URL = ServerUrl + "/web/Search.php";
    //计划
    public static final String PLAN_URL = ServerUrl + "/web/Plans.php";
    
    //经典路线请求URL
    public static final String CLASSIC_ITINERARIES_URL = ServerUrl + "/web/ClassicItineraries.php";
    //经典高级查找请求URL
    public static final String FIND_URL = ServerUrl + "/web/AccurateFind.php";
    //查询热度请求URL
    public static final String HOTTOP_URL = ServerUrl + "/web/HotTop.php";
    //活动请求URL
    public static final String ACTIVITY_URL = ServerUrl + "/web/Activity.php";
    //活动请求URL
    public static final String PROMOTIONS_URL = ServerUrl + "/web/Promotions.php";
    //反馈请求URL
    public static final String FEEDBACK_URL = ServerUrl + "/web/Feedback.php";
    //版本请求URL
    public static final String VERSION_URL = ServerUrl + "/web/GetVersion.php";
}
