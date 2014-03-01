<?php 
//printf(" ok !");
	if (isset( $_GET['name'])){
		$value =$_GET['name'];
// 		printf(" ok !");
		$array=array('name'=>$value);
		echo json_encode($array);
	}else{
			echo "linked";
	}
?>