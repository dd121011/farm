<?php
header("Content-Type:text/html;charset=utf-8");
//ini_set("max_execution_time", 600); // s 1 分钟
require_once "../src/dao/impl/AbstractDaoImpl.class.php";
require_once "../src/dao/impl/CommentDaoImpl.class.php";

if(isset($_POST['content']) && ($_POST['destination_id']) ){
	isset($_POST['destination_id']) ?$destination_id= $_POST['destination_id']:$destination_id = 0;
	isset($_POST['user_id']) ?$user_id= $_POST['user_id']:$user_id = 0;
	isset($_POST['otherSYS_type']) ?$otherSYS_type = $_POST['otherSYS_type']:$otherSYS_type = 0;
	isset($_POST['content']) ?$content= $_POST['content']:$content ="empty!";
	$comment_time = date("Y-m-d H:i:s" ,strtotime( "now" ));
	isset($_POST['comment_score']) ?$comment_score = $_POST['comment_score']:$comment_score = 3;
	Comment($destination_id,$user_id,$otherSYS_type,$content,$comment_time,$comment_score);
	echo "0";
}else{
	$destination_id = 0;
	$content = 'this is test!';
	isset($_POST['user_id']) ?$user_id= $_POST['user_id']:$user_id = 1;
	isset($_POST['otherSYS_type']) ?$otherSYS_type = $_POST['otherSYS_type']:$otherSYS_type = 0;
	$comment_time = date("Y-m-d H:i:s" ,strtotime( "now" ));
	isset($_POST['comment_score']) ?$comment_score = $_POST['comment_score']:$comment_score = 3;
	Comment($destination_id,$user_id,$otherSYS_type,$content,$comment_time,$comment_score);
	echo "this is test !";
}

function Comment($destination_id,$user_id,$otherSYS_type,$content,$comment_time,$comment_score){
	$commentDaoImpl = new CommentDaoImpl();
	$commentDaoImpl->insertComment($destination_id,$user_id,$otherSYS_type,$content,$comment_time,$comment_score);

}