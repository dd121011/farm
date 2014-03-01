<?php
class LineDao{
	var $itineraryId;//线路id
	var $stepNum;//步骤编号
	var $destinationId;//本步骤终端id
	var $destinationName;
	var $characteristic;
	var $content;//推荐理由，特色，内容等信息
	var $pics ;
	
	
	
	function __construct($itineraryId, $stepNum,$destinationId, $destinationName,$characteristic,$content,$pics) {
		$this->itineraryId = $itineraryId;
		$this->stepNum = $stepNum;
		$this->destinationId = $destinationId;
		$this-> destinationName = $destinationName;
		$this->characteristic = $characteristic;
		$this->content = $content;
		$this->pics = $pics;
	}
	
}