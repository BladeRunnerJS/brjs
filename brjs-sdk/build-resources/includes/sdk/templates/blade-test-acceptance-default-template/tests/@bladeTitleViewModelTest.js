(function(){
	'use strict';
	
	require( 'jasmine' );
	
	var originalConsoleLog = console.log;
	
	var @bladeTitleViewModel = require( '@bladeRequirePrefix/@bladeTitleViewModel' );
	
	describe('@bladeTitle Tests', function() {
	
		beforeEach(function() {
			console.log = jasmine.createSpy("console.log");
		});
	
		afterEach(function() {
			console.log = originalConsoleLog;
		});
	
		it( 'Should log hello on load', function() {
			new @bladeTitleViewModel();
			expect(console.log).toHaveBeenCalledWith('Welcome to your new Blade.');
		});
	
	});
}());