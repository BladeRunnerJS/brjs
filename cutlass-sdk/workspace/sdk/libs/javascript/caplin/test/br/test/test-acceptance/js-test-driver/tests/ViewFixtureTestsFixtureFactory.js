ViewFixtureTestsFixtureFactory = function()
{
};

br.Core.implement(ViewFixtureTestsFixtureFactory, br.test.FixtureFactory);

ViewFixtureTestsFixtureFactory.prototype.addFixtures = function(oFixtureRegistry)
{
	oFixtureRegistry.addFixture("page", new TestLoadedDummyFixture());
};

ViewFixtureTestsFixtureFactory.prototype.setUp = function()
{
	// This is to get around an issue in IE10 that prevents focus moving correctly when .focus() is called on
	// an element in some circumstances.
	document.body.focus();
};
