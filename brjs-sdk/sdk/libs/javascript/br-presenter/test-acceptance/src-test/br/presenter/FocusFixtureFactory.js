var PresenterComponentFixture = require("br/presenter/testing/PresenterComponentFixture");
var FixtureFactory = require("br/test/FixtureFactory");
var Core = require("br/Core");
FocusFixtureFactory = function()
{
};
Core.implement(FocusFixtureFactory, FixtureFactory);

FocusFixtureFactory.prototype.addFixtures = function(oFixtureRegistry)
{
    require('br/presenter/FocusPresentationModel');
    oFixtureRegistry.addFixture("focusform",
            new PresenterComponentFixture('focus-view-form-id',
                    'br/presenter/FocusPresentationModel'));
};

module.exports = FocusFixtureFactory;
