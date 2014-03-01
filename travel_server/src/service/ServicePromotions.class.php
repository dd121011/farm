<?php
class ServicePromotions{
	var $count;

	function __construct($count){
		$this->count = $count;
	}

	function findPromotions(){
		$PromotionsDaoImpl = new PromotionsDaoImpl();
		$params = array('count'=>$this->count);
		$sql = $PromotionsDaoImpl->creatFindPromotionsSql($params);
		$Promotions_array = $PromotionsDaoImpl->findPromotions($sql);
		return $Promotions_array;
	}
}