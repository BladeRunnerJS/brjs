var ExampleClassTest = TestCase("ExampleClassTest");

var App = require("@aspectRequirePrefix/App");

ExampleClassTest.prototype.testSomething = function()
{
	assertEquals( "hello world!", App.getHello() );
};
