<?php
class VillaPic{
	var $type;
	var $content;
	var $pic;

	
	function __construct($type, $content,$pic){
		$this->type = $type;
		$this->content = $content;
		$this->pic = $pic ;
	}
	
}