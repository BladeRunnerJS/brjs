'use strict';

var br = require('br/Core');
var Errors = require('br/Errors');
var Fixture = require('br/test/Fixture');
var ViewFixture = require('br/test/ViewFixture');

/** @private */
function TestFixture(gwtTestRunner) {
	this.m_oGwtTestRunner = gwtTestRunner;
}
br.inherit(TestFixture, Fixture);

TestFixture.prototype.canHandleExactMatch = function() {
	return false;
};

TestFixture.prototype.canHandleProperty = function(property) {
	return property == 'continuesFrom';
};

TestFixture.prototype.addSubFixtures = function(fixtureRegistry) {
	fixtureRegistry.addFixture('page', new ViewFixture('body'));
};

TestFixture.prototype.doGiven = function(propertyName, value) {
	startingContinuesFrom(value);
	finishedContinuesFrom();
};

TestFixture.prototype.doWhen = function(propertyName, value) {
	throw new Errors.InvalidTestError('when is not supported by TestFixture');
};

TestFixture.prototype.doThen = function(propertyName, value) {
	throw new Errors.InvalidTestError('then is not supported by TestFixture');
};

module.exports = TestFixture;
