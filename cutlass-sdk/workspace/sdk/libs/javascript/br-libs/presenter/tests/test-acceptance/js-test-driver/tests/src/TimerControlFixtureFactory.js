TimerControlFixtureFactory = function()
{
};

TimerControlFixtureFactory.prototype.addFixtures = function(oFixtureRegistry)
{
	oFixtureRegistry.addFixture("component", 
			new br.presenter.testing.PresenterComponentFixture("timer-control",
					"TimerControlPresentationModel"));
};

br.Core.implement(TimerControlFixtureFactory, br.test.FixtureFactory);
