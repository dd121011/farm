<?php
class FeedbackDao{
	var $id;
	var $content;
	var $contact;
	var $time;
	var $contact_type; // 联系方式 3为邮箱
	
	
	function __construct($content,$contact,$time,$contact_type){
		$this->content = $content;
		$this->contact = $contact;
		$this->time = $time;
		$this->isused = $contact_type;
	}
	
	
}