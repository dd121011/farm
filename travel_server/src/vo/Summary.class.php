<?php
/**
 * @author tianhao
 *
 */


/*  */
class Summary{
	var $destinationId;//内容id，景点/农家乐等内容在后台系统中的唯一标示
	var $name;//内容名称
	var $lat;//内容在地图上标点的纬度坐标
	var $lng;//内容在地图上标点的经度坐标
	var $address;//内容详细地址
	var $pic;//图片URL
	var $tel;//座机电话
	var $phone;//手机号码
	var $hot;//热度
	var $price;//人均消费/票价
	var $priceInfo;//价格信息
	var $score;//平均分数
	var $distance;//离用户距离，单位为km
	var $characteristic;//特色，用$分隔
	var $type;//分类
	

	
	/*  */
	function __construct($destinationId,$name,$lat,$lng,$address,$pic,$tel,$phone,$hot,$price,$priceInfo,$score,$distance,
			$characteristic,$type){
		$this->destinationId = $destinationId;
		$this->name = $name;
		$this->lat = $lat;
		$this->lng = $lng;
		$this->address = $address;
		$this->pic = $pic;
		$this->tel = $tel;
		$this->phone = $phone;
		$this->hot = $hot;
		$this->price = $price;
		$this->priceInfo = $priceInfo;
		$this->score = $score;
		$this->distance = $distance;
		$this->characteristic = $characteristic;
		$this->type = $type;
	}
	
	public function getDestinationId() {
		return $this->destinationId;
	}
	public function setDestinationId($destinationId) {
		$this->destinationId = $destinationId;
	}
	public function getName() {
		return $this->name;
	}
	public function setName($name) {
		$this->name = $name;
	}
	public function getLat() {
		return $this->lat;
	}
	public function setLat($lat) {
		$this->lat = $lat;
	}
	public function getLng() {
		return $this->lng;
	}
	public function setLng($lng) {
		$this->lng = $lng;
	}
	public function getAddress() {
		return $this->address;
	}
	public function setAddress($address) {
		$this->address = $address;
	}
	public function getPic() {
		return $this->pic;
	}
	public function setPic($pic) {
		$this->pic = $pic;
	}
	public function getTel() {
		return $this->tel;
	}
	public function setTel($tel) {
		$this->tel = $tel;
	}
	public function getPhone() {
		return $this->phone;
	}
	public function setPhone($phone) {
		$this->phone = $phone;
	}
	public function getHot() {
		return $this->hot;
	}
	public function setHot($hot) {
		$this->hot = $hot;
	}
	public function getPrice() {
		return $this->price;
	}
	public function setPrice($price) {
		$this->price = $price;
	}
	public function getPriceInfo() {
		return $this->priceInfo;
	}
	public function setPriceInfo($priceInfo) {
		$this->priceInfo = $priceInfo;
	}
	public function getScore() {
		return $this->score;
	}
	public function setScore($score) {
		$this->score = $score;
	}
	public function getDistance() {
		return $this->distance;
	}
	public function setDistance($distance) {
		$this->distance = $distance;
	}
	public function getCharacteristic() {
		return $this->characteristic;
	}
	public function setCharacteristic($characteristic) {
		$this->characteristic = $characteristic;
	}
	public function getType() {
		return $this->type;
	}
	public function setType($type) {
		$this->characteristic = $type;
	}

}