'use strict';

/**
 * @module br/test/viewhandler/HasClass
 */

var br = require('br/Core');
var Errors = require('br/Errors');
var ViewFixtureHandler = require('br/test/viewhandler/ViewFixtureHandler');

/**
 * @alias module:br/test/viewhandler/HasClass
 * @classdesc
 * <code>HasClass ViewFixtureHandler</code> can be used to verify that a view element
 * has a particular class. Example usage:
 * <p>
 * <code>then("form.view.(.orderAmount .amountValue input).hasClass = 'has-error'");</code>
 * </p>
 * @class
 * @implements module:br/test/viewhandler/ViewFixtureHandler
 */
function HasClass() {
}
br.implement(HasClass, ViewFixtureHandler);

HasClass.prototype.get = function(eElement, sClassName) {
	if (eElement.className.match("(^| )" + sClassName + "($| )")) {
		return sClassName;
	} else {
		return null;
	}
};

HasClass.prototype.set = function(eElement, sClassName) {
	throw new Errors.InvalidTestError("hasClass can't be used in a Given or When clause.");
};

module.exports = HasClass;
