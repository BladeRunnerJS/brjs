NodeListFixtureFactory = function()
{
};

br.Core.implement(NodeListFixtureFactory, br.test.FixtureFactory);

NodeListFixtureFactory.prototype.addFixtures = function(oFixtureRegistry)
{
	oFixtureRegistry.addFixture("component", 
			new br.presenter.testing.PresenterComponentFixture("node-list",
					"NodeListPresentationModel"));
	
	oFixtureRegistry.addFixture("templateAwareComponent", 
			new br.presenter.testing.PresenterComponentFixture("template-aware-node-list",
					"NodeListPresentationModel"));
};
