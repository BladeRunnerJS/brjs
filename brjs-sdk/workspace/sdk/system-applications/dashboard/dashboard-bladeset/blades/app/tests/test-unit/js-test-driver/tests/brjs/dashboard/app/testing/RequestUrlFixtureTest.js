RequestUrlFixtureTest = TestCase("RequestUrlFixtureTest");

RequestUrlFixtureTest.prototype.setUp = function()
{
	this.m_oXhrFactoryStub = new brjs.dashboard.app.testing.XhrFactoryStub();
	this.m_oRequestUrlFixture = new brjs.dashboard.app.testing.RequestUrlFixture();
	this.m_oRequestUrlFixture.setXhrFactory(this.m_oXhrFactoryStub);
};

RequestUrlFixtureTest.prototype.testWeCanMakeAssertionsAboutRequestsBeforeTheResponseHasComeBack = function()
{
	var oXhrStub = this.m_oXhrFactoryStub.getRequestObject();
	
	oXhrStub.open("GET", "/the-url");
	oXhrStub.send(null);
	this.m_oRequestUrlFixture.doThen("requestSent", "GET /the-url"); 
};

// TODO: add the other tests