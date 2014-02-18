PresenterFixtureFactory = function()
{
};

PresenterFixtureFactory.prototype.addFixtures = function(oFixtureRegistry)
{
	oFixtureRegistry.addFixture("demo", new br.presenter.testing.PresenterComponentFixture("test", "TestPresentationModel"));
};

br.Core.implement(PresenterFixtureFactory, br.test.FixtureFactory);
