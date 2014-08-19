'use strict';

/**
 * @module br/test/viewhandler/IsVisible
 */

require('jquery');

var br = require('br/Core');
var Errors = require('br/Errors');
var ViewFixtureHandler = require('br/test/viewhandler/ViewFixtureHandler');

/**
 * @alias module:br/test/viewhandler/IsVisible
 * @classdesc
 * <code>IsVisible ViewFixtureHandler</code> can be used to check if a view element is visible.
 * Example usage:
 * <p>
 * <code>then("form.view.(.orderSummary).isVisible = true");</code>
 * </p>
 * @class
 * @implements module:br/test/viewhandler/ViewFixtureHandler
 */
function IsVisible() {
}
br.implement(IsVisible, ViewFixtureHandler);

IsVisible.prototype.set = function(eElement) {
	throw new Errors.InvalidTestError("Visibility can't be used in a Given or When clause.");
};

IsVisible.prototype.get = function(eElement) {
	// Definition of invisible from jQuery API ...
	// Elements can be considered hidden for several reasons:
	//
	//	- They have a CSS display value of none.
	//	- They are form elements with type="hidden".
	//	- Their width and height are explicitly set to 0.
	//	- An ancestor element is hidden, so the element is not shown on the page.
	//
	// NOTE: Elements with visibility: hidden or opacity: 0 are considered to
	// be visible, since they still consume space in the layout.
	
	var sVisibility = jQuery(eElement).css("visibility");
	return jQuery(eElement).is(":visible") && sVisibility != 'hidden';
};

module.exports = IsVisible;
