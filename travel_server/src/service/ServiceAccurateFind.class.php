<?php

class ServiceAccurateFind{
	var $regionCode;
	var $classicFlag;
	var $labels;
	var $count;
	
	/**
	 * @param unknown_type $regionCode
	 * @param unknown_type $classicFlag
	 * @param unknown_type $labels
	 * @param unknown_type $count
	 */
	function __construct($regionCode,$classicFlag,$labels,$count){
		$this->regionCode = $regionCode;
		$this->classicFlag = $classicFlag;
		$this->labels = $labels;
		$this->count = $count;
	}
	
	/**
	 * @return multitype:
	 */
	function accurateFind(){
		$destinationDaoImpl = new DestinationDaoImpl();
		$Summary_array= array();//临时数组，存放过滤后的Summary对象
		$params = array(
				'regionCode'=>$this->regionCode ,
				'classicFlag'=>$this->classicFlag,
				'labels'=>$this->labels,
				'count'=>$this->count);
		$sql = $destinationDaoImpl->createAccurateFindSql($params);
		$Summary_array = $destinationDaoImpl->findSummarys($sql);
		return $Summary_array;
	}
}

