'use strict';

/**
 * @module br/test/viewhandler/MouseMove
 */

var br = require('br/Core');
var Errors = require('br/Errors');
var ViewFixtureHandler = require('br/test/viewhandler/ViewFixtureHandler');
var Utils = require('br/test/Utils');

/**
 * @alias module:br/test/viewhandler/MouseMove
 * @description
 * <code>MouseMove ViewFixtureHandler</code> can be used to trigger <code>mousemove</code> event for a view element.
 * Example usage:
 * <p>
 * <code>when("test.page.(#aRealButton).mouseMove => true");</code>
 * </p>
 * @class
 * @implements module:br/test/viewhandler/ViewFixtureHandler
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
