<?php

class ServiceMessage{
	var $type ;
	var $isused;

	function __construct($type,$isused){
		$this->type = $type;
		$this->isused = $isused;
	}

	function findMessage(){
		$recommendDaoImpl = new RecommendDaoImpl();
		$tools = new Tools();
		$params = array('type'=>$this->type,'isused'=>$this->isused);
		$sql = $recommendDaoImpl->creatGetMessage($params);
		$Summary_array = $recommendDaoImpl->getMessage($sql);
		return $Summary_array;
	}
}