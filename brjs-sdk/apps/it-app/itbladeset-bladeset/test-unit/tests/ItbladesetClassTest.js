(function(){
	'use strict';
	
	var ExampleClassTest = TestCase("ExampleClassTest");
	
	var ItbladesetClass = require("itapp/itbladeset/ItbladesetClass");
	
	ExampleClassTest.prototype.testSomething = function()
	{
		assertEquals("hello", new ItbladesetClass().sayHello());
	};
}());