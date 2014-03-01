<?php
require_once "AbstractDaoImpl.class.php";

/**
 * @author tianhao
 *
 */


class PromotionsDaoImpl extends AbstracDaotImpl{
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

	/* */
	function creatFindPromotionsSql($params){
		$count = $params['count'];
		$sql = "SELECT * FROM `travel`.`promotions` order by start_time desc limit ".
		10*($count-1).
	 	 "	,	10 	;";
		return $sql;
	}

	/* */
	function findPromotions($sql){
		try {
			$result = $this->execute($sql);
			if ($result) {
				if($result->num_rows>0){
					$PromotionsDao_array = array();
					while($row =$result->fetch_array() ){
						$promotionsId = $row['promotions_id'];
						$destinationId = $row['destination_id'];
						$name = $row['name'];
						$content = $row['content'];
						$startTime = $row['start_time'];
						$endTime = $row['end_time'];
						$PromotionsDao = new PromotionsDao($promotionsId, $destinationId, $name,$content, $startTime, $endTime);
						$PromotionsDao_array[] = $PromotionsDao;
					}
					return $PromotionsDao_array;
				}
				return "NULL TODO	";
			}
			return "fail TODO	";

		} catch (Exception $e) {
			echo "Exception".$e;
		}
	}
	}