var PresenterComponentFixture = require("br/presenter/testing/PresenterComponentFixture");
var FixtureFactory = require("br/test/FixtureFactory");
var Core = require("br/Core");
LegacyTemplateFixtureFactory = function()
{
};

Core.implement(LegacyTemplateFixtureFactory, FixtureFactory);

LegacyTemplateFixtureFactory.prototype.addFixtures = function(oFixtureRegistry)
{
    require("br/presenter/TestPresentationModel");
    oFixtureRegistry.addFixture("legacy",
            new PresenterComponentFixture("legacy",
                    "br/presenter/TestPresentationModel" ));
};

module.exports = LegacyTemplateFixtureFactory;
