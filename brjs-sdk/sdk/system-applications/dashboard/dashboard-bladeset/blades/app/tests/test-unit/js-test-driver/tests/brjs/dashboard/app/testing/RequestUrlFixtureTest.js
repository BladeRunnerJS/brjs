(function() {
    var XhrFactoryStub = require("brjs/dashboard/app/testing/XhrFactoryStub");
    var RequestUrlFixture = require("brjs/dashboard/app/testing/RequestUrlFixture");
    
    RequestUrlFixtureTest = TestCase("RequestUrlFixtureTest");

    RequestUrlFixtureTest.prototype.setUp = function()
    {
        this.m_oXhrFactoryStub = new XhrFactoryStub();
        this.m_oRequestUrlFixture = new RequestUrlFixture();
        this.m_oRequestUrlFixture.setXhrFactory(this.m_oXhrFactoryStub);
    };

    // TODO: add the other tests
    RequestUrlFixtureTest.prototype.testWeCanMakeAssertionsAboutRequestsBeforeTheResponseHasComeBack = function()
    {
        var oXhrStub = this.m_oXhrFactoryStub.getRequestObject();
        
        oXhrStub.open("GET", "/the-url");
        oXhrStub.send(null);
        this.m_oRequestUrlFixture.doThen("requestSent", "GET /the-url");
    };
})();
