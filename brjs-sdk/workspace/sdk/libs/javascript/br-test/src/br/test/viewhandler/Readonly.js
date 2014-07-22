'use strict';

var br = require('br/Core');
var Errors = require('br/Errors');
var ViewFixtureHandler = require('br/test/viewhandler/ViewFixtureHandler');

/**
 * @name br.test.viewhandler.Readonly
 * @class
 * <code>ReadOnly ViewFixtureHandler</code> can be used to set or get the <code>readonly</code> attribute of an input view element
 * Example usage:
 * <p>
 * <code>then("form.view.(.totalValue input).readonly = true");</code>
 * </p>
 * @constructor
 * @implements br.test.viewhandler.ViewFixtureHandler
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
