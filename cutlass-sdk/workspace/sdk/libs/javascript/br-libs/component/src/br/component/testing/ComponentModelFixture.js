"use strict";

var br = require('br/Core');
var Errors = require('br/Errors');
var Fixture = require('br/test/Fixture');

/**
 * The <code>ComponentModelFixture</code> interface
 * 
 * @class
 * @interface
 * @extends br.test.Fixture
 */
function ComponentModelFixture() {};

br.extend(ComponentModelFixture, Fixture);

/**
 * This method is called be the {@link br.component.testing.ComponentFixture} after
 * the component is created.
 * 
 * @param {br.component.Component} oComponent the component instance managed by this ComponentModelFixture.
 */
ComponentModelFixture.prototype.setComponent = function(oComponent) {
	throw new Errors.UnimplementedInterfaceError("ComponentModelFixture.setComponent() has not been implemented.");
};

module.exports = ComponentModelFixture;
