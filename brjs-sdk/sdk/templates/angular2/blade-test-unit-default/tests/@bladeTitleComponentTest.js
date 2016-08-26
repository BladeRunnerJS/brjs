(function(){
	'use strict';

	var @bladeTitleComponentTest = TestCase('@bladeTitleComponentTest');
	var @bladeTitleComponent = require('@bladeRequirePrefix/@bladeTitleComponent');

	@bladeTitleComponentTest.prototype.testSomething = function() {
		var component = new @bladeTitleComponent();
		assertEquals( 'Welcome to your new Blade.', component.welcomeMessage );
	};
}());
