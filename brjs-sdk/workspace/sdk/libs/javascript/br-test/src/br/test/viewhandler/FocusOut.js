'use strict';

/**
 * @module br/test/viewhandler/FocusOut
 */

require('jquery');

var br = require('br/Core');
var Errors = require('br/Errors');
var ViewFixtureHandler = require('br/test/viewhandler/ViewFixtureHandler');

/**
 * @alias module:br/test/viewhandler/FocusOut
 * @description
 * <code>FocusOut ViewFixtureHandler</code> can be used to trigger <code>focusout</code> on a view element.
 * Example usage:
 * <p>
 * <code>and("form.view.(#theField).focusOut => true");</code>
 * </p>
 * @class
 * @implements module:br/test/viewhandler/ViewFixtureHandler
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
