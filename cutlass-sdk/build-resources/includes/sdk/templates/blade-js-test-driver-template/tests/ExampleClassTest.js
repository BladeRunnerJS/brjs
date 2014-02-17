ExampleClassTest = TestCase("ExampleClassTest");

var ExamplePresentationModel = require( '@appns/@bladeset/@blade/ExamplePresentationModel' );

ExampleClassTest.prototype.testSomething = function() {
  var model = new ExamplePresentationModel();
  assertEquals( 'Hello World!', model.message.getValue() );
};
