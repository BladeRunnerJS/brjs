(function(){
	'use strict';
	
	var ItbladeViewModelTest = TestCase( 'ItbladeViewModelTest' );
	
	var ItbladeViewModel = require( 'itapp/itbladeset/itblade/ItbladeViewModel' );
	
	ItbladeViewModelTest.prototype.testSomething = function() {
	  var model = new ItbladeViewModel();
	  assertEquals( 'Welcome to your new Blade.', model.welcomeMessage() );
	};
}());