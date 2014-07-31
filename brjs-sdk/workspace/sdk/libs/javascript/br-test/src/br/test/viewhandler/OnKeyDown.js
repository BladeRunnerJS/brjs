'use strict';

var br = require('br/Core');
var Errors = require('br/Errors');
var ViewFixtureHandler = require('br/test/viewhandler/ViewFixtureHandler');
var Utils = require('br/test/Utils');

/**
 * @name br.test.viewhandler.OnKeyDown
 * @constructor
 * @implements br.test.viewhandler.ViewFixtureHandler
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
