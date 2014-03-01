<?php
require_once "AbstractDaoImpl.class.php";

/**
 * @author tianhao
 *
 */

class PictureDaoImpl extends AbstracDaotImpl{
	
	function insert($obj){
	}
	
	function delete($id){
	}
	
	function sava($obj){
	}
	
	function update($obj){
	}
	
	function findByid($id){}
	
	/**
	 * 查找目的地的实景图片，返回图片链接地址数组
	 * @param unknown_type $id
	 */
	function findPicsById($id){
		$sql = "SELECT url FROM `travel`.`picture` where destination_id = ".$id." and type =2	;";
		try {
			$result = $this->execute($sql);
			if ($result) {
				if($result->num_rows>0){
					$array_urls = array();
					while($row =$result->fetch_array() ){
						$array_urls[]=  $row['url'];
					}
					return $array_urls;
				}
				return "NULL TODO	";
			}
			return "fail TODO	";
		}catch (Exception $e) {
			echo "Exception".$e;
		}
	}
	
	/**
	 * 查找目的地的实景图片，返回图片链接地址数组
	 * @param unknown_type $id
	 */
	function findViallPicsById($id){
		/**tyep = 3 为吃，4为玩*/
		$sql = "SELECT * FROM `picture` where destination_id = ".$id."	and type = 3 or type = 4 ;";
		try {
			$result = $this->execute($sql);
			if ($result) {
				if($result->num_rows>0){
					$pics =  array();
					$tools = new Tools();
					while($row =$result->fetch_array() ){
						$url=  $row['url'];
						$type=  $row['type'];
						$content=  $row['content'];
						$picsvilla = new VillaPic($type, $content, $url);
						$pics[] = $tools->object2json($picsvilla);
					}
					return $pics;
				}
				return "NULL TODO	";
			}
			return "fail TODO	";
		}catch (Exception $e) {
			echo "Exception".$e;
		}
	}
}