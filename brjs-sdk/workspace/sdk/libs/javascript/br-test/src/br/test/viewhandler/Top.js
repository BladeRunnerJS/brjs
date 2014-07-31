'use strict';

require('jquery');

var br = require('br/Core');
var Errors = require('br/Errors');
var ViewFixtureHandler = require('br/test/viewhandler/ViewFixtureHandler');

/**
 * @name br.test.viewhandler.Top
 * @class
 * <code>Top ViewFixtureHandler</code> can be used to get style.top value of a view element.
 * Example usage:
 * <p>
 * <code>then("dynamicComponent.view.(.component).top = 20");</code>
 * </p>
 * @constructor
 * @implements br.test.viewhandler.ViewFixtureHandler
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
