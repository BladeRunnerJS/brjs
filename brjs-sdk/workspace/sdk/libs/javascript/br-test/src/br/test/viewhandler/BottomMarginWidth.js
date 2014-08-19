'use strict';

/**
 * @module br/test/viewhandler/BottomMarginWidth
 */

require('jquery');

var br = require('br/Core');
var Errors = require('br/Errors');
var ViewFixtureHandler = require('br/test/viewhandler/ViewFixtureHandler');

/**
 * @alias module:br/test/viewhandler/BottomMarginWidth
 * @description
 * <code>BottomMarginWidth ViewFixtureHandler</code> can be used to test the bottom margin width of an element.
 * Example usage:
 * <p>
 * <code>and("form.view.([identifier=\'orderForm\'] .order_amount .order_amount_input input).bottomMarginWidth = '10'");</code>
 * </p>
 * 
 * @class
 * @implements module:br/test/viewhandler/ViewFixtureHandler
 */
function BottomMarginWidth() {
}
br.implement(BottomMarginWidth, ViewFixtureHandler);

BottomMarginWidth.prototype.set = function(eElement) {
	throw new Errors.InvalidTestError("BottomMarginWidth can't be used in a Given or When clause.");
};

BottomMarginWidth.prototype.get = function(eElement) { 
	var sMargin = jQuery(eElement)[0].style.margin;
	
	var	pWidthValues = sMargin.match(/\d+/g);
	
	return pWidthValues.length == 4 ? pWidthValues[2] : pWidthValues[0] ;
};

module.exports = BottomMarginWidth;
