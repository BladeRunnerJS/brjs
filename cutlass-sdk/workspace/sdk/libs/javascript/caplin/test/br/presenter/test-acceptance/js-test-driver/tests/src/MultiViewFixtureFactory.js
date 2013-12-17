MultiViewFixtureFactory = function()
{
};

br.Core.implement(MultiViewFixtureFactory, br.test.FixtureFactory);
	
MultiViewFixtureFactory.prototype.addFixtures = function(oFixtureRegistry)
{
	oFixtureRegistry.addFixture("multiView", 
			new br.presenter.testing.PresenterComponentFixture("multiple-views",
					"MultiViewPresentationModel"));
};
