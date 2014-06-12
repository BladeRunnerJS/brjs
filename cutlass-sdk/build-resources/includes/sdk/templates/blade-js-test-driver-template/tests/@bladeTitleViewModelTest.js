var @bladeTitleViewModelTest = TestCase( '@bladeTitleViewModelTest' );

var @bladeTitleViewModel = require( '@appns/@bladeset/@blade/@bladeTitleViewModel' );

@bladeTitleViewModelTest.prototype.testSomething = function() {
  var model = new @bladeTitleViewModel();
  assertEquals( 'Hello World!', model.message() );
};
