MultiViewClickFixtureFactory = function()
{
};
	
MultiViewClickFixtureFactory.prototype.addFixtures = function(oFixtureRegistry)
{
	oFixtureRegistry.addFixture("multiViewClick", 
			new br.presenter.testing.PresenterComponentFixture("multi-view-click",
					"MultiViewClickPresentationModel"));
};

br.Core.implement(MultiViewClickFixtureFactory, br.test.FixtureFactory);