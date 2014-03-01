<?php
/**
 * @author tianhao
 *
 */



/*  */
class DestinationDao{
	var $summary ;
	var $introduction;//内容介绍
	var $otherContact;//其他联系方式
	var $car;//自驾路线介绍
	var $bus;//公共交通路线介绍
	var $bike;//骑行路线介绍
	var $classicFlag;//是否为经典
	var $regionCode;//区域编码
	var $mapPic;//静态地图
	var $preferentialInfo;//优惠信息

	
	/*  */
	function __construct($summary,$introduction,
			$otherContact, $car, $bus, $bike,
			$classicFlag, $regionCode, $mapPic,
			$preferentialInfo){
			$this->summary =  $summary;
			$this->introduction = $introduction;
			$this->otherContact = $otherContact;
			$this->car = $car;
			$this->bus = $bus;
			$this->bike = $bike;
			$this->classicFlag = $classicFlag;
			$this->regionCode = $regionCode;
			$this->mapPic = $mapPic;
			$this->preferentialInfo = $preferentialInfo;
			
	}
	
	
	function  getScenerySummary() {
		return $this->summary;
	}
	function  setSummary( $summary) {
		$this->summary = $summary;
	}
	function  getIntroduction() {
		return $this->introduction;
	}
	function  setIntroduction($introduction) {
		$this->introduction = $introduction;
	}
	function  getOtherContact() {
		return $this->otherContact;
	}
	function  setOtherContact($otherContact) {
		$this->otherContact = $otherContact;
	}
	function  getCar() {
		return $this->car;
	}
	function  setCar($car) {
		$this->car = $car;
	}
	function  getBus() {
		return $this->bus;
	}
	function  setBus($bus) {
		$this->bus = $bus;
	}
	function  getBike() {
		return $this->bike;
	}
	function  setBike($bike) {
		$this->bike = $bike;
	}
	function  isClassicFlag() {
		return $this->classicFlag;
	}
	function  setClassicFlag($classicFlag) {
		$this->classicFlag = $classicFlag;
	}
	function  getRegionCode() {
		return $this->regionCode;
	}
	function  setRegionCode($regionCode) {
		$this->regionCode = $regionCode;
	}
	function  getMapPic() {
		return $this->mapPic;
	}
	function  setMapPic($mapPic) {
		$this->mapPic = $mapPic;
	}
	function  getPreferentialInfo() {
		return $this->preferentialInfo;
	}
	function  setPreferentialInfo($preferentialInfo) {
		$this->preferentialInfo = $preferentialInfo;
	}
}