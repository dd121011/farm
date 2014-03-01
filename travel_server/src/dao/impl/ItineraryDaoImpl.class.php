<?php
require_once "AbstractDaoImpl.class.php";

/**
 * @author tianhao
 *
 */
class ClassicItineraryDaoImpl extends AbstracDaotImpl{
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
	function creatClassicSql($params){
		$count = $params['count'] ;
		$sql =  "SELECT * FROM `travel`.`itinerary` where classic_flag = 1	order by score desc limit ".
		10*($count-1).
	 	 "	,	10 	;";
		return $sql;
	}
	
	function findItineraries($sql){
		$lineDao = new  LineDaoImpl();
		try {
			$result = $this->execute($sql);
			if ($result) {
				if($result->num_rows>0){
					$Itinerary_array = array();
					while($row =$result->fetch_array() ){
						$itineraryId = $row['itinerary_id'];
						$itinerarySummary = $row['itinerary_summary'];
						$name =  $row['name'];
						$score = $row['score'];
						$hot = $row['hot'];
						$pic = $row['pic'];
						$PicMap = $row['pic_map'];
						$price = $row['price'];
						$priceInfo = $row['price_info'];
						$characteristic = $row['characteristic'];
						$line =$lineDao->findLines($lineDao->creatFindLinesByItineraryIdSql($itineraryId));
						$ItineraryDao =new ItineraryDao($itineraryId, $name,$itinerarySummary,
			$score, $hot, $pic,$PicMap, $price, $priceInfo,
			$characteristic, $line);
						$Itinerary_array[] = $ItineraryDao;
					}
					return $Itinerary_array;
				}
				return "NULL TODO	";
			}
			return "fail TODO	";
	
		} catch (Exception $e) {
			echo "Exception".$e;
		}
	}
}