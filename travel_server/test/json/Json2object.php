<?php
require_once "MObject2json.php";

$lover= new lover("lyj","woman");
$th = new employee("th","man",$lover);
$json =  json_encode(($th));
echo "ok!"."<br \>";
echo "json is :".$json."<br \>";
var_dump(json_decode($json));
$obj = json_decode($json);
echo "name is :".$obj->name."<br \>";
echo "lover's name is :".$obj->lover->name."<br \>";

echo "object 2 json  is :".json_encode(json_decode($json))."<br  \>";
echo "ok!"."<br \>";