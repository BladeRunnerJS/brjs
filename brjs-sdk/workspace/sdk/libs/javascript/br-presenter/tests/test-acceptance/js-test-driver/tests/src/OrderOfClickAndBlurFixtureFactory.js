OrderOfClickAndBlurFixtureFactory = function()
{
};

br.Core.implement(OrderOfClickAndBlurFixtureFactory, br.test.FixtureFactory);

OrderOfClickAndBlurFixtureFactory.prototype.addFixtures = function(oFixtureRegistry)
{
	oFixtureRegistry.addFixture("form", 
			new br.presenter.testing.PresenterComponentFixture("order-of-click-and-blur",
					"OrderOfClickAndBlurPresentationModel"));
};
