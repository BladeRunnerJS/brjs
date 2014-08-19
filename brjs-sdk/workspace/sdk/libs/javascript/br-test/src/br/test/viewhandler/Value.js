'use strict';

/**
 * @module br/test/viewhandler/Value
 */

require('jquery');

var br = require('br/Core');
var Errors = require('br/Errors');
var ViewFixtureHandler = require('br/test/viewhandler/ViewFixtureHandler');

/**
 * @alias module:br/test/viewhandler/Value
 * @description
 * <code>Value ViewFixtureHandler</code> can be used to set or get <code>value</code> property of a view element.
 * Example usage:
 * <p>
 * <code>then("form.view.(.orderSummary .orderAmount .native input).value = '50'");</code>
 * </p>
 * @class
 * @implements module:br/test/viewhandler/ViewFixtureHandler
 */
function Value() {
}
br.implement(Value, ViewFixtureHandler);

Value.prototype.get = function(eElement) {
	if (eElement.value === undefined) {
		throw new Errors.InvalidTestError("The element you tried to use the 'value' property on doesn't have one.");
	}
	var elementValue = jQuery(eElement).val();
	return elementValue;
};

Value.prototype.set = function(eElement, vValue) {
	if (eElement.value === undefined) {
		throw new Errors.InvalidTestError("The element you tried to use the 'value' property on doesn't have one.");
	}
	
	try { delete eElement.fireOnChange; } catch (e) { }
	jQuery(eElement).val(vValue).change();
};

module.exports = Value;
