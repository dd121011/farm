<?php
header("Content-Type:text/html;charset=utf-8");
require_once "../src/util/Tools.class.php";

require_once "../src/vo/Promotions.class.php";
require_once "../src/service/ServicePromotions.class.php";
require_once "../src/dao/PromotionsDao.class.php";
require_once "../src/dao/impl/PromotionsDaoImpl.class.php";
require_once "../src/dao/impl/AbstractDaoImpl.class.php";

isset($_POST['count']) ?$count = $_POST['count']:$count = 1;
echo Promotions($count);

/**
 * @param unknown_type $count
 * @return string
 */
function Promotions($count){
	$tools = new Tools();
	$service = new ServicePromotions($count);
	/* 获取活动数组*/
	$Promotions_array = $service->findPromotions();
	$json_array = array();//数组
	$length = count($Promotions_array);
	for($i = 0 ; $i<$length; $i++ ){
		$Promotions_obj = $Promotions_array[$i];//取对象
		$Promotions_json = $tools->object2Json($Promotions_obj);//对象转json
		$json_array[$i] = $Promotions_json;//json入组
	}
	//将json数组转json
	$res = $tools->jsonArray2Json($json_array);
	/* 返回查询结果  */
	return $res;
}