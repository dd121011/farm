<?php
require_once "AbstractDaoImpl.class.php";

/**
 * @author tianhao
 *
 */

class DestinationDaoImpl extends AbstracDaotImpl{

	function insert($obj){
	}

	function delete($id){
	}

	function sava($obj){
	}

	function update($obj){
	}

	function findByid($id){
		$sql = "SELECT * FROM `travel`.`destination` where destination_id = ".$id."	;";
		try {
			$result = $this->execute($sql);
			if ($result) {
				if($result->num_rows>0){
					while($row =$result->fetch_array() ){
						$destinationId = $row['destination_id'];
						$name = $row['name'];
						$lat = $row['lat'];
						$lng = $row['lng'];
						$address = $row['address'];
						$pic = $row['pic'];
						$tel = $row['tel'];
						$phone = $row['phone'];
						$hot = $row['hot'];
						$price = $row['price'];
						$priceInfo = $row['price_info'];
						$score = $row['score'];
						$distance = 0;
						$characteristic = $row['characteristic'];
						$type = $row['type'];
						//构造摘要
						$summary =new Summary($destinationId,$name,$lat,$lng,$address,$pic,$tel,$phone,$hot,$price,$priceInfo,$score,$distance,$characteristic,$type);
						//其它属性
						$introduction 	= $row['introduction'];
						$otherContact = $row['other_contact'];
						$car = $row['car'];
						$bus = $row['bus'];
						$bike = $row['bike'];
						$classicFlag = $row['classic_flag'];
						$regionCode = $row['region_code'];
						$mapPic = $row['map_pic'];
						$preferentialInfo = $row['preferential_info'];
						//构造obj
						$destinationDao = new DestinationDao($summary,$introduction,
								$otherContact, $car, $bus, $bike,
								$classicFlag, $regionCode, $mapPic,
								$preferentialInfo );
					}
					return $destinationDao;
				}
				return "NULL TODO	";
			}
			return "fail TODO	";
		}catch (Exception $e) {
			echo "Exception".$e;
		}
	}

	/**
	 * 根据经纬度，查询一定距离内 的用户；
	 * 返回结果有误差，
	 * 最终返回的用户范围在以用户为坐标的正方形内
	 * @see AbstractImpl::creatSql()
	 */
	function creatNearbySql($params){
		$lat = $params['lat'] ;
		$lng = $params['lng'] ;
		$distance = $params['distance'] ;
		$count = $params['count'] ;
		$type = $params['type'];
		$scale = 111.31949079327;//1维度的距离差；
		/*计算经纬度步长*/
		$half_distance =0.707*($distance)/($scale);
		 
		$sql =  "SELECT destination_id ,name , lat, lng, address,pic ,tel, phone, hot, price, price_info, score, characteristic,type  FROM travel.destination where "
		.($lat-($half_distance/2))."<lat<".($lat+($half_distance/2)).
		" and ".
		($lng-($half_distance/2))."<lng<".($lng+($half_distance/2)).
		" and ".
		"	type	=	".$type.
		"	and ACOS(SIN(($lat * 3.1415) / 180 ) *SIN((lat * 3.1415) / 180 ) +COS(($lat * 3.1415) / 180 ) * COS((lat * 3.1415) / 180 ) *COS(($lng* 3.1415) / 180 - (lng * 3.1415) / 180 ) ) * 6380 < $distance".
		"	limit ".
		10*($count-1).
		"	,	10 	;";
		return $sql;
	}

	/**
	 * 查找经典
	 * @param unknown_type $params
	 * @return string
	 */
	function creatClassicSql($params){
		$count = $params['count'] ;
		$sql =  "SELECT destination_id ,name , lat, lng, address,pic ,tel, phone, hot, price, price_info, score, characteristic ,type FROM travel.destination where classic_flag =1 order by score desc limit ".
	  	10*($count-1).
	  	"	,	10 	;";
		return $sql;
	}

	/**
	 * 查找热榜
	 * @param unknown_type $params
	 * @return string
	 */
	function creatHotTopSql($params){
		$count = $params['count'] ;
		$sql =  "SELECT destination_id ,name , lat, lng, address,pic ,tel, phone, hot, price, price_info, score, characteristic,type  FROM travel.destination  order by hot desc limit ".
	  	10*($count-1).
	  	"	,	10	;";
		return $sql;
	}
	 
	/**
	 * 查找
	 * @param unknown_type $params
	 * @return string
	 */
	function creatSearchSql($params){
		$name = $params['name'] ;
		$count = $params['count'] ;
		$sql =  "SELECT destination_id ,name , lat, lng, address,pic ,tel, phone, hot, price, price_info, score, characteristic,type  FROM travel.destination where name like '%$name%'	order by score desc limit ".
				10*($count-1).
	 	 "	,	10 	;";
		return $sql;
	}
	 
