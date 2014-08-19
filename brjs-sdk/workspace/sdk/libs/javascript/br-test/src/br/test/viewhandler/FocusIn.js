'use strict';

/**
 * @module br/test/viewhandler/FocusIn
 */

require('jquery');

var br = require('br/Core');
var Errors = require('br/Errors');
var ViewFixtureHandler = require('br/test/viewhandler/ViewFixtureHandler');

/**
 * @alias module:br/test/viewhandler/FocusIn
 * @classdesc
 * <code>FocusIn ViewFixtureHandler</code> can be used to trigger <code>focusin</code> on a view element.
 * Example usage:
 * <p>
 * <code>and("form.view.(#theField).focusIn => true");</code>
 * </p>
 * @class
 * @implements module:br/test/viewhandler/ViewFixtureHandler
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
