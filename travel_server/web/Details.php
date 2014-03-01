<?php
header("Content-Type:text/html;charset=utf-8");
ini_set("max_execution_time", 600); // s 1 分钟

require_once "../src/util/Tools.class.php";
require_once "../src/service/ServiceDetails.class.php";
require_once "../src/vo/Summary.class.php";
require_once "../src/vo/Destination.class.php";
require_once "../src/vo/VillaPic.class.php";
require_once "../src/dao/DestinationDao.class.php";
require_once "../src/dao/impl/DestinationDaoImpl.class.php";
require_once "../src/dao/impl/LabelsDaoImpl.class.php";
require_once "../src/dao/impl/PictureDaoImpl.class.php";
require_once "../src/dao/impl/AbstractDaoImpl.class.php";

if (isset( $_POST['destinationId'])){
	$destinationId =$_POST['destinationId'];
	/* return details*/
	echo getDetails($destinationId);
}else{
	/* for debug */
	echo getDetails(250);
	/* TODO */
	echo "error  destinationId is null !";
}

function getDetails($destinationId){
	$tools = new Tools();
	
	$service = new ServiceDetails($destinationId);
	//get obj
	$destination = $service->getDetailsById($destinationId);
	//将obj转json,
	$res = $tools->object2json($destination);
	/* 返回查询结果  */
	//replace "line":"[{"  to   "line":[{"
	$res = str_replace("\"{" , "{" , $res);
	$res = str_replace("}\"" , "}" , $res);
	return $res;
}