var PresenterComponentFixture = require("br/presenter/testing/PresenterComponentFixture");
var FixtureFactory = require("br/test/FixtureFactory");
var Core = require("br/Core");
OrderOfClickAndBlurFixtureFactory = function()
{
};

Core.implement(OrderOfClickAndBlurFixtureFactory, FixtureFactory);

OrderOfClickAndBlurFixtureFactory.prototype.addFixtures = function(oFixtureRegistry)
{
    require("br/presenter/OrderOfClickAndBlurPresentationModel");
    oFixtureRegistry.addFixture("form",
            new PresenterComponentFixture("order-of-click-and-blur",
                    "br/presenter/OrderOfClickAndBlurPresentationModel"));
};

module.exports = OrderOfClickAndBlurFixtureFactory;
