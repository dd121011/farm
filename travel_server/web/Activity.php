<?php
header("Content-Type:text/html;charset=utf-8");
require_once "../src/util/Tools.class.php";

require_once "../src/vo/Activity.class.php";
require_once "../src/service/ServiceActivity.class.php";
require_once "../src/dao/ActivityDao.class.php";
require_once "../src/dao/impl/ActivityDaoImpl.class.php";
require_once "../src/dao/impl/AbstractDaoImpl.class.php";

isset($_POST['count']) ?$count = $_POST['count']:$count = 1;
echo Activity($count);

/**
 * @param unknown_type $count
 * @return string
 */
function Activity($count){
	$tools = new Tools();
	$service = new ServiceActivity($count);
	/* 获取活动数组*/
	$activity_array = $service->findActivity();
	$json_array = array();//数组
	$length = count($activity_array);
	for($i = 0 ; $i<$length; $i++ ){
		$activity_obj = $activity_array[$i];//取对象
		$activity_json = $tools->object2Json($activity_obj);//对象转json
		$json_array[$i] = $activity_json;//json入组
	}
	//将json数组转json
	$res = $tools->jsonArray2Json($json_array);
	/* 返回查询结果  */
	return $res;
}