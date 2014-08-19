'use strict';

/**
 * @module br/test/viewhandler/BorderWidth
 */

require('jquery');

var br = require('br/Core');
var Errors = require('br/Errors');
var ViewFixtureHandler = require('br/test/viewhandler/ViewFixtureHandler');

/**
 * @alias module:br/test/viewhandler/BorderWidth
 * @description
 * <code>BorderWidth ViewFixtureHandler</code> can be used to test the border width of an element.
 * Example usage:
 * <p>
 * <code>and("form.view.([identifier=\'orderForm\'] .order_amount .order_amount_input input).borderwidth = '10'");</code>
 * </p>
 * 
 * @class
 * @implements module:br/test/viewhandler/ViewFixtureHandler
 */
function BorderWidth() {
}
br.implement(BorderWidth, ViewFixtureHandler);

BorderWidth.prototype.set = function(eElement) {
	throw new Errors.InvalidTestError("BorderWidth can't be used in a Given or When clause.");
};

BorderWidth.prototype.get = function(eElement) { 
	return parseInt(jQuery(eElement)[0].style.borderWidth);
};

module.exports = BorderWidth;
