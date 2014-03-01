var path = require('path');
var express = require("express");
var config = require('./config').config;
var routes = require('./routes');
var setLocals = require('./locals').setLocals;

var app = module.exports = express();

app.engine('.html', require('ejs').__express);
app.use(express.static(__dirname + '/public'));
app.set('views', __dirname + '/views');
app.set('view engine', 'html');
  
//define a custom res.message() method
//which stores messages in the session
app.response.message = function(msg){
  // reference `req.session` via the `this.req` reference
  var sess = this.req.session;
  // simply add the msg to an array for later
  sess.messages = sess.messages || [];
  sess.messages.push(msg);
  return this;
};

app.use(express.logger());
app.use(express.bodyParser());
app.use(express.cookieParser());
app.use(express.session({
  secret: config.session_secret
}));
app.use(setLocals);
  
var staticDir = path.join(__dirname, 'public');
app.configure('development', function () {
  app.use(express.static(staticDir));
  app.use(express.errorHandler({ dumpExceptions: true, showStack: true })); 
});

app.configure('production', function () {
  //app.use(express.static(staticDir, { maxAge: maxAge }));
  //app.use(express.errorHandler()); 
  //app.set('view cache', true);
});

//routes
routes(app);

if (!module.parent) {
	app.listen(config.port);
	console.log('郊游客管理端已启动,访问端口: 3000');
}