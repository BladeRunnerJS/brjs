LegacyTemplateFixtureFactory = function()
{
};

LegacyTemplateFixtureFactory.prototype.addFixtures = function(oFixtureRegistry)
{
	oFixtureRegistry.addFixture("legacy", 
			new br.presenter.testing.PresenterComponentFixture("legacy",
					"TestPresentationModel"));
};

br.Core.implement(LegacyTemplateFixtureFactory, br.test.FixtureFactory);