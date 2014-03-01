
var config = require('../config').config;
var db = require('../libs/db');
var dbconnection = db.connection, dbpool = db.dbpool, customformat = db.customformat;
var tables = require('../libs/db').tables;
var itinerary = tables.itinerary,
    retelation_itinerary_digraph= tables.retelation_itinerary_digraph,
    domain_destination_type= tables.domain_destination_type;
var checkLogin = require('../libs/util').checkLogin;

exports.index = function (req, res, next) {
	//检测是否已经登录
    checkLogin(null, req, res);
	var pool = dbpool;
	
	pool.getConnection(function(err, connection) {
	  // Use the connection
	  connection.query('SELECT * from ' + itinerary + ' limit 0, 20', function(err, rows, fields) {
	    if (err) throw err;
	    res.render('line/main', {
	    	  title: "线路",
	    	  errmsg: "",
	    	  results: rows
	    });
	    connection.end();
      });
    });
	
};

//增加线路
exports.add = function (req, res, next) {
	checkLogin(null, req, res);
	var sql = "insert into " + itinerary + " (name,classic_flag,score,hot,pic,price,price_info) " +
	            " values( '" + req.body.edit_name + "'," +
	            req.body.edit_classic_flag + "," +
	            req.body.edit_score + "," +
	            req.body.edit_hot + "," +
	            "'" + req.body.edit_pic + "'," +
	            req.body.edit_price + "," +
	            "'" + req.body.edit_price_info + "')";
		  
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

		    console.log("insert line commit");
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
	var inputSummary = req.body.inputSummary;
	var inputMinId = isNaN(req.body.inputMinId) ? 0 : req.body.inputMinId;
	var inputMaxId = isNaN(req.body.inputMaxId) ? 20 : req.body.inputMaxId;
	
	console.log(loadPage + ":" + loadCount + ":" + inputName + ":" + inputSummary + ":" + inputMinId + ":" + inputMaxId);
	
	var from = loadPage * 100 + (loadCount - 0);
	
	var sql = 'SELECT * from ' + itinerary + ' line where 1=1';
	if(inputName != null && inputName.length > 0) {
		sql += " and line.name like '%" + inputName + "%'";
	}
	if(inputSummary != null && inputSummary.length > 0) {
		sql += " and line.itinerary_summary like '%" + inputSummary + "%'";
	}
	if(inputMinId != null && inputMinId > 0) {
		sql += " and line.itinerary_id > " + (inputMinId - 0 - 1) ;
	}
	if(inputMaxId != null && inputMaxId > 0) {
		sql += " and line.itinerary_id < " + (inputMaxId - 0 + 1) ;
	}
	sql += " limit " + from + ", 20 ";
	
	console.log(sql);
	
	var pool = dbpool;
	pool.getConnection(function(err, connection) {
	  // Use the connection
		connection.query(sql, function(err, rows, fields) {
			console.log(rows.length);
		    if (err) throw err;
		    if(rows.length <=0 ) {
		    	res.render('/home/line', 
		    		  { 
		    		    title: "路线",
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
	
}

//更新路线中的itinerary_summary字段
exports.updatesummary = function (req, res, next) {
  checkLogin(null, req, res);
  var sql = 'update ' + itinerary +
            ' set itinerary_summary = :itinerary_summary ' +
            ' where itinerary_id = :itinerary_id ';
  
  console.log(sql);
	  
  var pool = dbpool;
  pool.getConnection(function(err, connection) {
	connection.config.queryFormat = customformat;

	connection.query(sql, 
	  { 
		itinerary_summary: req.body.itinerary_summary, 
		itinerary_id: req.body.itinerary_id
	  }, function(err, result) {
		  if (err){
			  console.log(err);
			  res.json("error");;
		  } else {
			  res.json("success");;
		  }

		  console.log("update summary in itinerary commit");
		  connection.end();
	  });
  });
}

//编辑景点
exports.edit = function (req, res, next) {
  checkLogin(null, req, res);
  var id = req.params.id;
  var sql = 'update ' + itinerary +
            ' set name = :name, ' +
            '   classic_flag = :classic_flag, ' +
            '   score = :score, ' +
            '   hot = :hot, ' +
            '   pic = :pic, ' +
            '   price = :price, ' +
            '   price_info = :price_info ' +
            ' where itinerary_id = :itinerary_id ';
	  
  //var pool = db().conn;
  var pool = dbpool;
  pool.getConnection(function(err, connection) {
	// Use the connection
	connection.config.queryFormat = customformat;

    console.log(req.body.edit_name);
	connection.query(sql, 
	  { 
		name: req.body.edit_name, 
		classic_flag: req.body.edit_classic_flag, 
		score: req.body.edit_score, 
		hot: req.body.edit_hot, 
		pic: req.body.edit_pic, 
		price: req.body.edit_price, 
		price_info: req.body.edit_price_info, 
		itinerary_id: id
	  }, function(err, result) {
		  if (err){
			  console.log(err);
			  res.json("error");;
		  } else {
			  res.json("success");;
		  }

		  console.log("update commit");
		  connection.end();
	  });
  });
}

//删除路线
exports.del = function (req, res, next) {
  checkLogin(null, req, res);
  var id = req.params.id;
  
  var sql = 'delete from ' + itinerary +
    ' where itinerary_id = ' + id;

  //删除路线概要
  var deleteSummarySql = 'delete from ' + retelation_itinerary_digraph +
    ' where itinerary_id = ' + id;

  dbconnection.query(sql + ";" + deleteSummarySql, function(err, results) {
	  if (err) throw err;
	  if (err){
		  res.json("error");;
	  } else {
		  res.json("success");;
	  }
	  // `results` is an array with one element for every statement in the query:
	  // console.log(results[0]); // [{1: 1}]
	  // console.log(results[1]); // [{2: 2}]
	});
  
}

//进入线路概要编辑页面
exports.summary = function (req, res, next) {
	//检测是否已经登录
    checkLogin(null, req, res);
    var id = req.params.id;
	var pool = dbpool;
	var sql = 'SELECT * FROM ' + retelation_itinerary_digraph +
	  ' where itinerary_id = ' + id +
	  ' order by step_num ';
	
	pool.getConnection(function(err, connection) {
	  connection.query(sql, function(err, rows, fields) {
	    if (err) throw err;
	    res.render('line/summary', {
	    	  title: "线路概要",
	    	  errmsg: "",
	    	  results: {'id':id, 'rows':rows}
	    });
	    connection.end();
      });
    });
	
};

//增加线路概要
exports.summaryAdd = function (req, res, next) {
	checkLogin(null, req, res);
	var sql = "insert into " + retelation_itinerary_digraph +
	            " values( " + req.body.itinerary_id + "," +
	            req.body.step_num + "," +
	            req.body.start_destination_id + "," +
	            "'" + req.body.start_destination_name + "'," +
	            req.body.end_destination_id + "," +
	            "'" + req.body.end_destination_name + "'," +
	            "'" + req.body.line_content + "'," +
	            "'" + req.body.content + "')";
		  
	  //console.log(sql);
	  
	  var pool = dbpool;
	  pool.getConnection(function(err, connection) {

		connection.query(sql, {}, 
		  function(err, result) {
		    if (err){
			  console.log(err);
			  res.json("error");;
		    } else {
			  res.json("success");;
		    }

		    console.log("insert summary commit");
		    connection.end();
		});
	  });	
};

//编辑线路概要
exports.summaryEdit = function (req, res, next) {
	checkLogin(null, req, res);
	  var sql = 'update ' + retelation_itinerary_digraph +
	            ' set start_destination_id = :start_destination_id, ' +
	            '   start_destination_name = :start_destination_name, ' +
	            '   end_destination_id = :end_destination_id, ' +
	            '   end_destination_name = :end_destination_name, ' +
	            '   line_content = :line_content, ' +
	            '   content = :content ' +
	            ' where itinerary_id = :itinerary_id ' +
	            '  and step_num = :step_num ' ;
		  
	  console.log(sql);
	  
	  var pool = dbpool;
	  pool.getConnection(function(err, connection) {
		connection.config.queryFormat = customformat;

		connection.query(sql, 
		  { 
			start_destination_id: req.body.start_destination_id, 
			start_destination_name: req.body.start_destination_name, 
			end_destination_id: req.body.end_destination_id, 
			end_destination_name: req.body.end_destination_name, 
			line_content: req.body.line_content, 
			content: req.body.content, 
			itinerary_id: req.body.itinerary_id, 
			step_num: req.body.step_num
		  }, function(err, result) {
			  if (err){
				console.log(err);
				res.json("error");;
			  } else {
				res.json("success");;
			  }

			  console.log("update summary commit");
			  connection.end();
		  });
	  });	
};

//删除线路概要
exports.summaryDel = function (req, res, next) {
  checkLogin(null, req, res);
  var sql = 'delete from ' + retelation_itinerary_digraph +
    ' where itinerary_id = :itinerary_id ' +
    '  and step_num = :step_num ';
  
  var pool = dbpool;
  pool.getConnection(function(err, connection) {
	  connection.config.queryFormat = customformat;
	  
	  connection.query(sql, {itinerary_id: req.body.itinerary_id, step_num: req.body.step_num}, 
	    function(err, result) {
		  if (err){
			  res.json("error");;
		  } else {
			  res.json("success");;
		  }
		  
		  console.log("delete summary commit");
		  connection.end();
		}
	  );
  });	
};

//查询线路经典特色内容
exports.characteristic = function (req, res, next) {
  //检测是否已经登录
  checkLogin(null, req, res);
  var sql = 'SELECT * FROM ' + domain_destination_type;
	
  dbpool.getConnection(function(err, connection) {
	connection.query(sql, function(err, rows, fields) {
	  if (err) throw err;
	  if(rows.length <=0 ) {
	  	connection.end();
	   	res.json([]);
	   	console.log("没有查询到'目的地所具有的属性'数据");
	  } else {
	   	res.json(rows);
	  }
	  connection.end();
    });
  });
	
};

//编辑经典/特色信息
exports.characteristicUpdate = function (req, res, next) {
  checkLogin(null, req, res);
  var id = req.params.id;
  var sql = 'update ' + itinerary +
            ' set characteristic = :characteristic ' +
            ' where itinerary_id = :itinerary_id ';
	  
  dbpool.getConnection(function(err, connection) {
	// Use the connection
	connection.config.queryFormat = customformat;

	connection.query(sql, 
	  { 
		characteristic: req.body.characteristic, 
		itinerary_id: id
	  }, function(err, result) {
		  if (err){
			  res.json("error");;
		  } else {
			  res.json("success");;
		  }

		  console.log("update characteristic commit");
		  connection.end();
	  });
  });
};

