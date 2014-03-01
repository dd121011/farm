<?php
require_once "HttpClient.class.php";
$params = array('name' => "getNear",);
		$client = new HttpClient("127.0.0.1",80);
		$pageContents = $client->quickPost("http://172.20.50.158/testPHP/basicTest/httpClient/server.php", $params);
		echo $pageContents;
// 		$result = explode(".", $pageContents);
// 		print_r($result);