<?php
class Activity{
	var $activityId;//后台数据中，活动的唯一标示
	var $name;//活动名称
	var $content;//活动内容
	var $startTime;//活动开始时间，8位
	var $endTime;//活动结束时间
	
	
	
	function __construct($activityId, $name, $content,
			$startTime, $endTime) {
		$this->activityId = $activityId;
		$this->name = $name;
		$this->content = $content;
		$this->startTime = $startTime;
		$this->endTime = $endTime;
	}
	
	function  getActivityId() {
		return $this->this->activityId;
	}
	function  setActivityId($activityId) {
		$this->activityId = $activityId;
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
