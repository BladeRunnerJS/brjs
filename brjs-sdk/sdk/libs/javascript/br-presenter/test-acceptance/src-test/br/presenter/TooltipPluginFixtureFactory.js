var PresenterComponentFixture = require("br/presenter/testing/PresenterComponentFixture");
var FixtureFactory = require("br/test/FixtureFactory");
var Core = require("br/Core");
TooltipPluginFixtureFactory = function()
{
};

Core.implement(TooltipPluginFixtureFactory, FixtureFactory);

TooltipPluginFixtureFactory.prototype.addFixtures = function(oFixtureRegistry)
{
    require("br/presenter/TooltipPresentationModel");
    oFixtureRegistry.addFixture("form",
            new PresenterComponentFixture('tooltip_plugin_test',
                    'br/presenter/TooltipPresentationModel'));

};

module.exports = TooltipPluginFixtureFactory;
