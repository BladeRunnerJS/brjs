require('../_resources-test-at/html/test-form.html');
require('../_resources-test-at/html/order-of-click-and-blur-form.html');
var PresenterComponentFixture = require('br-presenter/testing/PresenterComponentFixture');
var FixtureFactory = require('br-test/FixtureFactory');
var Core = require("br/Core");
OrderOfClickAndBlurFixtureFactory = function()
{
};

Core.implement(OrderOfClickAndBlurFixtureFactory, FixtureFactory);

OrderOfClickAndBlurFixtureFactory.prototype.addFixtures = function(oFixtureRegistry)
{
    require('br-presenter/_test-src/OrderOfClickAndBlurPresentationModel');
    oFixtureRegistry.addFixture("form",
            new PresenterComponentFixture("order-of-click-and-blur",
                    'br-presenter/_test-src/OrderOfClickAndBlurPresentationModel'));
};

module.exports = OrderOfClickAndBlurFixtureFactory;
