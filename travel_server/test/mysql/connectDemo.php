 <?php
//连接参数
	$db_servername = "172.20.50.19:3306";
	$db_user = "root";
	$db_password = "123";
	$db_database = "travel";
	$sql ="SELECT * FROM `travel`.`destination`;";
	
	//禁止输出错误信息
	error_reporting(0);
	
	//连接数据库	
	$link = mysql_connect($db_servername, $db_user, $db_password);
	if(!$link) 
	{ 
		echo "连接MySQL:false"; 
	}
	else
	{
		echo "连接MySQL: success "."<br \>";
		
	}
	

    ?>
    