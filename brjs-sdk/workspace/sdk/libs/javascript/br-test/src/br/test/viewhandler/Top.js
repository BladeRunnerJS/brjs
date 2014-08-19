'use strict';

/**
 * @module br/test/viewhandler/Top
 */

require('jquery');

var br = require('br/Core');
var Errors = require('br/Errors');
var ViewFixtureHandler = require('br/test/viewhandler/ViewFixtureHandler');

/**
 * @alias module:br/test/viewhandler/Top
 * @classdesc
 * <code>Top ViewFixtureHandler</code> can be used to get style.top value of a view element.
 * Example usage:
 * <p>
 * <code>then("dynamicComponent.view.(.component).top = 20");</code>
 * </p>
 * @class
 * @implements module:br/test/viewhandler/ViewFixtureHandler
 */
function Top() {
}
br.implement(Top, ViewFixtureHandler);

Top.prototype.set = function(eElement) {
	throw new Errors.InvalidTestError("The Top attribute for a element cannot be set directly and should be set via the viewModel.");
};

Top.prototype.get = function(eElement) {
	return jQuery(eElement)[0].style.top;
};

module.exports = Top;
