<?php
require_once "AbstractDaoImpl.class.php";


class FeedbackDaoImpl extends AbstracDaotImpl{
	
	function insert($obj){
	}
	
	function delete($id){
	}
	
	function sava($obj){
	}
	
	function update($obj){
	}
	
	function findByid($id){
	}
	
	function insertFeedback($content,$contact,$time,$contact_type){
		$sql = " INSERT INTO `travel`.`feedback` (`content`, `contact`, `time`, `contact_type`) VALUES ('$content', '$contact', '$time', '$contact_type');";
		$result = $this->execute($sql);
	}
	
}