<?php
class PromotionsDao{
	var $promotionsId;//后台数据中， 促销的唯一标示
	var $destinationId;//内容id
	var $name;//促销名称
	var $content;//促销内容
	var $startTime;//促销开始时间，具体到某天，8位字符串
	var $endTime;//促销结束时间
	
	function __construct($promotionsId, $destinationId, $name,
			$content, $startTime, $endTime) {
		$this->promotionsId = $promotionsId;
		$this->destinationId = $destinationId;
		$this->name = $name;
		$this->content = $content;
		$this->startTime = $startTime;
		$this->endTime = $endTime;
	}
	
	function getPromotionsId() {
		return $this->promotionsId;
	}
	function  setPromotionsId($promotionsId) {
		$this->promotionsId = $promotionsId;
	}
	function getDestinationId() {
		return $this->destinationId;
	}
	function  setDestinationId($destinationId) {
		$this->destinationId = $destinationId;
	}
	function getName() {
		return $this->name;
	}
	function  setName($name) {
		$this->name = $name;
	}
	function getContent() {
		return $this->content;
	}
	function  setContent($content) {
		$this->content = $content;
	}
	function getStartTime() {
		return $this->startTime;
	}
	function  setStartTime($startTime) {
		$this->startTime = $startTime;
	}
	function getEndTime() {
		return $this->endTime;
	}
	function  setEndTime($endTime) {
		$this->endTime = $endTime;
	}
}