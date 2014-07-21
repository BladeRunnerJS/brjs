'use strict';

var br = require('br/Core');
var Errors = require('br/Errors');
var ViewFixtureHandler = require('br/test/viewhandler/ViewFixtureHandler');
var Utils = require('br/test/Utils');

/**
 * @name br.test.viewhandler.ScrolledHorizontal
 * @class
 * <code>ScrolledHorizontal ViewFixtureHandler</code> can be used to trigger a horizontal scroll on a view element.
 * </code>
 * @constructor
 * @implements br.test.viewhandler.ViewFixtureHandler
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
