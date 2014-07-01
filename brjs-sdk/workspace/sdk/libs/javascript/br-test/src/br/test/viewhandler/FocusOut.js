'use strict';

require('jquery');

var br = require('br/Core');
var Errors = require('br/Errors');
var ViewFixtureHandler = require('br/test/viewhandler/ViewFixtureHandler');

/**
 * @name br.test.viewhandler.FocusOut
 * @class
 * <code>FocusOut ViewFixtureHandler</code> can be used to trigger <code>focusout</code> on a view element.
 * Example usage:
 * <p>
 * <code>and("form.view.(#theField).focusOut => true");</code>
 * </p>
 * @constructor
 * @implements br.test.viewhandler.ViewFixtureHandler
 */
function FocusOut() {
}
br.implement(FocusOut, ViewFixtureHandler);

FocusOut.prototype.set = function(eElement) {
	jQuery(eElement).trigger('focusout');
};

FocusOut.prototype.get = function(eElement) {
	throw new Errors.InvalidTestError("The focusOut event cannot be used in a doGiven or doThen");
};

module.exports = FocusOut;
