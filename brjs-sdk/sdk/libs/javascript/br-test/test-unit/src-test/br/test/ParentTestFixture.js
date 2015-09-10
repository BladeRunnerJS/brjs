var brCore = require("br/Core");
var Fixture = require("br/test/Fixture");

var ParentTestFixture = function()
{
	var TestFixtureFactory = require("br/test/TestFixtureFactory");
	this.m_oFirstMockFixture = TestFixtureFactory.createMockFixture(false);
	this.m_oSecondMockFixture = TestFixtureFactory.createMockFixture(false);
};
brCore.inherit(ParentTestFixture, Fixture);

ParentTestFixture.prototype.addSubFixtures = function(oFixtureRegistry)
{
	oFixtureRegistry.addFixture("subFixture1", this.m_oFirstMockFixture.proxy());
	oFixtureRegistry.addFixture("subFixture2", this.m_oSecondMockFixture.proxy());
};

ParentTestFixture.prototype.canHandleExactMatch = function()
{
	return false;
};

ParentTestFixture.prototype.canHandleProperty = function(sProperty)
{
	return false;
};

ParentTestFixture.prototype.getFirstMockFixture = function()
{
	return this.m_oFirstMockFixture;
};

ParentTestFixture.prototype.getSecondMockFixture = function()
{
	return this.m_oSecondMockFixture;
};

module.exports = ParentTestFixture;
