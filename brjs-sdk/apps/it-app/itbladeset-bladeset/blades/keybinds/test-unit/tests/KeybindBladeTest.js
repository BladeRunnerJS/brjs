(function(){
	'use strict';
	
	var Mousetrap = require('mousetrap');
	var KeybindBladeTest = TestCase( 'KeybindBladeTest' );
	var KeybindViewModel = require( 'itapp/itbladeset/keybinds/KeybindsViewModel' );
	
	KeybindBladeTest.prototype.testSomething = function() {
	  //given
	  var model = new KeybindViewModel();
	  var originalVeg = model.keyBoundVegetable();
	  assertEquals("Carrot", originalVeg);

	  //when
	  Mousetrap.trigger('shift+v');

	  //then
	  var resultingVeg = model.keyBoundVegetable();
	  assertEquals("Tomato", resultingVeg);
	};
}());