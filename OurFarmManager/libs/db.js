var config = require('../config').config;

var mysql = require('mysql');

var connection = mysql.createConnection({
  host     : config.dbIp,
  user     : config.dbUser,
  password : config.dbPassword,
  port     : config.dbPort,
  multipleStatements: true
});

var pool  = mysql.createPool({
  host     : config.dbIp,
  user     : config.dbUser,
  password : config.dbPassword,
  port     : config.dbPort
});

exports.dbpool = pool;
exports.connection = connection;

/**
 * Custom format sql 比如： where id = :id;
 */
exports.customformat = function (query, values) {
    if (!values) return query;
    return query.replace(/\:(\w+)/g, function (txt, key) {
	    if (values.hasOwnProperty(key)) {
		    return this.escape(values[key]);
	    }
	    return txt;
    }.bind(this));
};

//测试和实际应用到的表
exports.tables = {
  /*
  destination: 'travel_hunan.destination',
  itinerary: 'travel_hunan.itinerary',
  retelation_itinerary_digraph: 'travel_hunan.retelation_itinerary_digraph',
  domain_destination_type: 'travel_hunan.domain_destination_type'
  */
  destination: 'travel.destination_bak',
  itinerary: 'travel.itinerary_bak',
  activity: 'travel.activity_bak',
  retelation_itinerary_digraph: 'travel.retelation_itinerary_digraph_bak',
  domain_destination_type: 'travel.domain_destination_type',
  relation_destination_activity: 'travel.relation_destination_activity_bak'
};