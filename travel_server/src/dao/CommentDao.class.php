<?php

class CommentDao{
	var $destination_id ;
	var $user_id ;
	var $otherSYS_type ;
	var $content ;
	var $comment_time ;
	var $comment_score ;
	
	function __construct($destination_id,$user_id,$content,$otherSYS_type,$comment_time,$comment_score){
		$this->destination_id = $destination_id;
		$this->content = $content;
		$this->user_id = $user_id;
		$this->otherSYS_type = $otherSYS_type;
		$this->comment_time = $comment_time;
		$this->comment_score = $comment_score;
	}
	
}