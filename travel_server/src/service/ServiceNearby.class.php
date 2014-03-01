<?php



class ServiceNearby{
	var $lat ; //用户在地图上标点的坐标
	var $lng ;
	var $distance ;//按照距离检索，单位为km，默认距离为5km，
	var $count ; //返回的记录条数，默认为20。
	var $type;//查询种类，1游玩，2美食，3住宿，4特产
	var $pdo;
	var $res ;//json格式的查询结果，
	
	function __construct($lat ,$lng, $distance, $count,$type){
		$this->lat = $lat;
		$this->lng = $lng;
		$this->distance = $distance;
		$this->count = $count;
		$this->type = $type;
	}
	
	/* 通过service获取查询结果 
	 * 返回 对象summary的数组	*/
	function findNearby(){
		$destinationDaoImpl = new DestinationdaoImpl();
		$tools = new Tools();
		
		$half_distance = $this->distance/2;
	
		$params = array('lat'=>$this->lat ,'lng'=>$this->lng,'distance'=>$this->distance,'count'=>$this->count,'type'=>$this->type);
		$sql = $destinationDaoImpl->creatNearbySql($params);
// 		$sql = "SELECT destination_id ,name , lat, lng, address,pic ,tel, phone, hot, price, price_info, score, characteristic  FROM travel.destination where 10<lat<40 and 100<lng<150;";
		
		$Summary_array = $destinationDaoImpl->findSummarys($sql);
		$Summary_array_temp = array();//临时数组，存放过滤后的Summary对象
		$j = 0;//初始化临时数组下标，
		$length = count($Summary_array);
		for($i = 0 ; $i<$length; $i++ ){
			$summary = $Summary_array[$i];//取对象
			//计算实际距离
			$d_lat = $summary->getLat();
			$d_lng = $summary->getLng();
			$distance = $tools->getdistance($this->lng, $this->lat, $d_lng, $d_lat);
			$summary->setDistance($distance);//写入实际距离
			$Summary_array_temp [$j++] = $summary;
		}
		return $Summary_array_temp;
	}
	
}