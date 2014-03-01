<?php

/**
 * @author tianhao
 *
 */
Abstract class AbstracDaotImpl{

	//连接参数
	var $db_servername;
	var $db_user ;
	var $db_password ;
	var $db_database ;

	function __construct(){
		/* TODO construct PDO*/
		//连接参数
		$this->db_servername = "172.20.6.19:3306";
		$this->db_user = "root";
		$this->db_password = "123";
		$this->db_database = "travel";
	}

	Abstract public function insert($obj);
	Abstract public function delete($id);
	Abstract public function sava($obj);
	Abstract public function update($obj);
	Abstract public function findByid($id);
	
	
	

	public function  execute($sql){
		try {
			$mysqli = new mysqli($this->db_servername, $this->db_user, $this->db_password,$this->db_database);
			$result = $mysqli->query($sql);
		} catch (Exception $e) {
			return "eception TODO	";
		}
		$mysqli->close();
		return $result;
	}

}