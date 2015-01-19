TooltipPluginFixtureFactory = function()
{
};

br.Core.implement(TooltipPluginFixtureFactory, br.test.FixtureFactory);

TooltipPluginFixtureFactory.prototype.addFixtures = function(oFixtureRegistry)
{

	oFixtureRegistry.addFixture("form", 
			new br.presenter.testing.PresenterComponentFixture('tooltip_plugin_test',
					'TooltipPresentationModel')); 

};
