'use strict';

/**
 * @module br/test/viewhandler/Width
 */

require('jquery');

var br = require('br/Core');
var Errors = require('br/Errors');
var ViewFixtureHandler = require('br/test/viewhandler/ViewFixtureHandler');

/**
 * @alias module:br/test/viewhandler/Width
 * @classdesc
 * <code>Width ViewFixtureHandler</code> can be used to get width of a view element.
 * Example usage:
 * <p>
 * <code>then("dynamicComponent.view.(.component).width = 100");</code>
 * </p>
 * @class
 * @implements module:br/test/viewhandler/ViewFixtureHandler
 */
function Width() {
}
br.implement(Width, ViewFixtureHandler);

Width.prototype.set = function(eElement) {
	throw new Errors.InvalidTestError("The width attribute for a element cannot be set directly and should be set via the viewModel.");
};

Width.prototype.get = function(eElement) {
	return jQuery(eElement).width();
};

module.exports = Width;
