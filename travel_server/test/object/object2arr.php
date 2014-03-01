<?php
ini_set("max_execution_time", 600); // s 10 分钟
function objectToArray($d) {
	if (is_object($d)) {
		// Gets the properties of the given object
		// with get_object_vars function
		$d = get_object_vars($d);
	}

	if (is_array($d)) {
		/*
		 * Return array converted to object
		* Using __FUNCTION__ (Magic constant)
		* for recursive call
		*/
		return array_map(__FUNCTION__, $d);
	}
	else {
		// Return array
		return $d;
	}
}

function arrayToObject($d) {
	if (is_array($d)) {
		/*
		 * Return array converted to object
		* Using __FUNCTION__ (Magic constant)
		* for recursive call
		*/
		return (object) array_map(__FUNCTION__, $d);
	}
	else {
		// Return object
		return $d;
	}
}

// Useage:
// Create new stdClass Object
$init = new stdClass;
// Add some test data
$init->foo = "Test data";
$init->bar = new stdClass;
$init->bar->baaz = "Testing";
$init->bar->fooz = new stdClass;
$init->bar->fooz->baz = "Testing again";
$init->foox = "Just test";

// Convert array to object and then object back to array
$array = objectToArray($init);
$object = arrayToObject($array);

// Print objects and array
print_r($init);
echo "<br \>";
print_r($array);
echo "<br \>";
print_r($object);