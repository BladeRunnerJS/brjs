(function() {
	'use strict';
	
	require( 'jasmine' );
	
	var originalConsoleLog = console.log;
	
	var App = require("appx/App");
	
	describe('App Tests', function() {
	
		beforeEach(function() {
			console.log = jasmine.createSpy("console.log");
		});
	
		afterEach(function() {
			console.log = originalConsoleLog;
		});
	
		it( 'Should say hello', function() {
			App.logHello();
			expect(console.log).toHaveBeenCalledWith('hello world!');
		});
	
	});
}());