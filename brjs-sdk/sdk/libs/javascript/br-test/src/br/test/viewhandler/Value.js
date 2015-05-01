'use strict';

/**
 * @module br/test/viewhandler/Value
 */

require('jquery');

var br = require('br/Core');
var Errors = require('br/Errors');
var ViewFixtureHandler = require('br/test/viewhandler/ViewFixtureHandler');

/**
 * @class
 * @alias module:br/test/viewhandler/Value
 * @implements module:br/test/viewhandler/ViewFixtureHandler
 *
 * @classdesc
 * <code>Value</code> instances of <code>ViewFixtureHandler</code> can be used to set or get <code>value</code> property of a view element.
 * Example usage:
 *
 * <pre>then("form.view.(.orderSummary .orderAmount .native input).value = '50'");</pre>
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
	jQuery(eElement).val(vValue);
	/*
	 * DO NOT use JQuery for the change event here.
	 * Knockout doesn't listen for the jQuery change event unless jQuery appears before the knockout library
	 * and in order to make that happen presenter-knockout has to directly depends on jQuery.
	*/
	require("br/test/Utils").fireDomEvent(eElement, "change");
};

module.exports = Value;
