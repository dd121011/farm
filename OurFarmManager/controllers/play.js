
var config = require('../config').config;
var db = require('../libs/db');
var dbconnection = db.connection, dbpool = db.dbpool, customformat = db.customformat;
var tables = require('../libs/db').tables;
var destination = tables.destination;
var checkLogin = require('../libs/util').checkLogin;
var destinationPojo = require('../models/destination');

exports.index = function (req, res, next) {
	//检测是否已经登录
    checkLogin(null, req, res);
	dbpool.getConnection(function(err, connection) {
	  connection.query('SELECT * from ' + destination +' limit 0, 20', function(err, rows, fields) {
	    if (err) throw err;
	    res.render('nearby/play', {
	    	  title: "景点",
	    	  errmsg: "",
	    	  results: rows
	      });
	    connection.end();
      });
    });
};

// 通过ID查询
exports.id = function (req, res, next) {
  checkLogin(null, req, res);
  var id = req.params.id;
  var sql = "SELECT * from " + destination +" des where des.destination_id =" + id;
  dbpool.getConnection(function(err, connection) {
	  connection.query(sql, function(err, rows, fields) {
	    if (err) throw err;
	    if(rows.length <=0 ) {
	    	connection.end();
	    	res.json([]);
	    	console.log("没有查询到景点数据");
	    } else {
	    	res.json(rows);
	    }
	    connection.end();
    });
  });
};
// 通过名字查询
exports.name = function (req, res, next) {
  checkLogin(null, req, res);
  var inputName = req.body.inputName;
  var sql = "SELECT * from " + destination + " des where des.name like '%" + inputName + "%'";
  dbpool.getConnection(function(err, connection) {
	  connection.query(sql, function(err, rows, fields) {
	    if (err) throw err;
	    if(rows.length <=0 ) {
	    	connection.end();
	    	res.json([]);
	    	console.log("没有查询到数据");
	    } else {
	    	res.json(rows);
	    }
	    connection.end();
    });
  });
};

//下来框到达底端的时候
exports.scrolload = function (req, res, next) {
	checkLogin(null, req, res);
	var loadPage = isNaN(req.body.loadPage) ? 0 : req.body.loadPage;	
	var loadCount = isNaN(req.body.loadCount) ? 0 : req.body.loadCount;
	var inputName = req.body.inputName;
	var inputDescribe = req.body.inputDescribe;
	var inputMinId = isNaN(req.body.inputMinId) ? 0 : req.body.inputMinId;
	var inputMaxId = isNaN(req.body.inputMaxId) ? 20 : req.body.inputMaxId;
	
	console.log(loadPage + ":" + loadCount + ":" + inputName + ":" + inputDescribe + ":" + inputMinId + ":" + inputMaxId);
	
	var from = loadPage * 100 + (loadCount - 0);
	
	var sql = 'SELECT * from ' + destination + ' des where 1=1';
	if(inputName != null && inputName.length > 0) {
		sql += " and des.name like '%" + inputName + "%'";
	}
	if(inputDescribe != null && inputDescribe.length > 0) {
		sql += " and des.introduction like '%" + inputDescribe + "%'";
	}
	if(inputMinId != null && inputMinId > 0) {
		sql += " and des.destination_id > " + (inputMinId - 0 - 1) ;
	}
	if(inputMaxId != null && inputMaxId > 0) {
		sql += " and des.destination_id < " + (inputMaxId - 0 + 1) ;
	}
	sql += " limit " + from + ", 20 ";
	
	console.log(sql);
	
	dbpool.getConnection(function(err, connection) {
	  // Use the connection
		connection.query(sql, function(err, rows, fields) {
			console.log(rows.length);
		    if (err) throw err;
		    if(rows.length <=0 ) {
		    	//res.json({});
		    	res.render('/home/play', 
		    		  { 
		    		    title: "景点",
			    	    errmsg: "",
			    	    results: []
			    	  });
		    	console.log("没有查询到数据");
		    } else {
		    	res.json(rows);
		    }
		    connection.end();
	    });
    });
	
};

