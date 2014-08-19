'use strict';

/**
 * @module br/test/viewhandler/Color
 */

require('jquery');

var br = require('br/Core');
var Errors = require('br/Errors');
var ViewFixtureHandler = require('br/test/viewhandler/ViewFixtureHandler');

/**
 * @alias module:br/test/viewhandler/Color
 * @description
 * <code>Color ViewFixtureHandler</code> can be used to test the bottom margin width of an element.
 * Example usage:
 * <p>
 * <code>and("form.view.([identifier=\'orderForm\'] .order_amount .order_amount_input input).color = '#1212DD'");</code>
 * </p>
 * 
 * @class
 * @implements module:br/test/viewhandler/ViewFixtureHandler
 */
function Color() {
}
br.implement(Color, ViewFixtureHandler);

Color.prototype.set = function(eElement) {
	throw new Errors.InvalidTestError("Color can't be used in a Given or When clause.");
};

Color.prototype.get = function(eElement) { 
	var sColor = (jQuery(eElement)[0].style.color).toLowerCase(); 
	
	var digits = /rgba?\(\s*(\d+)\s*,\s*(\d+)\s*,\s*(\d+)/.exec(sColor);
	var sHexColor;
	
	if (digits) {
		var red = parseInt(digits[1]);
		var green = parseInt(digits[2]);
		var blue = parseInt(digits[3]);
		
		var rgb = 1 << 24 | blue | (green << 8) | (red << 16);
		
		sHexColor = '#' + rgb.toString(16).substr(1);
	} else if (sColor.match(/^#[0-9a-f]{6}/i)) {
		sHexColor = sColor;
	} else {
		throw new Errors.InvalidTestError("Color format was not expected");
	}
	return sHexColor.toUpperCase();
};

module.exports = Color;
