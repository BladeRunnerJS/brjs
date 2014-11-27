"use strict";

/**
 * @module br/component/testing/ComponentModelFixture
 */

var br = require('br/Core');
var Errors = require('br/Errors');
var Fixture = require('br/test/Fixture');

/**
 * @class
 * @interface
 * @alias module:br/component/testing/ComponentModelFixture
 * @extends module:br/test/Fixture
 * 
 * @classdesc
 * The <code>ComponentModelFixture</code> interface.
 */
function ComponentModelFixture() {
}

br.extend(ComponentModelFixture, Fixture);

/**
 * This method is called be the {@link module:br/component/testing/ComponentFixture} after
 * the component is created.
 *
 * @param {module:br/component/Component} oComponent the component instance managed by this ComponentModelFixture.
 */
ComponentModelFixture.prototype.setComponent = function(oComponent) {
	throw new Errors.UnimplementedInterfaceError("ComponentModelFixture.setComponent() has not been implemented.");
};

module.exports = ComponentModelFixture;
