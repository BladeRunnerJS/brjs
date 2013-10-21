ControlPluginFixtureFactory = function()
{
};

br.implement(ControlPluginFixtureFactory, br.test.FixtureFactory);

ControlPluginFixtureFactory.prototype.addFixtures = function(oFixtureRegistry)
{
	oFixtureRegistry.addFixture("component", 
			new br.presenter.testing.PresenterComponentFixture("control-plugin",
					"ControlPluginPresentationModel"));
};
