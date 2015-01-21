'use strict';

/**
 * @module br/test/viewhandler/OnKeyDown
 */

var br = require('br/Core');
var Errors = require('br/Errors');
var ViewFixtureHandler = require('br/test/viewhandler/ViewFixtureHandler');
var Utils = require('br/test/Utils');

/**
 * @class
 * @alias module:br/test/viewhandler/OnKeyDown
 * @implements module:br/test/viewhandler/ViewFixtureHandler
 */
function OnKeyDown() {
}
br.implement(OnKeyDown, ViewFixtureHandler);

OnKeyDown.prototype.set = function(eElement, mValues) {
	Utils.fireKeyEvent(eElement, "keydown", mValues, null);
};

OnKeyDown.prototype.get = function(eElement) {
	throw new Errors.InvalidTestError("The keyDown event cannot be used in a doGiven or doThen");
};

module.exports = OnKeyDown;
