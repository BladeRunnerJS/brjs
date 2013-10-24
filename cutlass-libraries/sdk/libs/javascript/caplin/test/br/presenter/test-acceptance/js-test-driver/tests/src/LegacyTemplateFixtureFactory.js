LegacyTemplateFixtureFactory = function()
{
};

br.implement(LegacyTemplateFixtureFactory, br.test.FixtureFactory);

LegacyTemplateFixtureFactory.prototype.addFixtures = function(oFixtureRegistry)
{
	oFixtureRegistry.addFixture("legacy", 
			new br.presenter.testing.PresenterComponentFixture("legacy",
					"TestPresentationModel"));
};
