OrderOfClickAndBlurFixtureFactory = function()
{
};

OrderOfClickAndBlurFixtureFactory.prototype.addFixtures = function(oFixtureRegistry)
{
	oFixtureRegistry.addFixture("form", 
			new br.presenter.testing.PresenterComponentFixture("order-of-click-and-blur",
					"OrderOfClickAndBlurPresentationModel"));
};

br.Core.implement(OrderOfClickAndBlurFixtureFactory, br.test.FixtureFactory);
