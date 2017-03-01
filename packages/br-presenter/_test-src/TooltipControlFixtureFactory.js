require('../_resources-test-at/html/test-form.html');
require('../_resources-test-at/html/tooltip-control.html');
var PresenterComponentFixture = require('br-presenter/testing/PresenterComponentFixture');
var FixtureFactory = require('br-test/FixtureFactory');
var Core = require("br/Core");
TooltipControlFixtureFactory = function()
{
};

Core.implement(TooltipControlFixtureFactory, FixtureFactory);

TooltipControlFixtureFactory.prototype.addFixtures = function(oFixtureRegistry)
{
    require('br-presenter/_test-src/TooltipControlPresentationModel');
    oFixtureRegistry.addFixture("form",
            new PresenterComponentFixture('tooltip-control-test',
                    'br-presenter/_test-src/TooltipControlPresentationModel'));
};

module.exports = TooltipControlFixtureFactory;
