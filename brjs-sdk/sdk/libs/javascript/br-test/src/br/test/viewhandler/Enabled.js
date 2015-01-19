'use strict';

/**
 * @module br/test/viewhandler/Enabled
 */

require('jquery');

var br = require('br/Core');
var Errors = require('br/Errors');
var ViewFixtureHandler = require('br/test/viewhandler/ViewFixtureHandler');

/**
 * @class
 * @alias module:br/test/viewhandler/Enabled
 * @implements module:br/test/viewhandler/ViewFixtureHandler
 * 
 * @classdesc
 * <code>Enabled</code> instances of <code>ViewFixtureHandler</code> can be used to enable and disable a view element
 * by setting the <code>disabled<code> attribute.
 * Example usage:
 * 
 * <pre>and("form.view.(.close).enabled = true");</pre>
 */
function Enabled(){
}
br.implement(Enabled, ViewFixtureHandler);

Enabled.prototype.get = function(eElement) {

	var pElementsToTest = jQuery(eElement).add(jQuery(eElement).parents());

	for (var i = 0; i < pElementsToTest.length; i++)

	{
		var eElementToTest = jQuery(pElementsToTest[i]);
		if (eElementToTest.is(":disabled"))
		{
			return false;
		}
	}
	return true;
};

Enabled.prototype.set = function(eElement, vValue) {
	// Using strict equality to detect non-boolean vValue's
	if (vValue === true) {
		// Disabled elements make their descendants disabled too, so if someone
		// tries to enable such a child, FAIL.
		if (jQuery(eElement).parents(":disabled").length > 0)
		{
			throw new Errors.InvalidTestError("Can not enable element with a disabled ancestor.")
		}
		eElement.disabled = false;
	} else if (vValue === false) {
		eElement.disabled = true;
	} else {
		throw new Errors.InvalidTestError("enabled can only be set with a boolean value.");
	}
};

module.exports = Enabled;
