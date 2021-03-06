<?php
header("Content-Type:text/html;charset=utf-8");
//ini_set("max_execution_time", 600); // s 1 分钟

require_once "../src/util/Tools.class.php";
require_once "../src/service/ServiceMessage.class.php";
require_once "../src/dao/RecommendDao.class.php";
require_once "../src/dao/impl/RecommendDaoImpl.class.php";
require_once "../src/dao/impl/AbstractDaoImpl.class.php";

isset($_POST['type']) ?$type = $_POST['type']:$type = 2;
isset($_POST['isused']) ?$isused = $_POST['isused']:$isused = 1;
echo Message($type,$isused);

function Message( $type,$isused){
	/* 通过service获取查询结果  */
	$service = new ServiceMessage( $type,$isused);
	$Summary_array =$service->findMessage() ;

	$tools = new Tools();//
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