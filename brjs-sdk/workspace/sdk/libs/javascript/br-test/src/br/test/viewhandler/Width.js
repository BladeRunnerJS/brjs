'use strict';

/**
 * @module br/test/viewhandler/Width
 */

require('jquery');

var br = require('br/Core');
var Errors = require('br/Errors');
var ViewFixtureHandler = require('br/test/viewhandler/ViewFixtureHandler');

/**
 * @class
 * @alias module:br/test/viewhandler/Width
 * @implements module:br/test/viewhandler/ViewFixtureHandler
 * 
 * @classdesc
 * <code>Width</code> instances of <code>ViewFixtureHandler</code> can be used to get width of a view element.
 * Example usage:
 * 
 * <pre>then("dynamicComponent.view.(.component).width = 100");</pre>
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
