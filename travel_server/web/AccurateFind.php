<?php
header("Content-Type:text/html;charset=utf-8");
ini_set("max_execution_time", 600); // s 1 分钟

require_once "../src/util/Tools.class.php";
require_once "../src/service/ServiceAccurateFind.class.php";
require_once "../src/vo/Summary.class.php";
require_once "../src/vo/Destination.class.php";
require_once "../src/dao/impl/DestinationDaoImpl.class.php";
require_once "../src/dao/impl/AbstractDaoImpl.class.php";


isset($_POST['regionCode']) ?$regionCode = $_POST['regionCode']:$regionCode = 0;//默认为0，显示所有区域内容
isset($_POST['classicFlag']) ?$classicFlag = $_POST['classicFlag']:$classicFlag = 0;// 默认为0，显示非经典内容
isset($_POST['labels']) ? $labels = $_POST['labels']:$labels = 0;//默认为0，不过滤，显示所有内容
isset($_POST['count']) ? $count = $_POST['count']:$count = 1;
//for debug
echo AccurateFind("110108","","",1);

//echo AccurateFind($regionCode,$classicFlag,$labels,$count);


/**
 * @param unknown_type $regionCode
 * @param unknown_type $classicFlag
 * @param unknown_type $labels
 * @param unknown_type $count
 * @return string
 */
function  AccurateFind($regionCode,$classicFlag,$labels,$count){
	$tools = new Tools();
	$service = new ServiceAccurateFind($regionCode,$classicFlag,$labels,$count);
	/* 获取活动数组*/
	$Summary_array = $service->accurateFind();
	$json_array = array();//数组，内容为summary的json
	$length = count($Summary_array);
	for($i = 0 ; $i<$length; $i++ ){
		$sumary_obj = $Summary_array[$i];//取对象
		$summary_json = $tools->object2Json($sumary_obj);//对象转json
		$json_array[$i] = $summary_json;//json入组
	}
	//将json数组转json
	$res = $tools->jsonArray2Json($json_array);
	/* 返回查询结果  */
	return $res;
}