'use strict';

require('jquery');

var br = require('br/Core');
var Errors = require('br/Errors');
var ViewFixtureHandler = require('br/test/viewhandler/ViewFixtureHandler');

/**
 * @name br.test.viewhandler.FocusIn
 * @class
 * <code>FocusIn ViewFixtureHandler</code> can be used to trigger <code>focusin</code> on a view element.
 * Example usage:
 * <p>
 * <code>and("form.view.(#theField).focusIn => true");</code>
 * </p>
 * @constructor
 * @implements br.test.viewhandler.ViewFixtureHandler
 */
function FocusIn() {
}
br.implement(FocusIn, ViewFixtureHandler);

FocusIn.prototype.set = function(eElement) {
	jQuery(eElement).trigger('focusin');
};

FocusIn.prototype.get = function(eElement) {
	throw new Errors.InvalidTestError("The focusIn event cannot be used in a doGiven or doThen");
};

module.exports = FocusIn;
