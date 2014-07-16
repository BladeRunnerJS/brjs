ExampleClassTest = TestCase("ExampleClassTest");

var @lib = require("@libns/@lib");

ExampleClassTest.prototype.testHelloWorldUtil = function()
{
	assertEquals( "Hello World!", @lib.helloWorldUtil() );
};
