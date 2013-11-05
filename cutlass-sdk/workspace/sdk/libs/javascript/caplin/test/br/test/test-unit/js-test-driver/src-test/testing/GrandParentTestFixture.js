testing.GrandParentTestFixture = function()
{
	this.m_oChildMockFixture = new testing.ParentTestFixture();
};
br.inherit(testing.GrandParentTestFixture, br.test.Fixture);

testing.GrandParentTestFixture.prototype.addSubFixtures = function(oFixtureRegistry)
{
	oFixtureRegistry.addFixture("childFixture", this.m_oChildMockFixture);
};

testing.GrandParentTestFixture.prototype.canHandleExactMatch = function()
{
	return false;
};

testing.GrandParentTestFixture.prototype.canHandleProperty = function(sProperty)
{
	return false;
};

testing.GrandParentTestFixture.prototype.getChildMockFixture = function()
{
	return this.m_oChildMockFixture;
};
