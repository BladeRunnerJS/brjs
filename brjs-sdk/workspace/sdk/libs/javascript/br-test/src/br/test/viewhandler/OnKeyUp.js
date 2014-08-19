'use strict';

/**
 * @module br/test/viewhandler/OnKeyUp
 */

var br = require('br/Core');
var Errors = require('br/Errors');
var ViewFixtureHandler = require('br/test/viewhandler/ViewFixtureHandler');
var Utils = require('br/test/Utils');

/**
 * @alias module:br/test/viewhandler/OnKeyUp
 * @class
 * @implements module:br/test/viewhandler/ViewFixtureHandler
 */
function OnKeyUp() {
}
br.implement(OnKeyUp, ViewFixtureHandler);

OnKeyUp.prototype.set = function(eElement, mValues) {
	Utils.fireKeyEvent(eElement, "keyup", mValues.sKey, mValues);
};

OnKeyUp.prototype.get = function(eElement) {
	throw new Errors.InvalidTestError("The keyUp event cannot be used in a doGiven or doThen");
};

module.exports = OnKeyUp;
