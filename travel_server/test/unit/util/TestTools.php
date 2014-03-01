<?php
ini_set("max_execution_time", 600); // s 10 分钟
require_once "../../../src/vo/Summary.class.php";
require_once "../../../src/vo/Destination.class.php";
require_once "../../../src/dao/DestinationDao.class.php";
require_once "../../../src/util/Tools.class.php";


$destination_id = 10002244;
 $name = "十三陵";
 $lat = 12.35414;
 $lng = 132.25414;
 $address = "昌平区xxx路22号";
 $pic = "http://www.beijingnongjiayuan.com/uploads/allimg/110517/1-11051G63U03L.jpg";
 $tel = "101-8414745";
 $phone = "132547887442";
 $hot = 4.2514;
 $price = 25;
 $price_info = "门票“25RMB";
 $score = 4.2143;
 $distance = 2.3548;
 $characteristic = "爬山&风景&名胜古迹";
 
$sumary_obj = new Summary($destination_id, $name, $lat, $lng, $address, $pic, $tel, $phone, $hot, $price, $price_info, $score, $distance,$characteristic);

$tools = new Tools();
$array = $tools->objec2Array($sumary_obj);
echo "objec2Array :"."<br \>";
print_r($array);
echo "<br \>";

$json = $tools->object2Json($sumary_obj);
echo "object2Json :"."<br \>";
print_r($json);
echo "<br \>";

$otherContact= "this is otherContact";
$car= "";
$bus= "";
$bike= "";
$classicFlag= "";
$regionCode= "";
$region= "";
$mapPic= "";
$preferentialInfo= "";
$label= "";
$pics = "";

$introduction = "这是景点介绍！";
$Destination_obj = new Destination($sumary_obj, $introduction,$otherContact, $car, $bus, $bike,
			$classicFlag, $regionCode, $region, $mapPic,
			$preferentialInfo, $label, $pics);

$array = $tools->objec2Array($Destination_obj);
echo "Destination_obj objec2Array :"."<br \>";
print_r($array);
echo "<br \>";

$json = $tools->object2Json($Destination_obj);
echo "introduction object2Json :"."<br \>";
print_r($json);
echo "<br \>";

// class BigObject{
// 	var $destination;
// 	function __construct($destination){
// 		$this->destination = $destination;
// 	}
// }

// $big_Obj = new BigObject($Destination_obj);
// $json = $tools->object2Json($big_Obj);
// echo "big_obj object2Json :"."<br \>";
// print_r($json);
// echo "<br \>";


class Obj_list{
	var $list;
	function __construct($list){
		$this->list = $list;
	}
}

$summary_json = $tools->object2Json($sumary_obj);
$list = array('0'=>$summary_json,'1'=>$summary_json,'2'=>$summary_json);
// $obj_list = new Obj_list($list);
$json =  $tools->array2Json($list);
echo "obj_list :"."<br \>";
print_r($json);
echo "<br \>";

