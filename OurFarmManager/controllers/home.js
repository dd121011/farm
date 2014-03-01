/**
 * Module dependencies.
 */
var config = require('../config').config;
var User = require('../models/user.js');
var checkLogin = require('../libs/util.js').checkLogin;

exports.index = function (req, res, next) {
  var loginuser = new User({
      name: req.body.username,
      password: req.body.password,
  });
  /*
  var loginuser = req.session.user;
  if(loginuser == null || loginuser == "") {
    console.log(req.body.username);
    
  }
  */
  req.session.user = loginuser;
  //loginuser = req.session.user;
  if(loginuser.name == "jiaoyouke" && loginuser.password == "f4321") {
    res.render('index', {title: "首页"});
  } else {
    req.session.user = null;
    res.render('login', {
	  title: "登陆",
	  errmsg: "用户名或者秘密错误"
	});
  }
};
