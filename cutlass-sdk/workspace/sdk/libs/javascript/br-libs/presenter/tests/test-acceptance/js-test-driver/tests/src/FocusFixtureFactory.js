FocusFixtureFactory = function()
{
};
br.Core.implement(FocusFixtureFactory, br.test.FixtureFactory);

FocusFixtureFactory.prototype.addFixtures = function(oFixtureRegistry)
{
	oFixtureRegistry.addFixture("focusform", 
			new br.presenter.testing.PresenterComponentFixture('focus-view-form-id',
					'FocusPresentationModel'));
};
