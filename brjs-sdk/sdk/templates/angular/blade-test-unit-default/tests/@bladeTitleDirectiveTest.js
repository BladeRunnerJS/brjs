(function(){
	'use strict';

	var @bladeTitleDirectiveTest = TestCase('@bladeTitleDirectiveTest');
	var @bladeTitleDirective = require('@bladeRequirePrefix/@bladeTitleDirective');

	@bladeTitleDirectiveTest.prototype.testSomething = function() {
		var directive = new @bladeTitleDirective();
		assertEquals('E', directive.restrict);
	};
}());
