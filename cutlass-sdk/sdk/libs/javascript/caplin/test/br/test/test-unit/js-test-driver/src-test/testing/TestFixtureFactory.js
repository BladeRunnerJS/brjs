testing.TestFixtureFactory = function()
{
	this.m_mFixtures = {};
};
br.implement(testing.TestFixtureFactory, br.test.FixtureFactory);

testing.TestFixtureFactory.createMockFixture = function(bCanHandleExactMatch, bApplyTearDownStub)
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

testing.TestFixtureFactory.prototype.addFixtures = function(oFixtureRegistry)
{
	this.addMockFixtureToRegistry(oFixtureRegistry, "fixture", testing.TestFixtureFactory.createMockFixture(false, false));
	this.addMockFixtureToRegistry(oFixtureRegistry, "propertyFixture", testing.TestFixtureFactory.createMockFixture(true));
	this.addFixtureToRegistry(oFixtureRegistry, "parentFixture", new testing.ParentTestFixture());
	this.addFixtureToRegistry(oFixtureRegistry, "grandParentFixture", new testing.GrandParentTestFixture());
	this.addMockFixtureToRegistry(oFixtureRegistry, "another=fixture", testing.TestFixtureFactory.createMockFixture(false));
};

testing.TestFixtureFactory.prototype.getFixture = function(sFixtureName)
{
	return this.m_mFixtures[sFixtureName];
};

testing.TestFixtureFactory.prototype.addFixtureToRegistry = function(oFixtureRegistry, sFixtureName, oFixture)
{
	this.m_mFixtures[sFixtureName] = oFixture;
	oFixtureRegistry.addFixture(sFixtureName, oFixture);
};

testing.TestFixtureFactory.prototype.addMockFixtureToRegistry = function(oFixtureRegistry, sFixtureName, oMockFixture)
{
	this.m_mFixtures[sFixtureName] = oMockFixture;
	oFixtureRegistry.addFixture(sFixtureName, oMockFixture.proxy());
};
