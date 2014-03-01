<?php
require_once "../../vendor/HttpClient.class.php";
$params = array('lat' => 39.6692,'lng' => 115.448,'type'=>'4');
$client = new HttpClient("127.0.0.1",80);
$client->setDebug(true);
$pageContents = $client->quickPost("http://framehomedemo.sinaapp.com/web/Nearby.php", $params);
echo $pageContents;