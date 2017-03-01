require('../_resources-test-at/html/test-form.html');
require('../_resources-test-at/html/control-plugin.html');
require('../_resources-test-at/html/control-plugin-node-list-item.html');
var PresenterComponentFixture = require('br-presenter/testing/PresenterComponentFixture');
var FixtureFactory = require('br-test/FixtureFactory');
var Core = require("br/Core");
ControlPluginFixtureFactory = function()
{
};

Core.implement(ControlPluginFixtureFactory, FixtureFactory);

ControlPluginFixtureFactory.prototype.addFixtures = function(oFixtureRegistry)
{
    var ControlAdaptorFactory = require('br-presenter/control/ControlAdaptorFactory');
    var TestControl = require('br-presenter/_test-src/TestControl');
    ControlAdaptorFactory.registerConfiguredControlAdaptor('caplin-test-control', TestControl, {});
    
    require('br-presenter/_test-src/ControlPluginPresentationModel');
    oFixtureRegistry.addFixture("component",
            new PresenterComponentFixture("control-plugin",
                    'br-presenter/_test-src/ControlPluginPresentationModel'));
};

module.exports = ControlPluginFixtureFactory;
