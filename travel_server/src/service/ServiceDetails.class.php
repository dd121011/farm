<?php

/**
 * @author tianhao
 *
 */
class ServiceDetails{
	var $destinationId;
	var $count;
	
	function __construct($destinationId){
		$this->destinationId = $destinationId;
	}
	
	/*获得目的地详情*/
	function  getDetailsById($destinationId){
		$destinationDaoImpl = new DestinationdaoImpl();
		$tools = new Tools();
		$destinationDao = $destinationDaoImpl->findByid($destinationId);
		
		if($destinationDao instanceof DestinationDao){
			//区域名称
			$region = $tools->transCode2Region($destinationDao->getRegionCode());
			//目的地类型，后台系统用于分类
			$labelsDaoImpl = new LabelsDaoImpl();
			$array_labels = $labelsDaoImpl->findLabelsById($destinationId);
			$labels = $tools->creatLabels($array_labels);
			//图片组
			$pictureDaoImpl = new PictureDaoImpl();
			$pics =  $pictureDaoImpl->findPicsById($destinationId);
			$picsVilla = $pictureDaoImpl->findViallPicsById($destinationId);
			$destination = new Destination($destinationDao, $region, $labels, $pics,$picsVilla);
			return $destination;
		}else{
			/* TODO ERROR*/
		}
	}
	
	/*获得景点农家乐*/
	function getFarmhomeById($destinationId,$count){
		$destinationDaoImpl = new DestinationdaoImpl();
		$farmhomeId_array = $destinationDaoImpl->findFarmhomeByid($destinationId,$count);
		return $farmhomeId_array;
	}
	
	
}
