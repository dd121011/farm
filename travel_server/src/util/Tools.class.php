<?php

class Tools {
	
	/**
	 *  支持多纬数组
	 * @param unknown_type $d
	 */
	function objec2Array($d) {
		if (is_object($d)) {
			// Gets the properties of the given object
			// with get_object_vars function
			$d = get_object_vars($d);
			
			foreach ($d as $key => $value) {
				//将数组中的对象转换成数组
				if(is_object($value)){
					$value = $this->objec2Array($value);
					$d[$key]=$value;
				}
				//
// 			if(is_array($value)){
// 					foreach($valueas as $key => $value ){
// 						//将数组中的对象转换成数组
// 						if(is_object($value)){
// 							$value = $this->objec2Array($value);
// 							$d[$key]=$value;
// 						}
// 					}
// 				}
				//
			}
		}
		return $d;
	}
	


/**************************************************************
 *
*	使用特定function对数组中所有元素做处理
*	@param	string	&$array		要处理的字符串
*	@param	string	$function	要执行的函数
*	@return boolean	$apply_to_keys_also		是否也应用到key上
*	@access public
*
*************************************************************/
function arrayRecursive(&$array, $function, $apply_to_keys_also = false)
{
	static $recursive_counter = 0;
	if (++$recursive_counter > 1000) {
		die('possible deep recursion attack');
	}
	foreach ($array as $key => $value) {
		if (is_array($value)) {
			$this->arrayRecursive($array[$key], $function, $apply_to_keys_also);
		} else {
			$array[$key] = $function($value);
		}

		if ($apply_to_keys_also && is_string($key)) {
			$new_key = $function($key);
			if ($new_key != $key) {
				$array[$new_key] = $array[$key];
				unset($array[$key]);
			}
		}
	}
	$recursive_counter--;
}

/**************************************************************
 *
*	将数组转换为JSON字符串（兼容中文）
*	@param	array	$array		要转换的数组
*	@return string		转换得到的json字符串
*	@access public
*
*************************************************************/
function array2Json($array) {
	$this->arrayRecursive($array, 'urlencode', true);
	$json = json_encode($array);
	$j =urldecode($json);
	return $j;
}

/**************************************************************
 *
*	将json数组转换为JSON字符串
*	@param	array	$array		要转换的数组
*	@return string		转换得到的json字符串
*	@access public
*
*************************************************************/
function jsonArray2Json($array){
	$length = count($array);
	if($length == 1){
		$res = "[".$array[0];
		$res = $res."]";
	}else if($length > 1){
		$i = 0;
		$res = "[".$array[$i++].",";
		for ($i ; $i <$length-1;$i++){
			$res = $res.$array[$i].",";
		}
		$res = $res.$array[$length-1]."]";
	}
	return $res;
}

/*  */
function object2json($obj){
	$array = $this->objec2Array($obj);
	$json =  $this->array2Json($array) ;
	return $json;
}


function getdistance($lng1,$lat1,$lng2,$lat2)//根据经纬度计算距离
{
	//将角度转为狐度
	$radLat1=deg2rad($lat1);
	$radLat2=deg2rad($lat2);
	$radLng1=deg2rad($lng1);
	$radLng2=deg2rad($lng2);
	$a=$radLat1-$radLat2;//两纬度之差,纬度<90
	$b=$radLng1-$radLng2;//两经度之差纬度<180
	$s=2*asin(sqrt(pow(sin($a/2),2)+cos($radLat1)*cos($radLat2)*pow(sin($b/2),2)))*6378.137;
	return $s;
}

function transCode2Region($code){
	$arr = array(
			"0"=>"非北京市",
			"110105" =>"朝阳区",
			"110106" =>"丰台区",
			"110107" =>"石景山区",
			"110108" =>"海淀区",
			"110109" =>"门头沟区",
			"110111" =>"房山区",
			"110112" =>"通州区",
			"110113" =>"顺义区",
			"110114" =>"昌平区",
			"110115" =>"大兴区",
			"110116" =>"怀柔区",
			"110117" =>"平谷区",
			"110228" =>"密云县",
			"110229" =>"延庆县");
	$region = $arr["$code"];
	return $region;
}

/*
 * 构造标签串，用&分隔*/
function  creatLabels($array_labels){
	if( is_array($array_labels)){
		$count = count($array_labels);
		if($count == 1){
			return $array_labels[0];
		}else{
			$labels = $array_labels[0];
			for($i = 1 ; $i< ($count ) ; $i++){
				$labels .="&"."$array_labels[$i]";
			}
			return $labels;
		}
	}
}

}