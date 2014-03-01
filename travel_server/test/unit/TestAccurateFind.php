<?php
require_once "../../vendor/HttpClient.class.php";
$params = array('regionCode' => 0,
		'classicFlag' => 1,
		'labels' => "0",
		'count' => 10);
$client = new HttpClient("127.0.0.1",80);
$client->setDebug(true);
$pageContents = $client->quickPost("http://localhost/travel_server/web/AccurateFind.php", $params);
echo $pageContents;
