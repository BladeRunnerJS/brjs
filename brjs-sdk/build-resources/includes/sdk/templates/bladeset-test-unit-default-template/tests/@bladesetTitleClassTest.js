(function(){
	'use strict';
	
	var ExampleClassTest = TestCase("ExampleClassTest");
	
	var @bladesetTitleClass = require("@bladesetRequirePrefix/@bladesetTitleClass");
	
	ExampleClassTest.prototype.testSomething = function()
	{
		assertEquals("hello", new @bladesetTitleClass().sayHello());
	};
}());