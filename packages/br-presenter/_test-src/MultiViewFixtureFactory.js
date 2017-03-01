require('../_resources-test-at/html/test-form.html');
require('../_resources-test-at/html/multiple-views-of-one-model.html');
var PresenterComponentFixture = require('br-presenter/testing/PresenterComponentFixture');
var FixtureFactory = require('br-test/FixtureFactory');
var Core = require("br/Core");
MultiViewFixtureFactory = function()
{
};

Core.implement(MultiViewFixtureFactory, FixtureFactory);

MultiViewFixtureFactory.prototype.addFixtures = function(oFixtureRegistry)
{
    require('br-presenter/_test-src/MultiViewPresentationModel');
    oFixtureRegistry.addFixture("multiView",
            new PresenterComponentFixture("multiple-views",
                    'br-presenter/_test-src/MultiViewPresentationModel'));
};

module.exports = MultiViewFixtureFactory;
