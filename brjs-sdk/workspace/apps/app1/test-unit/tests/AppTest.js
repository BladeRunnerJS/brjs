(function() {
	'use strict';
	
	var AppTest = TestCase("AppTest");
	
	var App = require("appx/App");
	
	AppTest.prototype.testSomething = function() {
		assertEquals( "hello world!", App.getHello() );
	};
}());