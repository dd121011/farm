<?php
class ServiceClassicItineraries{
	var $count ; //返回的记录条数，默认为20。

	function __construct($count){
		$this->count = $count;
	}

	function findClassic(){
		$classicItinerariesDaoImpl = new ClassicItineraryDaoImpl();
		$tools = new Tools();
		$params = array('count'=>$this->count);
		$sql = $classicItinerariesDaoImpl->creatClassicSql($params);
		$Itinerary_array = $classicItinerariesDaoImpl->findItineraries($sql);
		return $Itinerary_array;
	}
}