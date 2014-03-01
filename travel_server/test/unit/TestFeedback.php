<?php
require_once "../../vendor/HttpClient.class.php";
$params = array('content' => "test content");
$client = new HttpClient("127.0.0.1",80);
$client->setDebug(true);
$pageContents = $client->quickPost("http://framehomedemo.sinaapp.com/web/Feedback.php", $params);
echo $pageContents;