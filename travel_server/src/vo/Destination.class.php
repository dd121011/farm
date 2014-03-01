<?php
/**
 * @author tianhao
 *
 */



/*  */
class Destination{
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
	//vo 属性
	var $region;//区域名称
	var $labels;//内容类型，后台系统用于分类
	var $pics =  array();//该景点图片
	var $picsVilla ;
	
	/*  */
	function __construct($destinationDao, $region, $labels, $pics,$picsVilla ){
		if($destinationDao instanceof  DestinationDao ){
			$this->summary =  $destinationDao->summary;
			$this->introduction = $destinationDao->introduction;
			$this->otherContact = $destinationDao->otherContact;
			$this->car = $destinationDao->car;
			$this->bus= $destinationDao->bus ;
			$this->bike = $destinationDao->bike;
			$this->classicFlag = $destinationDao->classicFlag;
			$this->regionCode = $destinationDao->regionCode;
			$this->mapPic = $destinationDao->mapPic;
			$this->preferentialInfo = $destinationDao->preferentialInfo;
		}
			$this->region = $region;
			$this->labels = $labels;
			$this->pics = $pics;
			$this->picsVilla = $picsVilla;
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
	function  getRegion() {
		return $this->region;
	}
	function  setRegion($region) {
		$this->region = $region;
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
	function  getLabels() {
		return $this->labels;
	}
	function  setLabels($labels) {
		$this->labels = $labels;
	}
	function  getPics() {
		return $this->pics;
	}
	function  setPics($pics) {
		$this->pics = $pics;
	}
	
}