var PresenterComponentFixture = require("br/presenter/testing/PresenterComponentFixture");
var FixtureFactory = require("br/test/FixtureFactory");
var Core = require("br/Core");
TimerControlFixtureFactory = function()
{
};

Core.implement(TimerControlFixtureFactory, FixtureFactory);

TimerControlFixtureFactory.prototype.addFixtures = function(oFixtureRegistry)
{
    require("br/presenter/TimerControlPresentationModel");
    oFixtureRegistry.addFixture("component",
            new PresenterComponentFixture("timer-control",
                    "br/presenter/TimerControlPresentationModel"));
};

module.exports = TimerControlFixtureFactory;
