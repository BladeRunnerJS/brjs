TimerControlFixtureFactory = function()
{
};

br.Core.implement(TimerControlFixtureFactory, br.test.FixtureFactory);

TimerControlFixtureFactory.prototype.addFixtures = function(oFixtureRegistry)
{
	oFixtureRegistry.addFixture("component", 
			new br.presenter.testing.PresenterComponentFixture("timer-control",
					"TimerControlPresentationModel"));
};