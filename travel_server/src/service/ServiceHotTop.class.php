<?php
class ServiceHotTop{
	var $count ; //返回的记录条数，默认为20。

	function __construct($count){
		$this->count = $count;
	}

	function findHotTop(){
		$destinationDaoImpl = new DestinationdaoImpl();
		$tools = new Tools();
		$params = array('count'=>$this->count);
		$sql = $destinationDaoImpl->creatHotTopSql($params);
		$Summary_array = $destinationDaoImpl->findSummarys($sql);
		return $Summary_array;
	}
}