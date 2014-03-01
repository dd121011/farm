<?php
require_once "../src/vo/Summary.class.php";
require_once "../src/dao/DestinationDao.class.php";
require_once "../src/util/Tools.class.php";

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
$summary_json = $tools->object2Json($sumary_obj);
$list = array($summary_json,$summary_json,$summary_json);
$json =  $tools->array2Json($list);
print_r($json);