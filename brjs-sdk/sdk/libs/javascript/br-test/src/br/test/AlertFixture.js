'use strict';

/**
 * @module br/test/AlertFixture
 */

var br = require('br/Core');
var Errors = require('br/Errors');
var Fixture = require('br/test/Fixture');

/**
 * @class
 * @alias module:br/test/AlertFixture
 * 
 * @classdesc
 * The <code>AlertFixture</code> allows for testing of browser alerts.
 */
function AlertFixture() {
}
br.inherit(AlertFixture, Fixture);

AlertFixture.prototype.setUp = function() {
	this.m_pAlertStack = [];
	this.m_fOriginalWindowAlertFunction = window.alert;

	var self = this;
	window.alert = function(alertMessage) {
		self.m_pAlertStack.push(alertMessage);
	};
};

AlertFixture.prototype.tearDown = function() {
	window.alert = this.m_fOriginalWindowAlertFunction;
	assertTrue('there were alerts triggered that were not expected in the test', this.m_pAlertStack.length === 0);
};

AlertFixture.prototype.doGiven = function(propertyNameName, value) {
	throw new Errors.InvalidTestError('given is not supported by AlertFixture');
};

AlertFixture.prototype.doWhen = function(propertyNameName, value) {
	throw new Errors.InvalidTestError('when is not supported by AlertFixture');
};

AlertFixture.prototype.doThen = function(propertyNameName, value) {
	if (this.m_pAlertStack.length < 1) {
		fail('no alerts were triggered');
	}

	assertEquals(
		"expected alert message '" + value + "', but was '" + this.m_pAlertStack[0] + "'",
		value,
		this.m_pAlertStack[0]
	);

	this.m_pAlertStack.shift();
};

AlertFixture.prototype.addSubFixtures = function(fixtureRegistry) {
};

AlertFixture.prototype.canHandleExactMatch = function() {
	return true;
};

AlertFixture.prototype.canHandleProperty = function(propertyName) {
	return false;
};

module.exports = AlertFixture;
