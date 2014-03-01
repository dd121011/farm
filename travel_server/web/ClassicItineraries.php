<?php
header("Content-Type:text/html;charset=utf-8");
ini_set("max_execution_time", 600); // s 1 分钟

require_once "../src/util/Tools.class.php";
require_once "../src/service/ServiceClassicItineraries.class.php";
require_once "../src/dao/LineDao.class.php";
require_once "../src/dao/ItineraryDao.class.php";
require_once "../src/dao/impl/LineDaoImpl.class.php";
require_once "../src/dao/impl/ItineraryDaoImpl.class.php";
require_once "../src/dao/impl/AbstractDaoImpl.class.php";

isset($_POST['count']) ?$count = $_POST['count']:$count = 1;
echo ClassicItineraries($count);

function ClassicItineraries( $count){
	/* 通过service获取查询结果  */
	$service = new ServiceClassicItineraries( $count);
	$Itinerary_array =$service->findClassic();

	$tools = new Tools();//
	$json_array = array();//数组，内容为summary的json
	$length = count($Itinerary_array);
	for($i = 0 ; $i<$length; $i++ ){
		$Itinerary_obj = $Itinerary_array[$i];//取对象
		
		$line_obj_array = $Itinerary_obj->getLine();//取line的对象数组
		$line_obj_array_length = count($line_obj_array);//line的对象数组的长度
		for($j = 0 ; $j<$line_obj_array_length; $j++){
			$line_json_array [$j] = $tools->object2json($line_obj_array[$j]);//将line的对象数组转换为json数组
		}
		$line = $tools->jsonArray2Json($line_json_array);
		$Itinerary_obj->setLine($line);
		$Itinerary_json = $tools->object2Json($Itinerary_obj);//对象转json
		//去除对象数组中多余的“”
		{
			//replace "line":"[{"  to   "line":[{"
			$Itinerary_json = str_replace("\"line\":\"[{\"" , "\"line\":[{\"" , $Itinerary_json); 
			// replace "}]" to "}]}    replace  ]}]" to ]}]
			$Itinerary_json = str_replace("]}]\"", "]}]",$Itinerary_json); 
		}
		$json_array[$i] = $Itinerary_json;//json入组
	}
	//将json数组转json
	$res = $tools->jsonArray2Json($json_array);
	/* 返回查询结果  */
	return $res;
}