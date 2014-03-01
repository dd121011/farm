<?php
echo getdistance(0,0,1,0);

function getdistance($lng1,$lat1,$lng2,$lat2)//根据经纬度计算距离
{
	//将角度转为狐度
	$radLat1=deg2rad($lat1);
	$radLat2=deg2rad($lat2);
	$radLng1=deg2rad($lng1);
	$radLng2=deg2rad($lng2);
	$a=$radLat1-$radLat2;//两纬度之差,纬度<90
	$b=$radLng1-$radLng2;//两经度之差纬度<180
	$s=2*asin(sqrt(pow(sin($a/2),2)+cos($radLat1)*cos($radLat2)*pow(sin($b/2),2)))*6378.137;
	return $s;
}