'use strict';

/**
 * @module br/test/FixtureFactory
 */

var Errors = require('br/Errors');

/**
 * Constructs a <code>FixtureFactory</code>.
 * An implementing FixtureFactory can have an optional <code>setUp</code> method which will be called before each test
 *  is executed and can be used to reset the state of a test and its stubs.
 * @alias module:br/test/FixtureFactory
 * @class
 * @interface
 * @description
 */
function FixtureFactory() {
};

/**
 * This method is called once by the test-runner after the FixtureFactory is constructed. The implementation should add
 *  to the test runner all the fixtures that are needed by the tests.
 *
 * @param {module:br/test/FixtureRegistry} fixtureRegistry The registry to which the fixtures should be registered.
 */
FixtureFactory.prototype.addFixtures = function(fixtureRegistry) {
	throw new Errors.UnimplementedInterfaceError('FixtureFactory.addFixtures() has not been implemented.');
};

module.exports = FixtureFactory;
