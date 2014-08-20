'use strict'

/**
 * @module br/test/viewhandler/ClassName
 */

var br = require('br/Core');
var Errors = require('br/Errors');
var ViewFixtureHandler = require('br/test/viewhandler/ViewFixtureHandler');

/**
 * @class
 * @alias module:br/test/viewhandler/ClassName
 * @implements module:br/test/viewhandler/ViewFixtureHandler
 * 
 * @classdesc
 * <code>ClassName</code> instances of <code>ViewFixtureHandler</code> can be used to get a class of a view element.
 * Example usage:
 * 
 * <pre>then("form.view.('#formContentAreaContainer').className = 'OpenSent'");</pre>
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
