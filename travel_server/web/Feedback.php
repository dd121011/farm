<?php
header("Content-Type:text/html;charset=utf-8");
//ini_set("max_execution_time", 600); // s 1 分钟
require_once "../src/dao/impl/AbstractDaoImpl.class.php";
require_once "../src/dao/impl/FeedbackDaoImpl.class.php";

if(isset($_POST['content'])  ){
	isset($_POST['content']) ?$content = $_POST['content']:$content= "test_content";
	isset($_POST['contact']) ?$contact = $_POST['contact']:$contact = "test_contact";
	isset($_POST['time']) ?$time= $_POST['time']:$time = date("Y-m-d H:i:s" ,strtotime( "now" ));
	isset($_POST['contact_type']) ?$contact_type = $_POST['contact_type']:$contact_type = 3;
	Feedback($content,$contact,$time,$contact_type);
	echo "0";
}else{
	echo "content is null!";
}

function Feedback($content,$contact,$time,$contact_type){
	$feedbackDaoImpl = new FeedbackDaoImpl();
	$feedbackDaoImpl->insertFeedback($content,$contact,$time,$contact_type);
	
}