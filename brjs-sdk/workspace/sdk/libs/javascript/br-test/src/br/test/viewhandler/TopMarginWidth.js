'use strict';

require('jquery');

var br = require('br/Core');
var Errors = require('br/Errors');
var ViewFixtureHandler = require('br/test/viewhandler/ViewFixtureHandler');

/**
 * @name br.test.viewhandler.TopMarginWidth
 * @class
 * <code>TopMarginWidth ViewFixtureHandler</code> can be used to test the top margin width of an element.
 * Example usage:
 * <p>
 * <code>and("form.view.([identifier=\'orderForm\'] .order_amount .order_amount_input input).topMarginWidth = '10'");</code>
 * </p>
 * 
 * @constructor
 * @implements br.test.viewhandler.ViewFixtureHandler
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
