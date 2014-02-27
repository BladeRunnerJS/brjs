var @class-nameViewModelTest = TestCase( '@class-nameViewModelTest' );

var @class-nameViewModel = require( '@appns/@bladeset/@blade/@class-nameViewModel' );

@class-nameViewModelTest.prototype.testSomething = function() {
  var model = new @class-nameViewModel();
  assertEquals( 'Hello World!', model.message() );
};
