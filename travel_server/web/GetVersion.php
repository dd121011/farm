<?php
//ini_set("max_execution_time", 600); // s 1 分钟
require_once "../src/util/Tools.class.php";
require_once "../src/dao/impl/AbstractDaoImpl.class.php";
require_once "../src/dao/impl/VersionDaoImpl.class.php";
require_once "../src/dao/VersionDao.class.php";

if(isset($_POST['type']) ){
	isset($_POST['type']) ? $type = $_POST['type']:$type = 1;
	echo GetVersion($type);
}else{
	echo GetVersion(0);
	echo "this is test !";
}

function GetVersion($type){
	$tools = new Tools();//
	$versionDaoImpl = new VersionDaoImpl();
	$res = $versionDaoImpl->GetVersion($type);
	$res = $tools->object2json($res);
	return $res;
}