ExampleClassTest = TestCase("ExampleClassTest");

var Testlib = require("testlib/Testlib");

ExampleClassTest.prototype.testHelloWorldUtil = function()
{
	assertEquals( "Hello World!", Testlib.helloWorldUtil() );
};
