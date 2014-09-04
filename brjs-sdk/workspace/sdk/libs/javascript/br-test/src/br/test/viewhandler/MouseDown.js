'use strict';

/**
 * @module br/test/viewhandler/MouseDown
 */

var br = require('br/Core');
var Errors = require('br/Errors');
var ViewFixtureHandler = require('br/test/viewhandler/ViewFixtureHandler');
var Utils = require('br/test/Utils');

/**
 * @class
 * @alias module:br/test/viewhandler/MouseDown
 * @implements module:br/test/viewhandler/ViewFixtureHandler
 * 
 * @classdesc
 * <code>MouseDown</code> instances of <code>ViewFixtureHandler</code> can be used to trigger <code>mousedown</code> event for a view element.
 * Example usage:
 * 
 * <pre>when("test.page.(#aRealButton).mouseDown => true");</pre>
 */
function MouseDown() {
}
br.implement(MouseDown, ViewFixtureHandler);

MouseDown.prototype.set = function(eElement, mValues) {
	Utils.fireMouseEvent(eElement, 'mousedown', mValues);
};

MouseDown.prototype.get = function(eElement) {
	throw new Errors.InvalidTestError("The mouseDown event cannot be used in a doGiven or doThen");
};

module.exports = MouseDown;
