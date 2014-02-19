ControlPluginFixtureFactory = function()
{
};

br.Core.implement(ControlPluginFixtureFactory, br.test.FixtureFactory);

ControlPluginFixtureFactory.prototype.addFixtures = function(oFixtureRegistry)
{
	oFixtureRegistry.addFixture("component", 
			new br.presenter.testing.PresenterComponentFixture("control-plugin",
					"ControlPluginPresentationModel"));
};
