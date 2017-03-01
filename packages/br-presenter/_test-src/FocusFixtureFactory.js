require('../_resources-test-at/html/test-form.html');
require('../_resources-test-at/html/focus-view-form.html');
var PresenterComponentFixture = require('br-presenter/testing/PresenterComponentFixture');
var FixtureFactory = require('br-test/FixtureFactory');
var Core = require("br/Core");
FocusFixtureFactory = function()
{
};
Core.implement(FocusFixtureFactory, FixtureFactory);

FocusFixtureFactory.prototype.addFixtures = function(oFixtureRegistry)
{
    require('br-presenter/_test-src/FocusPresentationModel');
    oFixtureRegistry.addFixture("focusform",
            new PresenterComponentFixture('focus-view-form-id',
                    'br-presenter/_test-src/FocusPresentationModel'));
};

module.exports = FocusFixtureFactory;
