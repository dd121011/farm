<?php
header("Content-Type:text/html;charset=utf-8");
//ini_set("max_execution_time", 600); // s 1 分钟

require_once "../src/util/Tools.class.php";
require_once "../src/service/ServiceNearby.class.php";
require_once "../src/vo/Summary.class.php";
require_once "../src/vo/Destination.class.php";
require_once "../src/dao/impl/DestinationDaoImpl.class.php";
require_once "../src/dao/impl/AbstractDaoImpl.class.php";

if (isset( $_POST['lat'])&&isset($_POST['lng'])){
	$lat =$_POST['lat'];
	$lng =$_POST['lng'];
	/* distance */
	isset($_POST['distance']) ?$distance = $_POST['distance']:$distance = 100;
	/* count */
	(isset($_POST['count']))?$count = $_POST['count']:$count = 1;
	/* type */
	(isset($_POST['type']))?$type = $_POST['type']:$type = 1;
	/* return nearby*/
	echo Nearby($lat ,$lng, $distance, $count,$type);

}else{
	/* for debug */
	//echo  Nearby(39.6692 ,115.448, 100, 20,4);
	/* TODO */
	echo "error  lat/lng is null !";
}

function Nearby($lat ,$lng, $distance, $count,$type){
	//农家乐都提供住宿
	//	if($type==3){$type =2;}else{$type = $type;}
	/* 通过service获取查询结果  */
	$service = new ServiceNearby($lat ,$lng, $distance, $count,$type);
	$Summary_array =$service->findNearby() ;
	
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

?>