	/**
	 * @param unknown_type $params
	 * @return string
	 */
	function  createAccurateFindSql($params){
		$regionCode = $params['regionCode'] ;
		$classicFlag = $params['classicFlag'] ;
		$labels = $params['labels'] ;
		$count = $params['count'] ;

		$sql = "SELECT travel.destination.destination_id ,
		travel.destination.name ,
		travel.destination.lat,
		travel.destination.lng,
		travel.destination.address,
		travel.destination.pic ,
		travel.destination.tel,
		travel.destination.phone,
		travel.destination.hot,
		travel.destination.price,
		travel.destination.price_info,
		travel.destination.score,
		travel.destination.characteristic ,
		travel.destination.type
		FROM travel.destination ,travel.relation_destination_label
		where travel.destination.destination_id = travel.relation_destination_label.destination_id  ";
		if($classicFlag !=  ""){
			$sql .="and travel.destination.classic_flag = $classicFlag	";
		}

		if($regionCode != 0 ){
			$array_regionCode = explode("&",$regionCode);
			$length = count($array_regionCode);
			$sql .= "	and ( travel.destination.region_code = $array_regionCode[0]	";
	  for($i= 1;$i<$length ;$i++) {
	  	$sql .= " or travel.destination.region_code = $array_regionCode[$i] " ;
	  }
	  $sql .= "	)	";
		}
		 
		if ($labels != 0){
			$array_label = explode("&",$labels);
			$length = count($array_label) ;
			$sql .= " and ( travel.relation_destination_label.label_name = $array_label[0] " ;
			for($i= 1;$i<$length ;$i++) {
				$sql .= " or travel.relation_destination_label.label_name = $array_label[$i] " ;
			}
			$sql .= "	)	";
		}
		//去重
		$sql .= "	group by travel.destination.destination_id ";
		//显示个数
		$sql .= "	limit ".
				10*($count-1).
				"	,	10 ;";;
		//返回sql
		return $sql;
	}

	/*
	 * 查找推荐
	*   */
	function creatCommendSql($params){
		$type = $params['type'];
		$isused = $params['isused'];
		$sql =" SELECT travel.destination.destination_id ,
		travel.destination.name ,
		travel.destination.lat,
		travel.destination.lng,
		travel.destination.address,
		travel.destination.pic ,
		travel.destination.tel,
		travel.destination.phone,
		travel.destination.hot,
		travel.destination.price,
		travel.destination.price_info,
		travel.destination.score,
		travel.destination.characteristic ,
		travel.destination.type
		from  travel.destination ,travel.recommend
		where travel.destination.destination_id= travel.recommend.destination_id".
		"	and travel.recommend.type = ".
		$type.
		"	and travel.recommend.isused =".
		$isused.
		"	;";
		return $sql;
	}
	 
	/** 根据类型查找*/
	function creatPlansSql($params){
		$count = $params['count'] ;
		$type = $params['type'];
		$sql = "SELECT
		destination.destination_id ,
		destination.name ,
		destination.lat,
		destination.lng,
		destination.address,
		destination.pic ,
		destination.tel,
		destination.phone,
		destination.hot,
		destination.price,
		destination.price_info,
		destination.score,
		destination.characteristic ,
		destination.type
		FROM travel.relation_destination_label,  travel.destination
		where  destination.destination_id = relation_destination_label.destination_id
		and relation_destination_label.label_id = 1
		limit ".
		10*($count-1).
		"	,	10 	;";
		return $sql;
	}
	 
	/**大类别分类排行*/
	function creatTopSql4type($params){
		$count = $params['count'] ;
		$type = $params['type'];
		$type_value = $params['type_value'];
		//特产类，可以根据小类别进行排行
		if( 40 < $type_value &&  $type_value < 50){
			$sql = " SELECT
			destination.destination_id 
			destination.name ,
			destination.lat,
			destination.lng,
			destination.address,
			destination.pic ,
			destination.tel,
			destination.phone,
			destination.hot,
			destination.price,
			destination.price_info,
			destination.score,
			destination.characteristic ,
			destination.type
			FROM  travel.destination
			where  destination.type = 	4
			and destination.type_value = $type_value
			order by score desc
			limit 0 , ".
			$count.
			"	;";
		}else{
			$sql = " SELECT
			destination.destination_id ,
			destination.name ,
			destination.lat,
			destination.lng,
			destination.address,
			destination.pic ,
			destination.tel,
			destination.phone,
			destination.hot,
			destination.price,
			destination.price_info,
			destination.score,
			destination.characteristic ,
			destination.type
			FROM  travel.destination
			where  destination.type = 	".
			$type_value ."
			order by score desc
			limit 0 , ".
			$count.
			"	;";
		}
		
		return $sql;
	}
	 
