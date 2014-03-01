<?php
require_once "../../vendor/HttpClient.class.php";
$params = array('count' => 2);
$client = new HttpClient("127.0.0.1",80);
$client->setDebug(true);
$pageContents = $client->quickPost("http://localhost/travel_server/web/Activity.php", $params);
echo $pageContents;