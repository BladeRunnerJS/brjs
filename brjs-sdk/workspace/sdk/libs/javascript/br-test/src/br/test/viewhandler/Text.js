'use strict';

/**
 * @module br/test/viewhandler/Text
 */

require('jquery');

var br = require('br/Core');
var Errors = require('br/Errors');
var ViewFixtureHandler = require('br/test/viewhandler/ViewFixtureHandler');

/**
 * @class
 * @alias module:br/test/viewhandler/Text
 * @implements module:br/test/viewhandler/ViewFixtureHandler
 * 
 * @classdesc
 * <code>Text</code> instances of <code>ViewFixtureHandler</code> can be used to set or get <code>text</code> property of a view element.
 * Example usage:
 * 
 * <pre>and("form.view.(.orderSummary .deliveryDate label).text = 'Delivery Date'");</pre>
 */
function Text() {
}
br.implement(Text, ViewFixtureHandler);

Text.prototype.get = function(eElement) {
	if (eElement.tagName.toLowerCase() === "input") {
		throw new Errors.InvalidTestError("Can not use the 'text' property on INPUT elements, try using 'value'.");
	}
	return jQuery(eElement).text();
};

Text.prototype.set = function(eElement, vValue) {
	if (eElement.tagName.toLowerCase() === "input") {
		throw new Errors.InvalidTestError("Can not use the 'text' property on INPUT elements, try using 'value'.");
	}
	jQuery(eElement).text(vValue);
};

module.exports = Text;
