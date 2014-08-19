'use strict';

/**
 * @module br/test/viewhandler/Options
 */

require('jquery');

var br = require('br/Core');
var Errors = require('br/Errors');
var ViewFixtureHandler = require('br/test/viewhandler/ViewFixtureHandler');

/**
 * @alias module:br/test/viewhandler/Options
 * @description
 * <code>Options ViewFixtureHandler</code> can be used to set or get the value of <code>options</code> property
 * for a SELECT view element.
 * Example usage:
 * <p>
 * <code>then("form.model.payment.options = ['credit','debit']");</code>
 * </p>
 * @class
 * @implements module:br/test/viewhandler/ViewFixtureHandler
 */
function Options() {
}
br.implement(Options, ViewFixtureHandler);

Options.prototype.set = function(eElement, pValues) {
	if (eElement.tagName.toLowerCase() !== "select") {
		throw new Errors.InvalidTestError("The 'options' property is only available for SELECT elements.");
	}
	if (!(pValues instanceof Array)) {
		throw new Errors.InvalidTestError("The 'options' property can only take an Array as its value.");
	}
	eElement.innerHTML = "";
	for (var idx = 0, max = pValues.length; idx < max; idx++) {
		var eNewOption = document.createElement("option");
		eNewOption.innerHTML = pValues[idx].toString();
		eElement.appendChild(eNewOption);
	}
};

Options.prototype.get = function(eElement) {
	if (eElement.tagName.toLowerCase() !== "select") {
		throw new Errors.InvalidTestError("The 'options' property is only available for SELECT elements.");
	}
	var pOptions = [];
	jQuery(eElement).find("option").each(function(i,eOption){
		pOptions.push(eOption.innerHTML);
	});
	return pOptions;
};

module.exports = Options;
