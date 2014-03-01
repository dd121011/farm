<?php
require_once "../../vendor/HttpClient.class.php";
$params = array('name' => "野三坡");
$client = new HttpClient("127.0.0.1",80);
$client->setDebug(true);
$pageContents = $client->quickPost("http://framehomedemo.sinaapp.com/web/HotTop.php", $params);
echo $pageContents;