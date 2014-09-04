'use strict';

/**
 * @module br/test/viewhandler/ViewFixtureHandler
 */

var Errors = require('br/Errors');

/**
 * @class
 * @interface
 * @alias module:br/test/viewhandler/ViewFixtureHandler
 * 
 * @classdesc
 * <p>Classes implementing <code>ViewFixtureHandler</code> interface are used by <code>ViewFixture</code> to interact
 * with the elements in the rendered view.</p>
 */
function ViewFixtureHandler() {
}

/**
 * Updates <code>eElement</code> in a particular way, for example by setting a <code>vValue</code> on one of its properties.
 * @param {DOMElement} eElement DOM element
 * @param {Variant} vValue value to be set on eElement
 */
ViewFixtureHandler.prototype.set = function(eElement, vValue) {
	throw new Errors.UnimplementedInterfaceError("This method has not yet been implemented");
};

/**
 * Inspects <code>eElement</code> and returns the value of its particular property.
 * @param {DOMElement} eElement DOM element
 * @param {Variant} vValue value to be used when inspecting the element
 */
ViewFixtureHandler.prototype.get = function(eElement, vValue) {
	throw new Errors.UnimplementedInterfaceError("This method has not yet been implemented");
};

module.exports = ViewFixtureHandler;
