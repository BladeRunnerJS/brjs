br.test.TestFixtureFactory = function()
{
	this.m_mFixtures = {};
};
br.Core.implement(br.test.TestFixtureFactory, br.test.FixtureFactory);

br.test.TestFixtureFactory.createMockFixture = function(bCanHandleExactMatch, bApplyTearDownStub)
{
	var oMockFixture = mock(br.test.Fixture);
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

br.test.TestFixtureFactory.prototype.addFixtures = function(oFixtureRegistry)
{
	this.addMockFixtureToRegistry(oFixtureRegistry, "fixture", br.test.TestFixtureFactory.createMockFixture(false, false));
	this.addMockFixtureToRegistry(oFixtureRegistry, "propertyFixture", br.test.TestFixtureFactory.createMockFixture(true));
	this.addFixtureToRegistry(oFixtureRegistry, "parentFixture", new br.test.ParentTestFixture());
	this.addFixtureToRegistry(oFixtureRegistry, "grandParentFixture", new br.test.GrandParentTestFixture());
	this.addMockFixtureToRegistry(oFixtureRegistry, "another=fixture", br.test.TestFixtureFactory.createMockFixture(false));
};

br.test.TestFixtureFactory.prototype.getFixture = function(sFixtureName)
{
	return this.m_mFixtures[sFixtureName];
};

br.test.TestFixtureFactory.prototype.addFixtureToRegistry = function(oFixtureRegistry, sFixtureName, oFixture)
{
	this.m_mFixtures[sFixtureName] = oFixture;
	oFixtureRegistry.addFixture(sFixtureName, oFixture);
};

br.test.TestFixtureFactory.prototype.addMockFixtureToRegistry = function(oFixtureRegistry, sFixtureName, oMockFixture)
{
	this.m_mFixtures[sFixtureName] = oMockFixture;
	oFixtureRegistry.addFixture(sFixtureName, oMockFixture.proxy());
};
