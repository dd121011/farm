<?php
	ini_set("max_execution_time", 600); // s 10 分钟
	
	class employee{
		 public $name; //必须为public
		 public $sex;
		
		function __construct($name,$sex){
				$this->name = $name;
				$this->sex = $sex;
		}
	}
	
	$th = new employee("th","sex");
	echo json_encode(($th));