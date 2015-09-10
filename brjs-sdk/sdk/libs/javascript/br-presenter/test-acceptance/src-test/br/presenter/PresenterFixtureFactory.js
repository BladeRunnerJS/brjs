var PresenterComponentFixture = require("br/presenter/testing/PresenterComponentFixture");
var FixtureFactory = require("br/test/FixtureFactory");
var Core = require("br/Core");
PresenterFixtureFactory = function()
{
};

Core.implement(PresenterFixtureFactory, FixtureFactory);

PresenterFixtureFactory.prototype.addFixtures = function(oFixtureRegistry)
{
    require("br/presenter/TestPresentationModel");
    oFixtureRegistry.addFixture("demo", new PresenterComponentFixture("test", "br/presenter/TestPresentationModel" ));
};

module.exports = PresenterFixtureFactory;
