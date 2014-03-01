<?php
class Line{
	var $itineraryId;//线路id
	var $stepNum;//步骤编号
	var $startDestinationId;//本步骤起点id
	var $endDestinationId;//本步骤终端id
	var $lineContent;//本步骤线路描述，出行方式及内容
	
	
	
	function __construct($itineraryId, $stepNum, $startDestinationId,
			$endDestinationId, $lineContent) {
		$this->itineraryId = $$itineraryId;
		$this->stepNum = $stepNum;
		$this->startDestinationId = $startDestinationId;
		$this->endDestinationId = $endDestinationId;
		$this->lineContent = $lineContent;
	}
	
	function getItineraryId() {
		return $this->itineraryId;
	}
	
	function  setItineraryId($itineraryId) {
		$this->itineraryId = $itineraryId;
	}
	
	function getStepNum() {
		return $this->stepNum;
	}
	
	function  setStepNum($stepNum) {
		$this->stepNum = $stepNum;
	}
	
	function getStartDestinationId() {
		return $this->startDestinationId;
	}
	
	function  setStartDestinationId($startDestinationId) {
		$this->startDestinationId = $startDestinationId;
	}
	
	function getEndDestinationId() {
		return $this->endDestinationId;
	}
	
	function  setEndDestinationId($endDestinationId) {
		$this->endDestinationId = $endDestinationId;
	}
	
	function getLineContent() {
		return $this->$this->lineContent;
	}
	
	function  setLineContent($lineContent) {
		$this->lineContent = $lineContent;
	}
}