'use strict';

/**
 * @module br/test/viewhandler/Checked
 */

var br = require('br/Core');
var Errors = require('br/Errors');
var ViewFixtureHandler = require('br/test/viewhandler/ViewFixtureHandler');
var Utils = require('br/test/Utils');

/**
 * @alias module:br/test/viewhandler/Checked
 * @classdesc
 * <code>Checked ViewFixtureHandler</code> can be used to trigger <code>checked</code> property of a checkbox or a radiobutton.
 * Example usage:
 * <p>
 * <code>and("example.view.(input:eq(0)).checked = false");</code>
 * </p>
 * @class
 * @implements module:br/test/viewhandler/ViewFixtureHandler
 */
function Checked() {
}
br.implement(Checked, ViewFixtureHandler);

Checked.prototype.get = function(eElement) {
	if (eElement.checked === undefined) {
		throw new Errors.InvalidTestError("Only checkboxes and radio buttons have the 'checked' property.");
	}
	return eElement.checked;
};

Checked.prototype.set = function(eElement, vValue) {
	if (eElement.checked === undefined) {
		throw new Errors.InvalidTestError("Only checkboxes and radio buttons can have the 'checked' property set.");
	}
	if (!(vValue === true || vValue === false)) {
		throw new Errors.InvalidTestError("the 'checked' property can only be set to true or false.");
	}
	
	Utils.fireDomEvent(eElement, 'click');
	Utils.fireDomEvent(eElement, 'change');
	eElement.checked = vValue;
};

module.exports = Checked;
