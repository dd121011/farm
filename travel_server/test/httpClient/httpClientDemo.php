<?php
	require_once "HttpClient.class.php";
	$client = new HttpClient("www.amazon.com");
	$client->setDebug(true);
	if (!$client->get("/")) {
		echo "<p>Request failed!</p> ";
	} else {
		echo "<p>Amazon home page is ".strlen($client->getContent())." bytes.</p>";
	}
?>