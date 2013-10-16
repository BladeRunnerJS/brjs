testing.ParentTestFixture = function()
{
	this.m_oFirstMockFixture = testing.TestFixtureFactory.createMockFixture(false);
	this.m_oSecondMockFixture = testing.TestFixtureFactory.createMockFixture(false);
};
br.provide(testing.ParentTestFixture, br.test.Fixture);

testing.ParentTestFixture.prototype.addSubFixtures = function(oFixtureRegistry)
{
	oFixtureRegistry.addFixture("subFixture1", this.m_oFirstMockFixture.proxy());
	oFixtureRegistry.addFixture("subFixture2", this.m_oSecondMockFixture.proxy());
};

testing.ParentTestFixture.prototype.canHandleExactMatch = function()
{
	return false;
};

testing.ParentTestFixture.prototype.canHandleProperty = function(sProperty)
{
	return false;
};

testing.ParentTestFixture.prototype.getFirstMockFixture = function()
{
	return this.m_oFirstMockFixture;
};

testing.ParentTestFixture.prototype.getSecondMockFixture = function()
{
	return this.m_oSecondMockFixture;
};