	/**小类别分类排行*/
	function creatTopSql4label($params){
		$count = $params['count'] ;
		$type = $params['type'];
		$type_value = $params['type_value'];
		$sql = "  SELECT destination.destination_id , destination.name , destination.lat, destination.lng,
		destination.address, destination.pic , destination.tel, destination.phone, destination.hot,
		destination.price, destination.price_info, destination.score, destination.characteristic , destination.type
		FROM travel.relation_destination_label, travel.destination
		where destination.destination_id =  relation_destination_label.destination_id
		and relation_destination_label.label_id = ".
		$type_value ."
		order by score desc
		limit 0 , 10 ;";
		return $sql;
	}
	 
	/**自驾排行*/
	function creatTopSql4carl(){
		$count = $params['count'] ;
		$type = $params['type'];
		$type_value = $params['type_value'];
		$sql = "";
		return $sql;
	}
	 
	/**
	 * @param unknown_type $sql
	 * @return multitype:Summary |string
	 */
	function findSummarys($sql){
		try {
			$result = $this->execute($sql);
			if ($result) {
				if($result->num_rows>0){
					$Summary_array = array();
					while($row =$result->fetch_array() ){
						$destinationId = $row['destination_id'];
						$name = $row['name'];
						$lat = $row['lat'];
						$lng = $row['lng'];
						$address = $row['address'];
						$pic = $row['pic'];
						$tel = $row['tel'];
						$phone = $row['phone'];
						$hot = $row['hot'];
						$price = $row['price'];
						$priceInfo = $row['price_info'];
						$score = $row['score'];
						$distance = 0;
						$characteristic = $row['characteristic'];
						$type = $row['type'];
						$summary =new Summary($destinationId,$name,$lat,$lng,$address,$pic,$tel,$phone,$hot,$price,$priceInfo,$score,$distance,$characteristic,$type);
						$Summary_array[] = $summary;
					}
					return $Summary_array;
				}
				return "NULL TODO	";
			}
			return "fail TODO	";

		} catch (Exception $e) {
			echo "Exception".$e;
		}
	}

	/**
	 */
	function findFarmhomeByid($id,$count){
		$sql = "SELECT
		destination.name ,
		destination.lat,
		destination.lng,
		destination.address,
		destination.pic ,
		destination.tel,
		destination.phone,
		destination.hot,
		destination.price,
		destination.price_info,
		destination.score,
		destination.characteristic ,
		destination.type,
		relation_view_farmhome.view_id ,
		relation_view_farmhome.farmhome_id ,
		relation_view_farmhome.type ,
		relation_view_farmhome.distance
		FROM travel.relation_view_farmhome,  travel.destination
		where  destination.destination_id = relation_view_farmhome.view_id
		and relation_view_farmhome.view_id = ".$id.
		"	order by relation_view_farmhome.type asc ".
		"	limit ".
		10*($count-1).
		"	,	10 	;";
		try {
			$result = $this->execute($sql);
			if ($result) {
				if($result->num_rows>0){
					$farmhomeId_array = array();
					$i=0;
					while($row =$result->fetch_array() ){
							
						$view_id = $row['view_id'];
						$farmhome_id = $row['farmhome_id'];
						$type = $row['type'];
						$distance = $row['distance'];
							
						$name = $row['name'];
						$lat = $row['lat'];
						$lng = $row['lng'];
						$address = $row['address'];
						$pic = $row['pic'];
						$tel = $row['tel'];
						$phone = $row['phone'];
						$hot = $row['hot'];
						$price = $row['price'];
						$priceInfo = $row['price_info'];
						$score = $row['score'];
						$distance = 0;
						$characteristic = $row['characteristic'];
						$type = $row['type'];
						$summary =new Summary($farmhome_id,$name,$lat,$lng,$address,$pic,$tel,$phone,$hot,$price,$priceInfo,$score,$distance,$characteristic,$type);
						$view2farmhome =new View2farmhome($view_id,$farmhome_id,$type,$distance,$summary);
						$farmhomeId_array[] =$view2farmhome ;
					}
					return $farmhomeId_array;
				}
				return "NULL TODO	";
			}
			return "fail TODO	";
		}catch (Exception $e) {
			echo "Exception".$e;
		}
	}





}