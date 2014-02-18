ControlPluginFixtureFactory = function()
{
};

ControlPluginFixtureFactory.prototype.addFixtures = function(oFixtureRegistry)
{
	oFixtureRegistry.addFixture("component", 
			new br.presenter.testing.PresenterComponentFixture("control-plugin",
					"ControlPluginPresentationModel"));
};

br.Core.implement(ControlPluginFixtureFactory, br.test.FixtureFactory);