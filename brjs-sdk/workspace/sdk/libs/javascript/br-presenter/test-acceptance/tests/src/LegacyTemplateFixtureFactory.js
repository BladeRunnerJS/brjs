LegacyTemplateFixtureFactory = function()
{
};

br.Core.implement(LegacyTemplateFixtureFactory, br.test.FixtureFactory);

LegacyTemplateFixtureFactory.prototype.addFixtures = function(oFixtureRegistry)
{
	oFixtureRegistry.addFixture("legacy", 
			new br.presenter.testing.PresenterComponentFixture("legacy",
					"TestPresentationModel"));
};
