'use strict';

/**
 * @module br/test/FixtureRegistry
 */

/**
 * @interface
 * @class
 * @alias module:br/test/FixtureRegistry
 * 
 * @classdesc
 * The <code>FixtureRegistry</code> allows for registration of fixtures for a specified scope.
 */
function FixtureRegistry() {
};

/**
 * Adds a fixture to the registry.
 * 
 * @param {String} scope The scope to which the fixture should be registered.
 * @param {module:br/test/Fixture} fixture The fixture to register.
 */
FixtureRegistry.prototype.addFixture = function(scope, fixture) {
};

module.exports = FixtureRegistry;
