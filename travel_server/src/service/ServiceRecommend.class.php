<?php

class ServiceRecommend{
	var $type ; 
	var $isused;
	
	function __construct($type,$isused){
		$this->type = $type;
		$this->isused = $isused;
	}
	
	function findRecommend(){
		$destinationDaoImpl = new DestinationdaoImpl();
		$tools = new Tools();
		$params = array('type'=>$this->type,'isused'=>$this->isused);
		$sql = $destinationDaoImpl->creatCommendSql($params);
		$Summary_array = $destinationDaoImpl->findSummarys($sql);
		return $Summary_array;
	}
}