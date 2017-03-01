(function() {
	'use strict';
	
	require( 'jasmine' );
	
	var originalConsoleLog = console.log;
	
	var App = require("itapp/App");
	
	describe('App Tests', function() {
	
		beforeEach(function() {
			console.log = jasmine.createSpy("console.log");
		});
	
		afterEach(function() {
			console.log = originalConsoleLog;
		});
	
		it( 'Should say hello', function() {
		});
	
	});
}());