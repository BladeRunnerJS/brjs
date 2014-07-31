'use strict';

var br = require('br/Core');
var Errors = require('br/Errors');
var ViewFixtureHandler = require('br/test/viewhandler/ViewFixtureHandler');
var Utils = require('br/test/Utils');

/**
 * @name br.test.viewhandler.RightClicked
 * @class
 * <code>RightClicked ViewFixtureHandler</code> can be used to trigger <code>contextmenu</code> event for a view element.
 * Example usage:
 * <p>
 * <code>when("test.page.(#aRealButton).rightclicked => true");</code>
 * </p>
 * @constructor
 * @implements br.test.viewhandler.ViewFixtureHandler
 */
function RightClicked() {
}
br.implement(RightClicked, ViewFixtureHandler);

RightClicked.prototype.set = function(eElement) {
	Utils.fireMouseEvent(eElement, "contextmenu");
};

RightClicked.prototype.get = function(eElement) {
	throw new Errors.InvalidTestError("Clicked can't be used in a then clause.");
};

module.exports = RightClicked;
