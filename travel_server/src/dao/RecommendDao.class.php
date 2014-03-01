<?php

class RecommentDao{
	var $idRecommend;//后台数据中，的唯一标示
	var $destination_id;//推荐内容id
	var $content;//推荐文案
	var $type;//推荐类型，1.首页推荐。2其它
	var $time;//推荐时间
	var $isused;//是否作为当前推荐使用
	
	function __construct($idRecommend,$destination_id,$content,$type,$time,$isused){
		$this->idRecommend = $idRecommend;
		$this->destination_id = $destination_id;
		$this->content = $content;
		$this->type = $type;
		$this->time = $time;
		$this->isused = $isused;
	}
	
	function getContent() {
		return $this->content;
	}
	function  setContent($content) {
		$this->content = $content;
	}
	
}