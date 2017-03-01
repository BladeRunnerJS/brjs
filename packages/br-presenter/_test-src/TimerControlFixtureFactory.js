require('../_resources-test-at/html/test-form.html');
require('../_resources-test-at/html/timer-control.html');
var PresenterComponentFixture = require('br-presenter/testing/PresenterComponentFixture');
var FixtureFactory = require('br-test/FixtureFactory');
var Core = require("br/Core");
TimerControlFixtureFactory = function()
{
};

Core.implement(TimerControlFixtureFactory, FixtureFactory);

TimerControlFixtureFactory.prototype.addFixtures = function(oFixtureRegistry)
{
    require('br-presenter/_test-src/TimerControlPresentationModel');
    oFixtureRegistry.addFixture("component",
            new PresenterComponentFixture("timer-control",
                    'br-presenter/_test-src/TimerControlPresentationModel'));
};

module.exports = TimerControlFixtureFactory;
