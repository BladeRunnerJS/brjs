'use strict';

/**
 * @module br/test/viewhandler/ChildrenCount
 */

require('jquery');

var br = require('br/Core');
var Errors = require('br/Errors');
var ViewFixtureHandler = require('br/test/viewhandler/ViewFixtureHandler');

/**
 * @alias module:br/test/viewhandler/ChildrenCount
 * @classdesc
 * <code>ChildrenCount ViewFixtureHandler</code> can be used to get number of child elements for a view element.
 * Example usage:
 * <p>
 * <code>and("example.view.(select).childrenCount = 5");</code>
 * </p>
 * @class
 * @implements module:br/test/viewhandler/ViewFixtureHandler
 */
function ChildrenCount() {
}
br.implement(ChildrenCount, ViewFixtureHandler);

ChildrenCount.prototype.get = function(eElement) {
	return jQuery(eElement).children().length;
};

ChildrenCount.prototype.set = function(eElement, vValue) {
	throw new Errors.InvalidTestError("ChildrenCount value can not be set on an object and therefore should only be used in a then clause.");
};

module.exports = ChildrenCount;
