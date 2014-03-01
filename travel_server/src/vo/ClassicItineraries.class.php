<?php
class ClassicItinerary {
	var $itineraryId;//线路id
	var $itinerarySummary;//行程的概要
	var $score;//平均分数
	var $hot;//热度
	var $pic;//头像url
	var $price;//人均消费
	var $priceInfo;//价格信息
	var $characteristic;//特色，用$分隔
	var $line = array();//行程步骤
	
	function __construct($itineraryId, $itinerarySummary,
			$score, $hot, $pic, $price, $priceInfo,
			$characteristic, $line) {
		$this->itineraryId = itineraryId;
		$this->itinerarySummary = itinerarySummary;
		$this->score = score;
		$this->hot = hot;
		$this->pic = pic;
		$this->price = price;
		$this->priceInfo = priceInfo;
		$this->characteristic = characteristic;
		$this->line = line;
	}
	
	function getItineraryId() {
		return $this->itineraryId;
	}
	
	function  setItineraryId($itineraryId) {
		$this->itineraryId = itineraryId;
	}
	
	function getItinerarySummary() {
		return $this->itinerarySummary;
	}
	
	function  setItinerarySummary($itinerarySummary) {
		$this->itinerarySummary = itinerarySummary;
	}
	
	function getScore() {
		return $this->score;
	}
	
	function  setScore($score) {
		$this->score = score;
	}
	
	function getHot() {
		return $this->hot;
	}
	
	function  setHot($hot) {
		$this->hot = hot;
	}
	
	function getPic() {
		return $this->pic;
	}
	
	function  setPic($pic) {
		$this->pic = pic;
	}
	
	function getPrice() {
		return $this->price;
	}
	
	function  setPrice($price) {
		$this->price = price;
	}
	
	function getPriceInfo() {
		return $this->priceInfo;
	}
	
	function  setPriceInfo($priceInfo) {
		$this->priceInfo = priceInfo;
	}
	
	function getCharacteristic() {
		return $this->characteristic;
	}
	
	function  setCharacteristic($characteristic) {
		$this->characteristic = characteristic;
	}
	
	function getLine() {
		return $this->line;
	}
	
	function  setLine($line) {
		$this->line = line;
	}
}