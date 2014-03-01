<?php
class ServicePlays{
	var $count ; //返回的记录条数，默认为20。
	var $type;

	function __construct($count,$type){
		$this->count = $count;
		$this->type = $type;
	}

	function findPlans(){
		$destinationDaoImpl = new DestinationdaoImpl();
		$tools = new Tools();
		$params = array('count'=>$this->count ,'type'=>$this->type);
		$sql = $destinationDaoImpl->creatPlansSql($params);
		$Summary_array = $destinationDaoImpl->findSummarys($sql);
		return $Summary_array;
	}
}