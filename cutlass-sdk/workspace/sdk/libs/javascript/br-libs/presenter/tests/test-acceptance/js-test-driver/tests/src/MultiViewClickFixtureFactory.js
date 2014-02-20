MultiViewClickFixtureFactory = function()
{
};

br.Core.implement(MultiViewClickFixtureFactory, br.test.FixtureFactory);
	
MultiViewClickFixtureFactory.prototype.addFixtures = function(oFixtureRegistry)
{
	oFixtureRegistry.addFixture("multiViewClick", 
			new br.presenter.testing.PresenterComponentFixture("multi-view-click",
					"MultiViewClickPresentationModel"));
};
