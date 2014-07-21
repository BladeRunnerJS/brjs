'use strict';

var br = require('br/Core');
var FixtureRegistry = require('br/test/FixtureRegistry');

/**
 * @name br.test.SubFixtureRegistry
 * @constructor
 * @private
 *
 * @implements br.test.FixtureRegistry
 */
function SubFixtureRegistry(parentFixtureRegistry, scope) {
	this.m_oParentFixtureRegistry = parentFixtureRegistry;
	this.m_sScope = scope;
};

br.inherit(SubFixtureRegistry, FixtureRegistry);

/** @see br.test.FixtureRegistry#addFixture */
SubFixtureRegistry.prototype.addFixture = function(scope, fixture) {
	this.m_oParentFixtureRegistry.addFixture(this.m_sScope + '.' + scope, fixture);
};

module.exports = SubFixtureRegistry;
