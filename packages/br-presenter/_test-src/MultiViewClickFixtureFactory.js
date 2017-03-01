require('../_resources-test-at/html/test-form.html');
require('../_resources-test-at/html/multi-view-click.html');
var PresenterComponentFixture = require('br-presenter/testing/PresenterComponentFixture');
var FixtureFactory = require('br-test/FixtureFactory');
var Core = require("br/Core");
MultiViewClickFixtureFactory = function()
{
};

Core.implement(MultiViewClickFixtureFactory, FixtureFactory);

MultiViewClickFixtureFactory.prototype.addFixtures = function(oFixtureRegistry)
{
    require('br-presenter/_test-src/MultiViewClickPresentationModel');
    oFixtureRegistry.addFixture("multiViewClick",
            new PresenterComponentFixture("multi-view-click",
                    'br-presenter/_test-src/MultiViewClickPresentationModel'));
};

module.exports = MultiViewClickFixtureFactory;
