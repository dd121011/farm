<?php
require_once "AbstractDaoImpl.class.php";


class CommentDaoImpl extends AbstracDaotImpl{
	
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
	
	function insertComment($destination_id,$user_id,$otherSYS_type,$content,$comment_time,$comment_score){
		$sql = " INSERT INTO `travel`.`comment` (`destination_id`, `user_id`, `otherSYS_type`, `content`,`comment_time`,`comment_score`) VALUES 
		('$destination_id', '$user_id', '$otherSYS_type', '$content','$comment_time','$comment_score');";
		$result = $this->execute($sql);
	}
	
	function GetComment($destination_id,$count){
		$sql = "SELECT * FROM `travel`.`comment` where  destination_id = $destination_id  order by comment_time desc limit 0 , $count ;";
		$result = $this->execute($sql);
		if ($result) {
			if($result->num_rows>0){
				$comment_array = array();
				while($row =$result->fetch_array() ){
					$destination_id = $row['destination_id'];
					$user_id = $row['user_id'];
					$otherSYS_type = $row['otherSYS_type'];
					$content = $row['content'];
					$comment_time = $row['comment_time'];
					$comment_score = $row['comment_score'];
					$comment = new CommentDao($destination_id,$user_id,$content,$otherSYS_type,$comment_time,$comment_score) ;
					$comment_array[] = $comment;
				}
				return  $comment_array ;
			}
		}
	}
	
	
}