br.test.ParentTestFixture = function()
{
	this.m_oFirstMockFixture = br.test.TestFixtureFactory.createMockFixture(false);
	this.m_oSecondMockFixture = br.test.TestFixtureFactory.createMockFixture(false);
};
br.Core.inherit(br.test.ParentTestFixture, br.test.Fixture);

br.test.ParentTestFixture.prototype.addSubFixtures = function(oFixtureRegistry)
{
	oFixtureRegistry.addFixture("subFixture1", this.m_oFirstMockFixture.proxy());
	oFixtureRegistry.addFixture("subFixture2", this.m_oSecondMockFixture.proxy());
};

br.test.ParentTestFixture.prototype.canHandleExactMatch = function()
{
	return false;
};

br.test.ParentTestFixture.prototype.canHandleProperty = function(sProperty)
{
	return false;
};

br.test.ParentTestFixture.prototype.getFirstMockFixture = function()
{
	return this.m_oFirstMockFixture;
};

br.test.ParentTestFixture.prototype.getSecondMockFixture = function()
{
	return this.m_oSecondMockFixture;
};
