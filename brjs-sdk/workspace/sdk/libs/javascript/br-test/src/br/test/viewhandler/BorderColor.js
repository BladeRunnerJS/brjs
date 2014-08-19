'use strict';

/**
 * @module br/test/viewhandler/BorderColor
 */

require('jquery');

var br = require('br/Core');
var Errors = require('br/Errors');
var ViewFixtureHandler = require('br/test/viewhandler/ViewFixtureHandler');

/**
 * @alias module:br/test/viewhandler/BorderColor
 * @description
 * <code>BorderColor ViewFixtureHandler</code> can be used to test the border color of an element.
 * Example usage:
 * <p>
 * <code>and("form.view.([identifier=\'orderForm\'] .order_amount .order_amount_input input).bordercolor = '#1111FF'");</code>
 * </p>
 * 
 * @class
 * @implements module:br/test/viewhandler/ViewFixtureHandler
 */
function BorderColor() {
}
br.implement(BorderColor, ViewFixtureHandler);

BorderColor.prototype.set = function(eElement) {
	throw new Errors.InvalidTestError("BorderWidth can't be used in a Given or When clause.");
};

BorderColor.prototype.get = function(eElement) { 
	var sColor = (jQuery(eElement)[0].style.borderColor).toLowerCase(); 
	
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

module.exports = BorderColor;
