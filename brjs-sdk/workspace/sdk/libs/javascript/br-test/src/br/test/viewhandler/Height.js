'use strict';

/**
 * @module br/test/viewhandler/Height
 */

require('jquery');

var br = require('br/Core');
var Errors = require('br/Errors');
var ViewFixtureHandler = require('br/test/viewhandler/ViewFixtureHandler');

/**
 * @alias module:br/test/viewhandler/Height
 * @classdesc
 * <code>Height ViewFixtureHandler</code> can be used to get height of a view element.
 * Example usage:
 * <p>
 * <code>then("dynamicComponent.view.(.component).height = 200");</code>
 * </p>
 * @class
 * @implements module:br/test/viewhandler/ViewFixtureHandler
 */
function Height() {
}
br.implement(Height, ViewFixtureHandler);

Height.prototype.set = function(eElement) {
	throw new Errors.InvalidTestError("The Height attribute for a element cannot be set directly and should be set via the viewModel.");
};

Height.prototype.get = function(eElement) {
	return jQuery(eElement).height();
};

module.exports = Height;
