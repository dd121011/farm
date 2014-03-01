<?php
class View2farmhome{
	var $view_id;
	var $farmhome_id;
	var $type;
	var $distance;
	var $summary;
	
	function __construct($view_id,$farmhome_id,$type,$distance,$summary) {
		$this->view_id = $view_id;
		$this->farmhome_id = $farmhome_id;
		$this->type = $type;
		$this->distance = $distance;
		$this->summary =$summary ;
	}
}