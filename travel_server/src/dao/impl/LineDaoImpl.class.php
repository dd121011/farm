<?php
require_once "AbstractDaoImpl.class.php";

/**
 * @author tianhao
 *
 */
class LineDaoImpl extends AbstracDaotImpl{
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

	/*
	 *  */
	function creatFindLinesByItineraryIdSql($itineraryId){
		$sql =  "SELECT * FROM `travel`.`relation_itinerary_digraph` where itinerary_id= ".$itineraryId."	;";
		return $sql;
	}

	function findLines($sql){
		try {
			$result = $this->execute($sql);
			if ($result) {
				if($result->num_rows>0){
					$lines_array = array();
					while($row =$result->fetch_array() ){
						$itineraryId = $row['itinerary_id'];
						$stepNum = $row['step_num'];
						$destinationId = $row['destination_id'];
						$destinationName =$row['destination_name'];
						$characteristic = $row['characteristic'];
						$content = $row['content'];
						$pics = $this->findpics($destinationId);
						$lineDao =new LineDao($itineraryId, $stepNum,$destinationId, $destinationName,$characteristic,$content,$pics);
						$lines_array[] = $lineDao;
					}
					return $lines_array;
				}
				return "NULL TODO	";
			}
			return "fail TODO	";

		} catch (Exception $e) {
			echo "Exception".$e;
		}
	}
	
	function  findpics($destinationId){
		$sql = " SELECT * FROM `travel`.`picture` where destination_id = ". $destinationId." ;";
		try {
			$result = $this->execute($sql);
			if ($result) {
				if($result->num_rows>0){
					$pics_array = array();
					while($row =$result->fetch_array() ){
						$url = $row['url'];
						$pics_array[]= $url;
					}
					return $pics_array;
				}
				return "NULL TODO	";
			}
			return "fail TODO	";
		} catch (Exception $e) {
			echo "Exception".$e;
		}
	}
}