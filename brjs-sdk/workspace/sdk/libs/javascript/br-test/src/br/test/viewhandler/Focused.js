'use strict';

/**
 * @module br/test/viewhandler/Focused
 */

var br = require('br/Core');
var Errors = require('br/Errors');
var ViewFixtureHandler = require('br/test/viewhandler/ViewFixtureHandler');

/**
 * @alias module:br/test/viewhandler/Focused
 * @classdesc
 * <code>Focused ViewFixtureHandler</code> can be used to trigger <code>focus</code> and <code>blur</code> on a view element.
 * Example usage:
 * <p>
 * <code>and("form.view.(#theButton).focused => true");</code>
 * </p>
 * @class
 * @implements module:br/test/viewhandler/ViewFixtureHandler
 */
function Focused() {
}
br.implement(Focused, ViewFixtureHandler);

Focused.focusableElements = {"A" : true, "BODY" : true, "BUTTON" : true, "FRAME" : true, "IFRAME" : true, "IMG" : true, "INPUT" : true, "ISINDEX" : true,
		"OBJECT" : true, "SELECT" : true, "TEXTAREA" : true};

Focused.isFocusableElement = function(eElement) {
	return (eElement.tabIndex > 0) || ((eElement.tabIndex === 0) && this.focusableElements[eElement.tagName]);
};

Focused.prototype.set = function(eElement, vValue) {
	if ( !Focused.isFocusableElement(eElement) || eElement.disabled ) {
		throw new Errors.InvalidTestError("The 'focused' property is not available on non-focusable or disabled elements.");
	}

	if (vValue === true) {
		eElement.focus();
	} else if (vValue === false) {
		eElement.blur();
	} else {
		throw new Errors.InvalidTestError("The 'focused' property only takes boolean values.");
	}
};

Focused.prototype.get = function(eElement) {
	if (!Focused.isFocusableElement(eElement)) {
		throw new Errors.InvalidTestError("The 'focused' property is not available on non-focusable elements.");
	}

	if (eElement === document.activeElement) {
		return true;
	}
	return false;
};

module.exports = Focused;
