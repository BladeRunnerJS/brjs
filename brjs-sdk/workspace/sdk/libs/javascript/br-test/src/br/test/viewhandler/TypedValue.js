'use strict';

/**
 * @module br/test/viewhandler/TypedValue
 */

require('jquery');

var br = require('br/Core');
var Errors = require('br/Errors');
var ViewFixtureHandler = require('br/test/viewhandler/ViewFixtureHandler');
var Utils = require('br/test/Utils');

/**
 * @alias module:br/test/viewhandler/TypedValue
 * @classdesc
 * <code>TypedValue ViewFixtureHandler</code> can be used to simulate typing a value into an input view element.
 * Example usage:
 * <p>
 * <code>when("form.view.([identifier=\'orderForm\'] .order_amount .order_amount_input input).typedValue => 'abc'");</code>
 * </p>
 * @class
 * @implements module:br/test/viewhandler/ViewFixtureHandler
 */
function TypedValue() {
}
br.implement(TypedValue, ViewFixtureHandler);

TypedValue.prototype.get = function(eElement) {
	throw new Errors.InvalidTestError("The 'typedValue' property can't be used in a Then clause, try using 'value'.");
};

TypedValue.prototype.set = function(eElement, sValue) {
	if (eElement.value === undefined)
	{
		throw new Errors.InvalidTestError("The element you tried to use 'typedValue' on doesn't have a value field to simulate typing on.");
	}

	//Check whether the last active element wants us to fire a change event. 
	if (document.activeElement && document.activeElement.bFireChangeEventWhenNextElementIsActivated)
	{
		delete document.activeElement.bFireChangeEventWhenNextElementIsActivated;
		Utils.fireDomEvent(document.activeElement, 'change');
	}

	eElement.focus();
	jQuery(eElement).trigger('focusin');

	for (var i = 0, max = sValue.length; i < max; ++i)
	{
		var sKey = sValue.charAt(i);

		Utils.fireKeyEvent(eElement, "keydown", sKey);
		eElement.value += sKey;
		Utils.fireKeyEvent(eElement, "keypress", sKey);
		Utils.fireKeyEvent(eElement, "keyup", sKey);
	}

	//Request the next active element to fire a change event 
	eElement.bFireChangeEventWhenNextElementIsActivated = true;
};

module.exports = TypedValue;
