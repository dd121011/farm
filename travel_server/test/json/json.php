<?php
$arr = array(
		'name' => '陈毅鑫',
		'nick' => '深空',
		'contact' => array(
				'email' => 'shenkong at qq dot com',
				'website' => 'http://www.chinaz.com',
		)
);
$json_string = json_encode($arr);
echo $json_string;
?>w