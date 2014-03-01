/**
 * Module dependencies.
 */
var config = require('../config').config;

exports.login = function (req, res, next) {
  res.render('login', {
    title: "登陆",
	errmsg: ""
  });
	/*
	res.render('users', {
		  users: users,
		  title: "EJS example",
		  header: "Some users"
	});
	*/
};
