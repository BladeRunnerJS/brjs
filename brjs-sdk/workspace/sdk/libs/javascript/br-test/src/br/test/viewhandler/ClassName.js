'use strict'

var br = require('br/Core');
var Errors = require('br/Errors');
var ViewFixtureHandler = require('br/test/viewhandler/ViewFixtureHandler');

/**
 * @name br.test.viewhandler.ClassName
 * @class
 * <code>ClassName ViewFixtureHandler</code> can be used to get a class of a view element.
 * Example usage:
 * <p>
 * <code>then("form.view.('#formContentAreaContainer').className = 'OpenSent'");</code> * </p>
 * @constructor
 * @implements br.test.viewhandler.ViewFixtureHandler
 */
function ClassName() {
}
br.implement(ClassName, ViewFixtureHandler);

ClassName.prototype.get = function(eElement) {
	return eElement.className;
};

ClassName.prototype.set = function(eElement, vValue) {
	if (typeof vValue !== "string") {
		throw new Errors.InvalidTestError("className can only be set to a String.");
	} else {
		eElement.className = vValue;
	}
};

module.exports = ClassName;
