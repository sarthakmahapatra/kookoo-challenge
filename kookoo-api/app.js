/**
 * Module dependencies.
 */

var express = require('express'), http = require('http'), path = require('path')
var route = require('./routes/route');
var db = require('./db/db');

var app = express();

app.set('port', process.env.PORT || 3000);

app.get('/', route.validateCall);
app.post('/register', route.register);

db.initDBConnection();


http.createServer(app).listen(app.get('port'), '0.0.0.0', function() {
	console.log('Express server listening on port ' + app.get('port'));
});

