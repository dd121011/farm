<?php

class VersionDao{
	var $version;
	var $content ;
	var $type ;
	
	function __construct($version,$content,$type){
		$this->version = $version;
		$this->content = $content;
		$this->type = $type;
	}
	
}