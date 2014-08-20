'use strict';

/**
 * @module br/test/viewhandler/MouseOver
 */

var br = require('br/Core');
var Errors = require('br/Errors');
var ViewFixtureHandler = require('br/test/viewhandler/ViewFixtureHandler');
var Utils = require('br/test/Utils');

/**
 * @class
 * @alias module:br/test/viewhandler/MouseOver
 * @implements module:br/test/viewhandler/ViewFixtureHandler
 * 
 * @classdesc
 * <code>MouseOver</code> instances of <code>ViewFixtureHandler</code> can be used to trigger <code>mouseover</code> event for a view element.
 * Example usage:
 * 
 * <pre>when("test.page.(#aRealButton).mouseOver => true");</pre>
 */
function MouseOver() {
}
br.implement(MouseOver, ViewFixtureHandler);

MouseOver.prototype.set = function(eElement, mValues) {
	Utils.fireMouseEvent(eElement, 'mouseover', mValues);
};

MouseOver.prototype.get = function(eElement) {
	throw new Errors.InvalidTestError("The mouseOver event cannot be used in a doGiven or doThen");
};

module.exports = MouseOver;
