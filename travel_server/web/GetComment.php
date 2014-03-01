<?php
header("Content-Type:text/html;charset=utf-8");
//ini_set("max_execution_time", 600); // s 1 分钟
require_once "../src/util/Tools.class.php";
require_once "../src/dao/impl/AbstractDaoImpl.class.php";
require_once "../src/dao/impl/CommentDaoImpl.class.php";
require_once "../src/dao/CommentDao.class.php";

if(isset($_POST['destination_id']) ){
	isset($_POST['destination_id']) ?$destination_id= $_POST['destination_id']:$destination_id = 0;
	isset($_POST['count']) ? $count = $_POST['count']:$count = 1;
	echo GetComment($destination_id,$count);
}else{
	echo GetComment(1,1);
	//echo "this is test !";
}

function GetComment($destination_id,$count){
	$commentDaoImpl = new CommentDaoImpl();
	$comment_array = $commentDaoImpl->GetComment($destination_id,$count);
	
	$tools = new Tools();//
	$json_array = array();//数组，内容为summary的json
	$length = count($comment_array);
	if($length == 0){ $json_array[] = null;}
	for($i = 0 ; $i<$length; $i++ ){
		$comment_obj = $comment_array[$i];//取对象
		$comment_json = $tools->object2Json($comment_obj);//对象转json
		$json_array[$i] = $comment_json;//json入组
	}
	//将json数组转json
	$res = $tools->jsonArray2Json($json_array);
	/* 返回查询结果  */
	return $res;
}