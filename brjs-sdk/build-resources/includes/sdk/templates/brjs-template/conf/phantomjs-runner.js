/*
 * This PhantomJS runner is provided to make it easy to run tests on PhantomJS using BladeRunnerJS.
 * To use PhantomJS download the relevant binary from http://phantomjs.org and update the path in test-runner.conf
*/

var page = require('webpage').create();
page.settings.userAgent = 'PhantomJS';
var args = require('system').args;
var url = args[1];
var captureAttempts = 0;
var captured = false;
var locked = false;

var log = function(str) {
	var dt = new Date();
	console.log(dt.toString() + ': ' + str);
};

var pageLoaded = function(status) {
	log('Finished loading ' + url + ' with status: ' + status);

	var runnerFrame = page.evaluate(function() {
		return document.getElementById('runner');
	});

	if (!runnerFrame) {
		locked = false;
		setTimeout(capture, 1000);
	} else {
		captured = true;
	}
};

var capture = function() {
	if (captureAttempts === 5) {
		log('Failed to capture JSTD after ' + captureAttempts + ' attempts.');
		phantom.exit();
	}

	if (captured || locked) {
		return;
	}

	captureAttempts += 1;
	locked = true;

	log('Attempting (' + captureAttempts + ') to load: ' + url);
	page.open(url, pageLoaded);
};

capture();
