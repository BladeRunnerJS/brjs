(function(){
	'use strict';
	
	var ExampleClassTest = TestCase("ExampleClassTest");
	
	var NamedspacedJsLib = require("namedspacedjslib/NamedspacedJsLib");
	
	ExampleClassTest.prototype.testHelloWorldUtil = function()
	{
		//assertEquals( "Hello World!", NamedspacedJsLib.helloWorldUtil() );
	};
}());