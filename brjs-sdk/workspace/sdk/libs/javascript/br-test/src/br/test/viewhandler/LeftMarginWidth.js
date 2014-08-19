'use strict';

/**
 * @module br/test/viewhandler/LeftMarginWidth
 */

require('jquery');

var br = require('br/Core');
var Errors = require('br/Errors');
var ViewFixtureHandler = require('br/test/viewhandler/ViewFixtureHandler');

/**
 * @alias module:br/test/viewhandler/LeftMarginWidth
 * @description
 * <code>LeftMarginWidth ViewFixtureHandler</code> can be used to test the left margin width of an element.
 * Example usage:
 * <p>
 * <code>and("form.view.([identifier=\'orderForm\'] .order_amount .order_amount_input input).leftMarginWidth = '10'");</code>
 * </p>
 * 
 * @class
 * @implements module:br/test/viewhandler/ViewFixtureHandler
 */
function LeftMarginWidth() {
}
br.implement(LeftMarginWidth, ViewFixtureHandler);

LeftMarginWidth.prototype.set = function(eElement) {
	throw new Errors.InvalidTestError("LeftMarginWidth can't be used in a Given or When clause.");
};

LeftMarginWidth.prototype.get = function(eElement) { 
	var sMargin = jQuery(eElement)[0].style.margin;
	
	var	pWidthValues = sMargin.match(/\d+/g);
	
	return pWidthValues.length == 4 ? pWidthValues[3] : pWidthValues.length == 2 ? pWidthValues[1] : pWidthValues[0] ;
};

module.exports = LeftMarginWidth;
