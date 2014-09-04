br.test.GrandParentTestFixture = function()
{
	this.m_oChildMockFixture = new br.test.ParentTestFixture();
};
br.Core.inherit(br.test.GrandParentTestFixture, br.test.Fixture);

br.test.GrandParentTestFixture.prototype.addSubFixtures = function(oFixtureRegistry)
{
	oFixtureRegistry.addFixture("childFixture", this.m_oChildMockFixture);
};

br.test.GrandParentTestFixture.prototype.canHandleExactMatch = function()
{
	return false;
};

br.test.GrandParentTestFixture.prototype.canHandleProperty = function(sProperty)
{
	return false;
};

br.test.GrandParentTestFixture.prototype.getChildMockFixture = function()
{
	return this.m_oChildMockFixture;
};
