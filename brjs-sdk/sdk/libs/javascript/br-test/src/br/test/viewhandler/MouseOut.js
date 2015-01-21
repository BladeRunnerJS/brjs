'use strict';

/**
 * @module br/test/viewhandler/MouseOut
 */

var br = require('br/Core');
var Errors = require('br/Errors');
var ViewFixtureHandler = require('br/test/viewhandler/ViewFixtureHandler');
var Utils = require('br/test/Utils');

/**
 * @class
 * @alias module:br/test/viewhandler/MouseOut
 * @implements module:br/test/viewhandler/ViewFixtureHandler
 * 
 * @classdesc
 * <code>MouseOut</code> instances of <code>ViewFixtureHandler</code> can be used to trigger <code>mouseout</code> event for a view element.
 * Example usage:
 * 
 * <pre>when("test.page.(#aRealButton).mouseOut => true");</pre>
 */
function MouseOut() {
}
br.implement(MouseOut, ViewFixtureHandler);

MouseOut.prototype.set = function(eElement, mValues) {
	Utils.fireMouseEvent(eElement, 'mouseout', mValues);
};

MouseOut.prototype.get = function(eElement) {
	throw new Errors.InvalidTestError("The mouseOut event cannot be used in a doGiven or doThen");
};

module.exports = MouseOut;
