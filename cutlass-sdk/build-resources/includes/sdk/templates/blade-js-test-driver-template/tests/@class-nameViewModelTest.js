var @class-nameViewModelTest = TestCase("@class-nameTest");

var @class-nameViewModel = require( '@appns/@bladeset/@blade/@class-nameViewModel' );

@class-nameTest.prototype.testSomething = function() {
  var model = new @class-nameViewModel();
  assertEquals( 'Hello World!', model.message.getValue() );
};
