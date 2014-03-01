<?php
class ServiceTop{
	var $type;
	var $type_value;
	var $count ; 

	function __construct($type,$type_value,$count){
		$this->count = $count;
		$this->type = $type;
		$this->type_value = $type_value;
	}

	function findTop(){
		$destinationDaoImpl = new DestinationdaoImpl();
		$tools = new Tools();
		$params = array('count'=>$this->count , 'type'=>$this->type , 'type_value'=>$this->type_value);
		if( $this->type  == 1){
			/**大类别分类排行*/
			$sql = $destinationDaoImpl->creatTopSql4type($params);
		}else if ($this->type  == 2){
			/**小类别分类——通过label分类排行*/
			$sql = $destinationDaoImpl->creatTopSql4label($params);
		} 
		$Summary_array = $destinationDaoImpl->findSummarys($sql);
		return $Summary_array;
	}
}