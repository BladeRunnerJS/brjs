'use strict';

var fell = require('..');
var expect = require('expectations');

describe('fell', function() {
	var origInfoLogger, loggedMessage;

	beforeEach(function() {
		origInfoLogger = console.info;
		console.info = function(message) {
			loggedMessage = message;
		};
	});

	afterEach(function() {
		console.info = origInfoLogger;
	});

	it('logs to the console by default', function() {
		fell.info('@my-message');
		expect(loggedMessage).toContain('@my-message');
	});
});
