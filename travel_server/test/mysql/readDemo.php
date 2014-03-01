<?php
//修改最大执行时间
ini_set("max_execution_time", 60); // s 1 分钟
//连接参数
$db_servername = "172.20.50.19:3306";
$db_user = "root";
$db_password = "123";
$db_database = "app_saeDemo";
$sql ="SELECT * FROM `app_saeDemo`.`cvs_test`    ;";

//禁止输出错误信息
error_reporting(0);

//连接数据库
$mysqli = new mysqli($db_servername, $db_user, $db_password,$db_database);
$result = $mysqli->query($sql);
printf("Select returned %d rows.\n"."<br>", $result->num_rows);
if ($result) {
	if($result->num_rows>0){
		while($row =$result->fetch_array() ){
			echo ($row[0])."<br>";
			echo ($row[1])."<br>";
			echo ($row[2])."<br>";
			echo ($row[3])."<br>";
		}
	}
}






?>
