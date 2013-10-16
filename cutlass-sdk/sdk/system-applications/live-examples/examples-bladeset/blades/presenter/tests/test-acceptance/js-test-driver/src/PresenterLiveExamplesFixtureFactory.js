novobank = {example:{}};

PresenterLiveExamplesFixtureFactory = function()
{
};
caplin.implement(PresenterLiveExamplesFixtureFactory, caplin.testing.FixtureFactory);

PresenterLiveExamplesFixtureFactory.prototype.addFixtures = function(oFixtureRegistry) {
	oFixtureRegistry.addFixture("example", new caplin.presenter.testing.PresenterComponentFixture('view-template', 
			'novobank.example.DemoPresentationModel'));
	
	oFixtureRegistry.addFixture("alert.triggered", new caplin.testing.AlertFixture()); 
};

PresenterLiveExamplesFixtureFactory.prototype.setUp = function() {
	caplin.core.ServiceRegistry.registerService("br.html-service", new caplin.services.testing.TestCaplinHtmlResourceService("/test/examples-bladeset/blades/presenter/tests/test-acceptance/js-test-driver/bundles/html.bundle"));
	caplin.core.ServiceRegistry.registerService("br.xml-service", new caplin.services.testing.TestCaplinXmlResourceService("/test/examples-bladeset/blades/presenter/tests/test-acceptance/js-test-driver/bundles/xml.bundle"));
};

caplin.testing.GwtTestRunner.initialize();