/**
 * @constructor
 * @private
 *
 * @implements br.test.FixtureRegistry
 */
br.test.SubFixtureRegistry = function(oParentFixtureRegistry, sScope)
{
	this.m_oParentFixtureRegistry = oParentFixtureRegistry;
	this.m_sScope = sScope;
};

br.provide(br.test.SubFixtureRegistry, br.test.FixtureRegistry);

//*** FixtureRegistry Interface ***

br.test.SubFixtureRegistry.prototype.addFixture = function(sScope, oFixture)
{
	this.m_oParentFixtureRegistry.addFixture(this.m_sScope + "." + sScope, oFixture);
};
