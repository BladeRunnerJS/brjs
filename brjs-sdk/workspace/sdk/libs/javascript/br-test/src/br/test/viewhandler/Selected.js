'use strict';

/**
 * @module br/test/viewhandler/Selected
 */

require('jquery');

var br = require('br/Core');
var Errors = require('br/Errors');
var ViewFixtureHandler = require('br/test/viewhandler/ViewFixtureHandler');

/**
 * @alias module:br/test/viewhandler/Selected
 * @classdesc
 * <code>Selected ViewFixtureHandler</code> can be used to get or set <code>selected</code>
 * property of an OPTION view element.
 * Example usage:
 * <p>
 * <code>when("demo.view.(#multiSelectBox option:last).selected => true");</code>
 * </p>
 * @class
 * @implements module:br/test/viewhandler/ViewFixtureHandler
 */
function Selected(){
}
br.implement(Selected, ViewFixtureHandler);

Selected.prototype.get = function(eElement) {
	if (eElement.selected === undefined) {
		throw new Errors.InvalidTestError("Only Option elements have the 'selected' property.");
	}
	return eElement.selected;
};

Selected.prototype.set = function(eElement, vValue) {
	if (eElement.selected === undefined) {
		throw new Errors.InvalidTestError("Only Option elements have their 'selected' property set.");
	}
	if (!(vValue === true || vValue === false)) {
		throw new Errors.InvalidTestError("the 'selected' property can only be set to true or false.");
	}
	if (eElement.selected != vValue) {
		eElement.selected = vValue;
		jQuery(eElement).parent('select').trigger("change");
	}
};

module.exports = Selected;
