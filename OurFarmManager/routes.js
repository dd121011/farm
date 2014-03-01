/**
 * Module dependencies.
 */

var login = require('./controllers/login');
var home = require('./controllers/home');
var play = require('./controllers/play');
var line = require('./controllers/line');
var activity = require('./controllers/activity');

module.exports = function (app) {
  //login
  console.log('login.login:' + login.login);
  // login page
  app.get('/', login.login);
  app.post('/', login.login);
  // home page
  app.get('/home', home.index);
  app.post('/home', home.index);
  
  // 景点页面
  app.get('/home/play', play.index);
  app.post('/home/play', play.index);
  //不能用下面的方式，会报错 "Error: ER_BAD_FIELD_ERROR: Unknown column 'scrolload' in 'where clause'"
  //app.get('/home/play/:id', play.id);
  app.get('/home/:id/play', play.id);
  app.post('/home/:id/play', play.id);
  app.get('/home/play/add', play.add);
  app.post('/home/play/add', play.add);
  app.get('/home/play/scrolload', play.scrolload);
  app.post('/home/play/scrolload', play.scrolload);
  app.get('/home/play/edit/:id', play.edit);
  app.post('/home/play/edit/:id', play.edit);
  app.get('/home/play/del/:id', play.del);
  app.post('/home/play/del/:id', play.del);
  app.get('/home/play/name', play.name);
  app.post('/home/play/name', play.name);
  
  //路线页面
  app.get('/home/line', line.index);
  app.post('/home/line', line.index);
  app.get('/home/line/scrolload', line.scrolload);
  app.post('/home/line/scrolload', line.scrolload);
  app.get('/home/line/add', line.add);
  app.post('/home/line/add', line.add);
  app.get('/home/line/edit/:id', line.edit);
  app.post('/home/line/edit/:id', line.edit);
  app.get('/home/line/del/:id', line.del);
  app.post('/home/line/del/:id', line.del);
  app.get('/home/line/:id/summary', line.summary);
  app.post('/home/line/:id/summary', line.summary);
  app.get('/home/line/summary/add', line.summaryAdd);
  app.post('/home/line/summary/add', line.summaryAdd);
  app.get('/home/line/summary/edit', line.summaryEdit);
  app.post('/home/line/summary/edit', line.summaryEdit);
  app.get('/home/line/summary/del', line.summaryDel);
  app.post('/home/line/summary/del', line.summaryDel);
  app.get('/home/line/updatesummary', line.updatesummary);
  app.post('/home/line/updatesummary', line.updatesummary);
  app.get('/home/line/:id/characteristic', line.characteristic);
  app.post('/home/line/:id/characteristic', line.characteristic);
  app.get('/home/line/:id/characteristic/update', line.characteristicUpdate);
  app.post('/home/line/:id/characteristic/update', line.characteristicUpdate);
  
  //活动页面
  app.get('/home/activity', activity.index);
  app.post('/home/activity', activity.index);
  app.get('/home/activity/add', activity.add);
  app.post('/home/activity/add', activity.add);
  app.get('/home/activity/edit', activity.edit);
  app.post('/home/activity/edit', activity.edit);
  app.get('/home/activity/del', activity.del);
  app.post('/home/activity/del', activity.del);
  app.get('/home/activity/play/add', activity.playAdd);
  app.post('/home/activity/play/add', activity.playAdd);
  app.get('/home/activity/play/del', activity.playDel);
  app.post('/home/activity/play/del', activity.playDel);
  
  // user
  //app.get('/user/:name', user.index);
  //app.get('/setting', user.setting);
  //app.post('/setting', user.setting);

  //app.get('/user/:name/topics', user.list_topics);
  //app.post('/user/follow', user.follow);
  //app.post('/user/un_follow', user.un_follow);

  
  app.use(function(err, req, res, next){
	// treat as 404
	if (err.message.indexOf('not found')) {
	  return next();	
	}
	// log it
	console.error(err.stack);
	
	// error page
	res.status(500).render('5xx');
  });
		
  // assume 404 since no middleware responded
  app.use(function(req, res, next){
	res.status(404).render('404', { url: req.originalUrl });
  });

};
