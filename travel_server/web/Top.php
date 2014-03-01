<?php
header("Content-Type:text/html;charset=utf-8");
//ini_set("max_execution_time", 600); // s 1 分钟

require_once "../src/util/Tools.class.php";
require_once "../src/service/ServiceTop.class.php";
require_once "../src/vo/Summary.class.php";
require_once "../src/vo/Destination.class.php";
require_once "../src/dao/impl/DestinationDaoImpl.class.php";
require_once "../src/dao/impl/AbstractDaoImpl.class.php";

if(isset($_POST['type']) && isset($_POST['type_value'])  && isset($_POST['count']) ){
	isset($_POST['type']) ?$type = $_POST['type']:$type = 1;
	isset($_POST['type_value']) ?$type_value = $_POST['type_value']:$type_value = 1;
	isset($_POST['count']) ?$count = $_POST['count']:$count = 10;
	echo Top($type,$type_value,$count);
}else{
	echo Top(1,4,10)."this is test!";
}



function Top( $type,$type_value,$count){
	/* 通过service获取查询结果  */
	$service = new ServiceTop( $type,$type_value,$count);
	$Summary_array =$service->findTop() ;

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