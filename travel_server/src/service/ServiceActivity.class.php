<?php

class ServiceActivity{
	var $count;
	
	function __construct($count){
		$this->count = $count;
	}
	
	function findActivity(){
		$activityDaoImpl = new ActivityDaoImpl();
		$params = array('count'=>$this->count);
		$sql = $activityDaoImpl->creatFindActivitySql($params);
		$activity_array = $activityDaoImpl->findActivity($sql);
		return $activity_array;
	}
}