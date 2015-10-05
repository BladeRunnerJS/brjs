var PresenterComponentFixture = require("br/presenter/testing/PresenterComponentFixture");
var FixtureFactory = require("br/test/FixtureFactory");
var Core = require("br/Core");
ControlPluginFixtureFactory = function()
{
};

Core.implement(ControlPluginFixtureFactory, FixtureFactory);

ControlPluginFixtureFactory.prototype.addFixtures = function(oFixtureRegistry)
{
    var ControlAdaptorFactory = require("br/presenter/control/ControlAdaptorFactory");
    var TestControl = require("br/presenter/TestControl");
    ControlAdaptorFactory.registerConfiguredControlAdaptor('caplin-test-control', TestControl, {});
    
    require("br/presenter/ControlPluginPresentationModel");
    oFixtureRegistry.addFixture("component",
            new PresenterComponentFixture("control-plugin",
                    "br/presenter/ControlPluginPresentationModel"));
};

module.exports = ControlPluginFixtureFactory;
