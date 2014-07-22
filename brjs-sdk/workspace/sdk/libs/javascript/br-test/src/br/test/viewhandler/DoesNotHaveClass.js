'use strict';

var br = require('br/Core');
var Errors = require('br/Errors');
var ViewFixtureHandler = require('br/test/viewhandler/ViewFixtureHandler');

/**
 * @name br.test.viewhandler.DoesNotHaveClass
 * @class
 * <code>DoesNotHaveClass ViewFixtureHandler</code> can be used to verify that a view element
 * does not have a particular class. Example usage:
 * <p>
 * <code>then("test.page.(#aRealButton).doesNotHaveClass = 'hover'");</code>
 * </p>
 * @constructor
 * @implements br.test.viewhandler.ViewFixtureHandler
 */
function DoesNotHaveClass() {
}
br.implement(DoesNotHaveClass, ViewFixtureHandler);

DoesNotHaveClass.prototype.get = function(eElement, sClassName) {
	if (eElement.className.match("(^| )" + sClassName + "($| )")) {
		return null;
	} else {
		return sClassName;
	}
};

DoesNotHaveClass.prototype.set = function(eElement, sClassName) {
	throw new Errors.InvalidTestError("doesNotHaveClass can't be used in a Given or When clause.");
};

module.exports = DoesNotHaveClass;
