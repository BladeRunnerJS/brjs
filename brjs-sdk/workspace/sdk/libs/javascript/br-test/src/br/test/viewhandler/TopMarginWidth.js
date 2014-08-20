'use strict';

/**
 * @module br/test/viewhandler/TopMarginWidth
 */

require('jquery');

var br = require('br/Core');
var Errors = require('br/Errors');
var ViewFixtureHandler = require('br/test/viewhandler/ViewFixtureHandler');

/**
 * @class
 * @alias module:br/test/viewhandler/TopMarginWidth
 * @implements module:br/test/viewhandler/ViewFixtureHandler
 * 
 * @classdesc
 * <code>TopMarginWidth</code> instances of <code>ViewFixtureHandler</code> can be used to test the top margin width of an element.
 * Example usage:
 * 
 * <pre>and("form.view.([identifier=\'orderForm\'] .order_amount .order_amount_input input).topMarginWidth = '10'");</pre>
 */
function TopMarginWidth() {
}
br.implement(TopMarginWidth, ViewFixtureHandler);

TopMarginWidth.prototype.set = function(eElement) {
	throw new Errors.InvalidTestError("TopMarginWidth can't be used in a Given or When clause.");
};

TopMarginWidth.prototype.get = function(eElement) { 
	var sMargin = jQuery(eElement)[0].style.margin;
	
	pWidthValues = /\d+/.exec(sMargin);
	
	return parseInt(pWidthValues);
};

module.exports = TopMarginWidth;
