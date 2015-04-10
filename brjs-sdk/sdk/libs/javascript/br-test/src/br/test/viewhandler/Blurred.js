'use strict';

/**
 * @module br/test/viewhandler/Blurred
 */

require('jquery');

var br = require('br/Core');
var Errors = require('br/Errors');
var ViewFixtureHandler = require('br/test/viewhandler/ViewFixtureHandler');
var Focused = require('br/test/viewhandler/Focused');

/**
 * @class
 * @alias module:br/test/viewhandler/Blurred
 * @implements module:br/test/viewhandler/ViewFixtureHandler
 *
 * @classdesc
 * <code>Blurred</code> instances of <code>ViewFixtureHandler</code> can be used to trigger <code>blur</code> or <code>focus</code> events on the view element.
 * Example usage:
 *
 * <pre>and("form.view.([identifier=\'orderForm\'] .order_amount .order_amount_input input).blurred => true");</pre>
 */
function Blurred() {
}
br.implement(Blurred, ViewFixtureHandler);

Blurred.prototype.set = function(eElement, vValue) {
	/*
	 * DO NOT use JQuery for the events here.
	 * Knockout doesn't listen for the jQuery change event unless jQuery appears before the knockout library
	 * and in order to make that happen presenter-knockout has to directly depends on jQuery.
	*/
	var Utils = require("br/test/Utils");

	if ( !Focused.isFocusableElement(eElement) || eElement.disabled ) {
		throw new Errors.InvalidTestError("The 'blurred' property is not available on non-focusable or disabled elements.");
	}

	if (vValue === true) {
		eElement.blur();
		Utils.fireDomEvent(eElement, "blur");

		if (eElement.tagName.toLowerCase() == "input")
		{
			Utils.fireDomEvent(eElement, "change");
		}
	} else if (vValue === false) {
		eElement.focus();
	} else {
		throw new Errors.InvalidTestError("The 'blurred' property only takes boolean values.");
	}
};

Blurred.prototype.get = function(eElement) {
	if (!Focused.isFocusableElement(eElement)) {
		throw new Errors.InvalidTestError("The 'blurred' property is not available on non-focusable elements.");
	}

	if (eElement === document.activeElement) {
		return false;
	}
	return true;
};

module.exports = Blurred;
