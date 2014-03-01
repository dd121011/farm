<?php
require_once "AbstractDaoImpl.class.php";

class RecommendDaoImpl extends AbstracDaotImpl{
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
	
	function creatGetMessage($params){
		$type = $params['type'];
		$isused = $params['isused'];
		$sql =  "SELECT * FROM `travel`.`recommend` where   travel.recommend.type = ".
	  	$type.
	  	"	and travel.recommend.isused =".
	  	$isused.
	  	"	;";
		return $sql;
	}
	
	function getMessage($sql){
		try {
			$result = $this->execute($sql);
			if ($result) {
				if($result->num_rows>0){
					$recomment_array = array();
					while($row =$result->fetch_array() ){
						$idRecommend = $row['idRecommend'];
						$destination_id = $row['destination_id'];
						$content = $row['content'];
						$type = $row['type'];
						$time = $row['time'];
						$isused =$row['isused'];
						$recommentDao =new RecommentDao($idRecommend,$destination_id,$content,$type,$time,$isused);
						$recomment_array[] = $recommentDao;
					}
					return $recomment_array;
				}
				return "NULL TODO	";
			}
			return "fail TODO	";
	
		} catch (Exception $e) {
			echo "Exception".$e;
		}
	}
}