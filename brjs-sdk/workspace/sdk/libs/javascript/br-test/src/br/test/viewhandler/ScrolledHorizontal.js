'use strict';

/**
 * @module br/test/viewhandler/ScrolledHorizontal
 */

var br = require('br/Core');
var Errors = require('br/Errors');
var ViewFixtureHandler = require('br/test/viewhandler/ViewFixtureHandler');
var Utils = require('br/test/Utils');

/**
 * @alias module:br/test/viewhandler/ScrolledHorizontal
 * @description
 * <code>ScrolledHorizontal ViewFixtureHandler</code> can be used to trigger a horizontal scroll on a view element.
 * </code>
 * @class
 * @implements module:br/test/viewhandler/ViewFixtureHandler
 */
function ScrolledHorizontal() {
}
br.implement(ScrolledHorizontal, ViewFixtureHandler);

ScrolledHorizontal.prototype.set = function(eElement, nOffset) {
	eElement.scrollLeft += parseFloat(nOffset);
	Utils.fireScrollEvent(eElement);
};

ScrolledHorizontal.prototype.get = function(eElement) {
	return eElement.scrollLeft;
};

module.exports = ScrolledHorizontal;
