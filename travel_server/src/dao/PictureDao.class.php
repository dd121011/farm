<?php
class PictureDao{
	var $pictureId;
	var $destinationId;
	var $url;
	
	function __construct($pictureId,$destinationId,$url){
		$this->pictureId = $pictureId;
		$this->destinationId = $destinationId;
		$this->url = $url;
	}
	
	
}