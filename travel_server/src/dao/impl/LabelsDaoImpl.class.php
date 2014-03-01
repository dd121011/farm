<?php
require_once "AbstractDaoImpl.class.php";

/**
 * @author tianhao
 *
 */

class LabelsDaoImpl  extends AbstracDaotImpl{
	
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
	 * 查找目的地的标签，返回标签数组
	 * @param unknown_type $id
	 * @return multitype:unknown |string
	 */
	function findLabelsById($id){
		$sql = "SELECT * FROM `travel`.`relation_destination_label` where destination_id = ".$id."	;";
		try {
			$result = $this->execute($sql);
			if ($result) {
				if($result->num_rows>0){
					$array_labels = array();
					while($row =$result->fetch_array() ){
						$array_labels[]=  $row['label_name'];
					}
					return $array_labels;
				}
				return "NULL TODO	";
			}
			return "fail TODO	";
		}catch (Exception $e) {
			echo "Exception".$e;
		}
	}
}