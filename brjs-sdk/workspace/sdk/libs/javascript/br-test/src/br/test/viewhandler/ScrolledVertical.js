'use strict';

var br = require('br/Core');
var Errors = require('br/Errors');
var ViewFixtureHandler = require('br/test/viewhandler/ViewFixtureHandler');
var Utils = require('br/test/Utils');

/**
 * @name br.test.viewhandler.ScrolledVertical
 * @class
 * <code>ScrolledVertical ViewFixtureHandler</code> can be used to trigger a vertical scroll on a view element.
 * </code>
 * @constructor
 * @implements br.test.viewhandler.ViewFixtureHandler
 */
function ScrolledVertical() {
}
br.implement(ScrolledVertical, ViewFixtureHandler);

ScrolledVertical.prototype.set = function(eElement, nOffset) {
	eElement.scrollTop += parseFloat(nOffset);
	Utils.fireScrollEvent(eElement);
};

ScrolledVertical.prototype.get = function(eElement) {
	throw new Errors.InvalidTestError("ScrolledVertical can't be used in a then clause.");
};

module.exports = ScrolledVertical;
