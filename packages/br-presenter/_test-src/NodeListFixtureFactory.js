require('../_resources-test-at/html/test-form.html');
require('../_resources-test-at/html/node-list.html');
require('../_resources-test-at/html/template-aware-node-list.html');
require('../_resources-test-at/html/node-list-item.html');
var PresenterComponentFixture = require('br-presenter/testing/PresenterComponentFixture');
var FixtureFactory = require('br-test/FixtureFactory');
var Core = require("br/Core");
NodeListFixtureFactory = function()
{
};

Core.implement(NodeListFixtureFactory, FixtureFactory);

NodeListFixtureFactory.prototype.addFixtures = function(oFixtureRegistry)
{
    require('br-presenter/_test-src/NodeListPresentationModel');
    
    oFixtureRegistry.addFixture("component",
            new PresenterComponentFixture("node-list",
                    'br-presenter/_test-src/NodeListPresentationModel'));
    
    oFixtureRegistry.addFixture("templateAwareComponent",
            new PresenterComponentFixture("template-aware-node-list",
                    'br-presenter/_test-src/NodeListPresentationModel'));
};

module.exports = NodeListFixtureFactory;
