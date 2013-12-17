PresenterFixtureFactory = function()
{
};

br.Core.implement(PresenterFixtureFactory, br.test.FixtureFactory);

PresenterFixtureFactory.prototype.addFixtures = function(oFixtureRegistry)
{
	oFixtureRegistry.addFixture("demo", new br.presenter.testing.PresenterComponentFixture("test", "TestPresentationModel"));
};
