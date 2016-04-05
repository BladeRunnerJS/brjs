(function(){
	'use strict';

	require( 'jasmine' );

	var originalConsoleLog = console.log;
	var @bladeTitleComponent = require('@bladeRequirePrefix/@bladeTitleComponent');


	describe('@bladeTitle Tests', function() {

		beforeEach(function() {
			console.log = jasmine.createSpy('console.log');
		});

		afterEach(function() {
			console.log = originalConsoleLog;
		});

		it('Should log hello on load', function() {
			new @bladeTitleComponent();
			expect(console.log).toHaveBeenCalledWith('Welcome to your new Blade.');
		});

	});
}());
