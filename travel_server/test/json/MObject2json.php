<?php
ini_set("max_execution_time", 600); // s 10 分钟

class employee{
	public $name; //必须为public
	public $sex;
	public $lover;

	function __construct($name,$sex,$lover){
		$this->name = $name;
		$this->sex = $sex;
		$this->lover = $lover;
	}
}

class lover{
	public $name; //必须为public
	public $sex;
	
	function __construct($name,$sex){
		$this->name = $name;
		$this->sex = $sex;
	}
}
	

	$lover= new lover("lyj","woman");
	$th = new employee("th","man",$lover);
	echo json_encode(($th));

