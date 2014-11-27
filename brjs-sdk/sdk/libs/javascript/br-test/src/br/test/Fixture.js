'use strict';

/**
 * @module br/test/Fixture
 */

var Errors = require('br/Errors');

/**
 * @class
 * @interface
 * @alias module:br/test/Fixture
 * 
 * @classdesc
 * <code>Fixture</code> is the interface for individual fixtures added to the GWTTestRunner. The purpose of a Fixture 
 * is to enable tests to manipulate and access a specific area of the system under tests using the GWT 
 * (given-when-then) BDD format.
 */
function Fixture() {
}

/**
 * This method is called just before a GWT test. This optional interface method can be implemented if the fixture  is 
 *  required to correctly set up the system-under-test before each test.
 */
Fixture.prototype.setUp = function() {
	// optional interface method
};

/**
 * This method is called just after a GWT test. This optional interface method can be implemented if the fixture  is 
 *  required to correctly tear down the system-under-test after each test or to reset any state held in the fixture's
 *  implementation.
 */
Fixture.prototype.tearDown = function() {
	// optional interface method
};

/**
 * This optional interface method can be implemented by a Fixture for a complex system which can be conceptually 
 * decomposed into separate sub-systems, enabling the fixture to delegate the handling of some fixture properties to 
 *  the sub-fixtures. This method is called by the GWTTestRunner.
 *
 * @param {module:br/test/FixtureRegistry} fixtureRegistry The registry to which the fixtures should be registered.
 */
Fixture.prototype.addSubFixtures = function(fixtureRegistry) {
	// optional interface method
};

Fixture.prototype.canHandleExactMatch = function() {
	throw new Errors.UnimplementedInterfaceError('Fixture.canHandleExactMatch() has not been implemented.');
};

/**
 * This method is called by the GWTTestRunner to check whether a property used in a GWT test is supported by the 
 *  fixture.
 *
 * @param {String} propertyName the property name to check.
 * @returns {Boolean} true if the fixture handles the property; false otherwise.
 */
Fixture.prototype.canHandleProperty = function(propertyName) {
	throw new Errors.UnimplementedInterfaceError('Fixture.canHandleProperty() has not been implemented.');
};

/**
 * This method is called in order to manipulate a property on the system under test in a given clause.
 *
 * @param {String} propertyName The property to be changed.
 * @param {String} value The new value of the property.
 */
Fixture.prototype.doGiven = function(propertyName, value) {
	throw new Errors.UnimplementedInterfaceError('Fixture.doGiven() has not been implemented.');
};

/**
 * This method is called in order to manipulate a property on the system under test in a when clause.
 *
 * @param {String} propertyName The property to be changed.
 * @param {String} value The new value of the property.
 */
Fixture.prototype.doWhen = function(propertyName, value) {
	throw new Errors.UnimplementedInterfaceError('Fixture.doWhen() has not been implemented.');
};

/**
 * This method is called in order to assert a property's value on the system under test.
 *
 * @param {String} propertyName The property name to assert.
 * @param {String} value The value to assert.
 */
Fixture.prototype.doThen = function(propertyName, value) {
	throw new Errors.UnimplementedInterfaceError('Fixture.doThen() has not been implemented.');
};

module.exports = Fixture;
