var PresenterComponentFixture = require("br/presenter/testing/PresenterComponentFixture");
var FixtureFactory = require("br/test/FixtureFactory");
var Core = require("br/Core");
MultiViewFixtureFactory = function()
{
};

Core.implement(MultiViewFixtureFactory, FixtureFactory);

MultiViewFixtureFactory.prototype.addFixtures = function(oFixtureRegistry)
{
    require("br/presenter/MultiViewPresentationModel");
    oFixtureRegistry.addFixture("multiView",
            new PresenterComponentFixture("multiple-views",
                    "br/presenter/MultiViewPresentationModel"));
};

module.exports = MultiViewFixtureFactory;
