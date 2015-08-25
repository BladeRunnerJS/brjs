var brCore = require("br/Core");
var Fixture = require("br/test/Fixture");
var FixtureFactory = require("br/test/FixtureFactory");

var TestFixtureFactory = function()
{
	this.m_mFixtures = {};
};
brCore.implement(TestFixtureFactory, FixtureFactory);

TestFixtureFactory.createMockFixture = function(bCanHandleExactMatch, bApplyTearDownStub)
{
	var oMockFixture = mock(Fixture);
	oMockFixture.stubs().canHandleProperty(ANYTHING).will(returnValue(false));
	oMockFixture.stubs().canHandleProperty("prop").will(returnValue(true));
	oMockFixture.stubs().canHandleExactMatch().will(returnValue(bCanHandleExactMatch));
	oMockFixture.stubs().addSubFixtures(ANYTHING);
	oMockFixture.stubs().setUp();
	if(bApplyTearDownStub !== false)
	{
		oMockFixture.stubs().tearDown();
	}
	
	return oMockFixture;
};

TestFixtureFactory.prototype.addFixtures = function(oFixtureRegistry)
{
	var ParentTestFixture = require("br/test/ParentTestFixture");
	var GrandParentTestFixture = require("br/test/GrandParentTestFixture");
	
	this.addMockFixtureToRegistry(oFixtureRegistry, "fixture", TestFixtureFactory.createMockFixture(false, false));
	this.addMockFixtureToRegistry(oFixtureRegistry, "propertyFixture", TestFixtureFactory.createMockFixture(true));
	this.addFixtureToRegistry(oFixtureRegistry, "parentFixture", new ParentTestFixture());
	this.addFixtureToRegistry(oFixtureRegistry, "grandParentFixture", new GrandParentTestFixture());
	this.addMockFixtureToRegistry(oFixtureRegistry, "another=fixture", TestFixtureFactory.createMockFixture(false));
};

TestFixtureFactory.prototype.getFixture = function(sFixtureName)
{
	return this.m_mFixtures[sFixtureName];
};

TestFixtureFactory.prototype.addFixtureToRegistry = function(oFixtureRegistry, sFixtureName, oFixture)
{
	this.m_mFixtures[sFixtureName] = oFixture;
	oFixtureRegistry.addFixture(sFixtureName, oFixture);
};

TestFixtureFactory.prototype.addMockFixtureToRegistry = function(oFixtureRegistry, sFixtureName, oMockFixture)
{
	this.m_mFixtures[sFixtureName] = oMockFixture;
	oFixtureRegistry.addFixture(sFixtureName, oMockFixture.proxy());
};

module.exports = TestFixtureFactory;
