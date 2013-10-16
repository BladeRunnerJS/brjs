GreeterTest = TestCase("GreeterTest");

GreeterTest.prototype.testGreet = function() {
  var greeter = new myApp.Greeter();
  assertEquals("Hello World!", greeter.greet("World"));
};