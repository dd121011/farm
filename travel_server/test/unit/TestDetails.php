<?php
require_once "../../vendor/HttpClient.class.php";
$params = array('destinationId' => 4);
$client = new HttpClient("127.0.0.1",80);
$client->setDebug(true);
$pageContents = $client->quickPost("http://localhost/travel_server/web/Details.php", $params);
echo $pageContents;