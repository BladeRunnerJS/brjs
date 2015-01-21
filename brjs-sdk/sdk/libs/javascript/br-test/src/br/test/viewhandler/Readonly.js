'use strict';

/**
 * @module br/test/viewhandler/Readonly
 */

var br = require('br/Core');
var Errors = require('br/Errors');
var ViewFixtureHandler = require('br/test/viewhandler/ViewFixtureHandler');

/**
 * @class
 * @alias module:br/test/viewhandler/Readonly
 * @implements module:br/test/viewhandler/ViewFixtureHandler
 * 
 * @classdesc
 * <code>ReadOnly</code> instances of <code>ViewFixtureHandler</code> can be used to set or get the <code>readonly</code> attribute of an input view element
 * Example usage:
 * 
 * <pre>then("form.view.(.totalValue input).readonly = true");</pre>
 */
function Readonly() {
}
br.implement(Readonly, ViewFixtureHandler);

Readonly.prototype.set = function(eElement, vValue) {
	eElement.readOnly= (vValue === true);
};

Readonly.prototype.get = function(eElement) {
	return eElement.readOnly;
};

module.exports = Readonly;
