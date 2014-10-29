(function(){
	'use strict';
	
	var @bladeTitleViewModelTest = TestCase( '@bladeTitleViewModelTest' );
	
	var @bladeTitleViewModel = require( '@bladeRequirePrefix/@bladeTitleViewModel' );
	
	@bladeTitleViewModelTest.prototype.testSomething = function() {
	  var model = new @bladeTitleViewModel();
	  assertEquals( 'Welcome to your new Blade.', model.welcomeMessage() );
	};
}());