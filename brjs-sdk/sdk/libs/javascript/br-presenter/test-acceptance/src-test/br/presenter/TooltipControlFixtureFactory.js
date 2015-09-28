var PresenterComponentFixture = require("br/presenter/testing/PresenterComponentFixture");
var FixtureFactory = require("br/test/FixtureFactory");
var Core = require("br/Core");
TooltipControlFixtureFactory = function()
{
};

Core.implement(TooltipControlFixtureFactory, FixtureFactory);

TooltipControlFixtureFactory.prototype.addFixtures = function(oFixtureRegistry)
{
    require('br/presenter/TooltipControlPresentationModel');
    oFixtureRegistry.addFixture("form",
            new PresenterComponentFixture('tooltip-control-test',
                    'br/presenter/TooltipControlPresentationModel'));
};

module.exports = TooltipControlFixtureFactory;
