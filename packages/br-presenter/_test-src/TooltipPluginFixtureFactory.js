require('../_resources-test-at/html/test-form.html');
require('../_resources-test-at/html/tooltip-template.html');
var PresenterComponentFixture = require('br-presenter/testing/PresenterComponentFixture');
var FixtureFactory = require('br-test/FixtureFactory');
var Core = require("br/Core");
TooltipPluginFixtureFactory = function()
{
};

Core.implement(TooltipPluginFixtureFactory, FixtureFactory);

TooltipPluginFixtureFactory.prototype.addFixtures = function(oFixtureRegistry)
{
    require('br-presenter/_test-src/TooltipPresentationModel');
    oFixtureRegistry.addFixture("form",
            new PresenterComponentFixture('tooltip_plugin_test',
                    'br-presenter/_test-src/TooltipPresentationModel'));

};

module.exports = TooltipPluginFixtureFactory;
