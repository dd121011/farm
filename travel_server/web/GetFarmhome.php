<?php
header("Content-Type:text/html;charset=utf-8");
ini_set("max_execution_time", 600); // s 1 分钟

require_once "../src/util/Tools.class.php";
require_once "../src/service/ServiceDetails.class.php";
require_once "../src/vo/Summary.class.php";
require_once "../src/vo/Destination.class.php";
require_once "../src/vo/View2farmhome.class.php";
require_once "../src/dao/DestinationDao.class.php";
require_once "../src/dao/impl/DestinationDaoImpl.class.php";
require_once "../src/dao/impl/LabelsDaoImpl.class.php";
require_once "../src/dao/impl/PictureDaoImpl.class.php";
require_once "../src/dao/impl/AbstractDaoImpl.class.php";



if (isset( $_POST['view_id'])){
	$viewId =$_POST['view_id'];
	isset($_POST['count']) ?$count = $_POST['count']:$count = 1;
	/* return details*/
	echo getFarmhome($viewId,$count);
}else{
	/* for debug */
	echo getFarmhome(1,1);
	/* TODO */
	echo "error  destinationId is null !";
}

function getFarmhome($viewId,$count){
	$tools = new Tools();

	$service = new ServiceDetails($viewId,$count);
	//get obj
	$farmhome_array = $service->getFarmhomeById($viewId,$count);
	$tools = new Tools();//
	$json_array = array();//数组，内容为summary的json
	$length = count($farmhome_array);
	for($i = 0 ; $i<$length; $i++ ){
		$sumary_obj = $farmhome_array[$i];//取对象
		$summary_json = $tools->object2Json($sumary_obj);//对象转json
		$json_array[$i] = $summary_json;//json入组
	}
	//将json数组转json
	$res = $tools->jsonArray2Json($json_array);
	/* 返回查询结果  */
	return $res;
}