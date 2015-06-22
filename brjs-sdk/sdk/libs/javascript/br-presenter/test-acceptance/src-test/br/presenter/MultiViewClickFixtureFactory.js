var PresenterComponentFixture = require("br/presenter/testing/PresenterComponentFixture");
var FixtureFactory = require("br/test/FixtureFactory");
var Core = require("br/Core");
MultiViewClickFixtureFactory = function()
{
};

Core.implement(MultiViewClickFixtureFactory, FixtureFactory);

MultiViewClickFixtureFactory.prototype.addFixtures = function(oFixtureRegistry)
{
    require("br/presenter/MultiViewClickPresentationModel");
    oFixtureRegistry.addFixture("multiViewClick",
            new PresenterComponentFixture("multi-view-click",
                    "br/presenter/MultiViewClickPresentationModel"));
};

module.exports = MultiViewClickFixtureFactory;
