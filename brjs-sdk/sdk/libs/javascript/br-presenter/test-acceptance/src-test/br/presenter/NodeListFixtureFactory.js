var PresenterComponentFixture = require("br/presenter/testing/PresenterComponentFixture");
var FixtureFactory = require("br/test/FixtureFactory");
var Core = require("br/Core");
NodeListFixtureFactory = function()
{
};

Core.implement(NodeListFixtureFactory, FixtureFactory);

NodeListFixtureFactory.prototype.addFixtures = function(oFixtureRegistry)
{
    require("br/presenter/NodeListPresentationModel");
    
    oFixtureRegistry.addFixture("component",
            new PresenterComponentFixture("node-list",
                    "br/presenter/NodeListPresentationModel"));
    
    oFixtureRegistry.addFixture("templateAwareComponent",
            new PresenterComponentFixture("template-aware-node-list",
                    "br/presenter/NodeListPresentationModel"));
};

module.exports = NodeListFixtureFactory;
