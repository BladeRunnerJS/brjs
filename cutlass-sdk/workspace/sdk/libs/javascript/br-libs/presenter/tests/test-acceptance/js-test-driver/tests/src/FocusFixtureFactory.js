FocusFixtureFactory = function()
{
};

FocusFixtureFactory.prototype.addFixtures = function(oFixtureRegistry)
{
	oFixtureRegistry.addFixture("focusform", 
			new br.presenter.testing.PresenterComponentFixture('focus-view-form-id',
					'FocusPresentationModel'));
};

br.Core.implement(FocusFixtureFactory, br.test.FixtureFactory);