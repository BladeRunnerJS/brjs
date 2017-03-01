require('../_resources-test-at/html/test-form.html');
var PresenterComponentFixture = require('br-presenter/testing/PresenterComponentFixture');
var FixtureFactory = require('br-test/FixtureFactory');
var Core = require("br/Core");
PresenterFixtureFactory = function()
{
};

Core.implement(PresenterFixtureFactory, FixtureFactory);

PresenterFixtureFactory.prototype.addFixtures = function(oFixtureRegistry)
{
    require('br-presenter/_test-src/TestPresentationModel');
    oFixtureRegistry.addFixture("demo", new PresenterComponentFixture("test", 'br-presenter/_test-src/TestPresentationModel' ));
};

module.exports = PresenterFixtureFactory;
