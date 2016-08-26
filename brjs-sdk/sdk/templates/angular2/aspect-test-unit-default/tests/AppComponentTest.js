(function() {
	'use strict';

	var AppTest = TestCase('AppTest');
	var AppComponent = require('@aspectRequirePrefix/AppComponent');

	AppTest.prototype.testSomething = function() {
		assertEquals('hello world!', AppComponent.getHello());
	};
}());
