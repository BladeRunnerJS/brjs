;(function(){
	"use strict";
	
	var Errors = require('br/Errors');
	
	/**
	 * The <code>ComponentModelFixture</code> interface
	 * 
	 * @class
	 * @interface
	 * @extends br.test.Fixture
	 */
	function ComponentModelFixture() {};

	br.extend(ComponentModelFixture, br.test.Fixture);

	/**
	 * This method is called be the {@link br.component.testing.ComponentFixture} after
	 * the component is created.
	 * 
	 * @param {br.component.Component} oComponent the component instance managed by this ComponentModelFixture.
	 */
	ComponentModelFixture.prototype.setComponent = function(oComponent) {
		throw new Errors.UnimplementedInterfaceError("ComponentModelFixture.setComponent() has not been implemented.");
	};

	br.component.testing.ComponentModelFixture = ComponentModelFixture;
})();
