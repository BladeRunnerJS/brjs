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
 * @name br/test.viewhandler.Blurred
 * @classdesc
 * <code>Blurred ViewFixtureHandler</code> can be used to trigger <code>blur</code> or <code>focus</code> events on the view element.
 * Example usage:
 * <p>
 * <code>and("form.view.([identifier=\'orderForm\'] .order_amount .order_amount_input input).blurred => true");</code>
 * </p>
 * 
 * @class
 * @implements module:br/test/viewhandler/ViewFixtureHandler
 */
function Blurred() {
}
br.implement(Blurred, ViewFixtureHandler);

Blurred.prototype.set = function(eElement, vValue) {
	if ( !Focused.isFocusableElement(eElement) || eElement.disabled ) {
		throw new Errors.InvalidTestError("The 'blurred' property is not available on non-focusable or disabled elements.");
	}
	
	if (vValue === true) {
		eElement.blur();
		jQuery(eElement).trigger("blur");
		
		if (eElement.tagName.toLowerCase() == "input")
		{
			jQuery(eElement).trigger("change");
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
