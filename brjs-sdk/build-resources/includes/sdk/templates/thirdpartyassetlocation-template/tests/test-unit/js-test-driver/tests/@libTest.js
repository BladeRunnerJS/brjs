ExampleClassTest = TestCase("ExampleClassTest");

var @lib = require("@lib");

ExampleClassTest.prototype.testHelloWorldUtil = function()
{
	assertEquals( "Hello World!", @lib.helloWorldUtil() );
};
