<?php
require_once "AbstractDaoImpl.class.php";


class VersionDaoImpl extends AbstracDaotImpl{
	
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
	
	function GetVersion($type){
		$sql = "SELECT * FROM `version` where  type = $type ;";
		$result = $this->execute($sql);
		if ($result) {
			if($result->num_rows>0){
				while($row =$result->fetch_array() ){
					$version = $row['version'];
					$content = $row['content'];
					$type = $row['type'];
					$version = new VersionDao($version,$content,$type) ;
					return  $version ;
				}
			}
		}
	}
	
	
}