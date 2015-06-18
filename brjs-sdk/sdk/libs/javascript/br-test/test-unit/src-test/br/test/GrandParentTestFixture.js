var brCore = require("br/Core");
var Fixture = require("br/test/Fixture");

var GrandParentTestFixture = function()
{
	var ParentTestFixture = require("br/test/ParentTestFixture");
	this.m_oChildMockFixture = new ParentTestFixture();
};

brCore.inherit(GrandParentTestFixture, Fixture);

GrandParentTestFixture.prototype.addSubFixtures = function(oFixtureRegistry)
{
	oFixtureRegistry.addFixture("childFixture", this.m_oChildMockFixture);
};

GrandParentTestFixture.prototype.canHandleExactMatch = function()
{
	return false;
};

GrandParentTestFixture.prototype.canHandleProperty = function(sProperty)
{
	return false;
};

GrandParentTestFixture.prototype.getChildMockFixture = function()
{
	return this.m_oChildMockFixture;
};

module.exports = GrandParentTestFixture;