//增加景点
exports.add = function (req, res, next) {
	checkLogin(null, req, res);
	var sql = "insert into " + destination + " (name,lat,lng,price,address,tel,introduction," + 
	             "pic,phone,other_contact,score,hot,region_code,price_info,car,bus," + 
	             "bike,characteristic,classic_flag,map_pic,preferential_info,type) " +
	            " values( '" + req.body.name + "'," +
	            req.body.lat + "," +
	            req.body.lng + "," +
	            req.body.price + "," +
	            "'" + req.body.address + "'," +
	            "'" + req.body.tel + "'," +
	            "'" + req.body.introduction + "'," +
	            "'" + req.body.pic + "'," +
	            "'" + req.body.phone + "'," +
	            "'" + req.body.other_contact + "'," +
	            req.body.score + "," +
	            req.body.hot + "," +
	            req.body.region_code + "," +
	            "'" + req.body.price_info + "'," +
	            "'" + req.body.car + "'," +
	            "'" + req.body.bus + "'," +
	            "'" + req.body.bike + "'," +
	            "'" + req.body.characteristic + "'," +
	            req.body.classic_flag + "," +
	            "'" + req.body.map_pic + "'," +
	            "'" + req.body.preferential_info + "'," +
	            req.body.type + ")";
		  
	  console.log(sql);
	  
	  dbpool.getConnection(function(err, connection) {

		connection.query(sql, {}, 
		  function(err, result) {
		    if (err){
			  console.log(err);
			  res.json({'result': "error"});;
		    } else {
			  res.json({'result': "success",'insertId': result.insertId});;
		    }

		    console.log("insert play commit");
		    connection.end();
		});
	  });	
};

//编辑景点
exports.edit = function (req, res, next) {
  checkLogin(null, req, res);
  var destination_id = req.params.id;
  var sql = 'update ' + destination +
            ' set name = :name, ' +
            '   lat = :lat, ' +
            '   lng = :lng, ' +
            '   hot = :hot, ' +
            '   price = :price, ' +
            '   address = :address, ' +
            '   tel = :tel, ' +
            '   introduction = :introduction, ' +
            '   preferential_info = :preferential_info, ' +
            '   pic = :pic, ' +
            '   phone = :phone, ' +
            '   other_contact = :other_contact, ' +
            '   score = :score, ' +
            '   hot = :hot, ' +
            '   region_code = :region_code, ' +
            '   price_info = :price_info, ' +
            '   car = :car, ' +
            '   bus = :bus, ' +
            '   bike = :bike, ' +
            '   characteristic = :characteristic, ' +
            '   classic_flag = :classic_flag, ' +
            '   map_pic = :map_pic, ' +
            '   preferential_info = :preferential_info, ' +
            '   type = :type ' +
            ' where destination_id = :destination_id ';
	  
  dbpool.getConnection(function(err, connection) {
	// Use the connection
	connection.config.queryFormat = customformat;

	var params = getParams(req);
	params.destination_id = destination_id;
	
	connection.query(sql, params, function(err, result) {
		  if (err){
			  res.json("error");;
		  } else {
			  res.json("success");;
		  }

		  console.log("update commit");
		  connection.end();
	  });
  });
};

//删除景点
exports.del = function (req, res, next) {
  checkLogin(null, req, res);
  var id = req.params.id;
  var sql = 'delete from ' + destination + 
  ' where destination_id = :destination_id ';
  
  dbpool.getConnection(function(err, connection) {
	  // Use the connection
	  connection.config.queryFormat = customformat;
	  
	  connection.query(sql, {destination_id: id}, 
	    function(err, result) {
		  if (err){
			  res.json("error");;
		  } else {
			  res.json("success");;
		  }
		  
		  console.log("delete commit");
		  connection.end();
	    });
  });
};

/**
 * 解析传输过来的内容，返回对象
 */
function getParams(req) {
  var params = {};
  var des = destinationPojo.destination;
  for(var d in des) {
	params[d] = req.body[d];  
  }
  
  return params;
}

