(function(){
	'use strict';
	
	require( 'jasmine' );
	
	var originalConsoleLog = console.log;
	
	var ItbladeViewModel = require( 'itapp/itbladeset/itblade/ItbladeViewModel' );
	
	describe('Itblade Tests', function() {
	
		beforeEach(function() {
			console.log = jasmine.createSpy("console.log");
		});
	
		afterEach(function() {
			console.log = originalConsoleLog;
		});
	
		it( 'Should log hello on load', function() {
			new ItbladeViewModel();
		});
	
	});
}());