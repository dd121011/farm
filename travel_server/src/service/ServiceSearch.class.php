<?php
class ServiceSearch{
	var $name;// 检索内容
	var $count;
	var $res ;//json格式的查询结果，
	
	function __construct($name,$count){
		$this->name = $name;
		$this->count = $count;
	}
	
	/* 通过service获取查询结果
	 * 返回 对象summary的数组	*/
	function search(){
		$destinationDaoImpl = new DestinationdaoImpl();
		$tools = new Tools();
		$params = array('name'=>$this->name,'count'=>$this->count);
		$sql = $destinationDaoImpl->creatSearchSql($params);
		// 		$sql = "SELECT destination_id ,name , lat, lng, address,pic ,tel, phone, hot, price, price_info, score, characteristic  FROM travel.destination where 10<lat<40 and 100<lng<150;";
	
		$Summary_array = $destinationDaoImpl->findSummarys($sql);
		return $Summary_array;
	}
}