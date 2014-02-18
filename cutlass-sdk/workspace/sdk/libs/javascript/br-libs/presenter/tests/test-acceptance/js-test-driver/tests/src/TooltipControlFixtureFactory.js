TooltipControlFixtureFactory = function()
{
};

TooltipControlFixtureFactory.prototype.addFixtures = function(oFixtureRegistry)
{
	oFixtureRegistry.addFixture("form", 
			new br.presenter.testing.PresenterComponentFixture('tooltip-control-test',
					'TooltipControlPresentationModel')); 
};

br.Core.implement(TooltipControlFixtureFactory, br.test.FixtureFactory);
