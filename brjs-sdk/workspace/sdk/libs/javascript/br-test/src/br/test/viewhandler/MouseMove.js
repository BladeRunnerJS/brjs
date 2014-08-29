'use strict';

/**
 * @module br/test/viewhandler/MouseMove
 */

var br = require('br/Core');
var Errors = require('br/Errors');
var ViewFixtureHandler = require('br/test/viewhandler/ViewFixtureHandler');
var Utils = require('br/test/Utils');

/**
 * @class
 * @alias module:br/test/viewhandler/MouseMove
 * @implements module:br/test/viewhandler/ViewFixtureHandler
 * 
 * @classdesc
 * <code>MouseMove</code> instances of <code>ViewFixtureHandler</code> can be used to trigger <code>mousemove</code> event for a view element.
 * Example usage:
 * 
 * <pre>when("test.page.(#aRealButton).mouseMove => true");</pre>
 */
function MouseMove() {
}
br.implement(MouseMove, ViewFixtureHandler);

MouseMove.prototype.set = function(eElement, mValues) {
	Utils.fireMouseEvent(eElement, 'mousemove', mValues);
};

MouseMove.prototype.get = function(eElement) {
	throw new Errors.InvalidTestError("The mouseMove event cannot be used in a doGiven or doThen");
};

module.exports = MouseMove;
