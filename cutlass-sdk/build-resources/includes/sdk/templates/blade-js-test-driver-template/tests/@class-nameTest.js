var @class-nameTest = TestCase("@class-nameTest");

var @class-name = require( '@appns/@bladeset/@blade/@class-name' );

@class-nameTest.prototype.testSomething = function() {
  var model = new @class-name();
  assertEquals( 'Hello World!', model.message.getValue() );
};
