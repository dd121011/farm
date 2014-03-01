<?php
class ActivityDao{
	var $activityId;//后台数据中，活动的唯一标示
	var $name;//活动名称
	var $start_time;//活动开始时间，8位
	var $end_time;//活动结束时间
	
	
	
	function __construct($activityId, $name, $introduction,$startTime, $endTime,
								$lat, $lng,$address,$pic,$tel ,$region_code,$type,$www) {
		$this->activityId = $activityId;
		$this->name = $name;
		$this->introduction = $introduction;
		$this->start_time = $startTime;
		$this->end_time = $endTime;
		$this->lat = $lat;
		$this->lng = $lng;
		$this->address = $address;
		$this->pic = $pic;
		$this->tel = $tel;
		$this->region_code = $region_code;
		$this->type = $type;
		$this->www = $www;
	}
	
}
