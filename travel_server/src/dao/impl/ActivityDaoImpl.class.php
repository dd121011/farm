<?php
require_once "AbstractDaoImpl.class.php";

/**
 * @author tianhao
 *
 */


class ActivityDaoImpl extends AbstracDaotImpl{
	function insert($obj){
	}
	
	function delete($id){
	}
	
	function sava($obj){
	}
	
	function update($obj){
	}
	
	function findByid($id){}
	
	/* */
	function creatFindActivitySql($params){
		$count = $params['count'];
		$sql = "SELECT * FROM `travel`.`activity` order by start_time desc limit ".
		10*($count-1).
	 	 "	,	10 	;";
		return $sql;
	}
	
	/* */
	function findActivity($sql){
		try {
			$result = $this->execute($sql);
			if ($result) {
				if($result->num_rows>0){
					$activityDao_array = array();
					while($row =$result->fetch_array() ){
						$activityId = $row['activity_id'];
						$name = $row['name'];
						$introduction = $row['introduction'];
						$startTime = $row['start_time'];
						$endTime = $row['end_time'];
						$lat = $row['lat'];
						$lng = $row['lng'];
						$address = $row['address'];
						$pic = $row['pic'];
						$tel = $row['tel'];
						$region_code = $row['region_code'];
						$type = $row['type'];
						$www = $row['www'];
						$activityDao = new ActivityDao($activityId, $name, $introduction,$startTime, $endTime,
								$lat, $lng,$address,$pic,$tel ,$region_code,$type,$www);
						$activityDao_array[] = $activityDao;
					}
					return $activityDao_array;
				}
				return "NULL TODO	";
			}
			return "fail TODO	";
		
		} catch (Exception $e) {
			echo "Exception".$e;
		}
		
	}
}