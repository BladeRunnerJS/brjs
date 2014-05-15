
DashboardProviderTest = TestCase("DashboardProviderTest");

br.Core.thirdparty("mock4js");

DashboardProviderTest.prototype.setUp = function() {
	Mock4JS.addMockSupport(window);
	Mock4JS.clearMocksToVerify();
	
	this.mockRequest = mock(DummyXmlHttpRequest);
	
	this.provider = new caplinx.dashboard.app.service.dashboard.DashboardProvider(new MockXHRFactory(this.mockRequest.proxy()), "servlet/app-manager");
	
	this.responseStatusCode = -1;
	this.responseBody = "";
	this.errorResponseBody = "";
}

DashboardProviderTest.prototype.tearDown = function() {
	Mock4JS.verifyAllMocks();
	this.mockRequest = null;
	this.provider = null;
}


/** TESTS **/

DashboardProviderTest.prototype.testGettingApps_OkResponse = function () {
	this.expectRequest("GET", "servlet/app-manager/apps", null);

	this.provider.getApps(this.successCallback.bind(this), this.errorCallback.bind(this))
	
	this.triggerResponse(200, "[\"app1\", \"app2\"]");
  	
  	assertEquals( ["app1","app2"], this.responseBody );
};
DashboardProviderTest.prototype.testGettingApps_ErrorResponse = function () {	
	this.expectRequest("GET", "servlet/app-manager/apps", null);
	
	this.provider.getApps(this.successCallback.bind(this), this.errorCallback.bind(this))
	
	var sErrorResponseBody = "{\"cause\":\"Server Error!\",\"message\":\"There was a server error.\"}";
	this.triggerResponse(500, sErrorResponseBody);

  	assertEquals( "There was a server error.", this.errorResponseBody );
};



DashboardProviderTest.prototype.testGettingASingleApp_OkResponse = function () {
	this.expectRequest("GET", "servlet/app-manager/apps/app1", null);
	
	this.provider.getApp("app1", this.successCallback.bind(this), this.errorCallback.bind(this))
		
	this.triggerResponse(200, "{\"a-bladeset\":[\"blade1\", \"blade2\"], \"another-bladeset\":[\"blade1\", \"blade2\"]}");
	
  	assertEquals( {"a-bladeset":["blade1","blade2"],"another-bladeset":["blade1","blade2"]}, this.responseBody );
};
DashboardProviderTest.prototype.testGettingASingleApp_ErrorResponse = function () {
	this.expectRequest("GET", "servlet/app-manager/apps/app1", null);
	
	this.provider.getApp("app1", this.successCallback.bind(this), this.errorCallback.bind(this))
		
	var sErrorResponseBody = "{\"cause\":\"Server Error!\",\"message\":\"There was a server error.\"}";
	this.triggerResponse(500, sErrorResponseBody);
  	
  	assertEquals( "There was a server error.", this.errorResponseBody );
};

DashboardProviderTest.prototype.testGettingAppImage = function () {
  	assertEquals( "servlet/app-manager/apps/app1/thumb", this.provider.getAppImageUrl("app1") );
};

DashboardProviderTest.prototype.testImportBlades_OkResponse = function () {	
	var oRequest = {
		command:"import-blades",
		app:"sourceApp",
		bladesets:{someBladeset:["blade1","blade2"],anotherBladeset:["blade3","blade4"]}
	}
	this.expectRequest("POST", "servlet/app-manager/apps/destinationApp", oRequest);
		
	this.provider.importBlades("sourceApp", {someBladeset:["blade1","blade2"],anotherBladeset:["blade3","blade4"]}, "destinationApp", this.successCallback.bind(this), this.errorCallback.bind(this))
	
	this.triggerResponse(200, "");
	
  	assertEquals( "", this.responseBody );
}
DashboardProviderTest.prototype.testImportBlades_ErrorResponse = function () {	
	var oRequest = {
		command:"import-blades",
		app:"sourceApp",
		bladesets:{someBladeset:["blade1","blade2"]}
	}
	this.expectRequest("POST", "servlet/app-manager/apps/destinationApp", oRequest);
		
	this.provider.importBlades("sourceApp", {someBladeset:["blade1","blade2"]}, "destinationApp", this.successCallback.bind(this), this.errorCallback.bind(this))
	
	var sErrorResponseBody = "{\"cause\":\"Server Error!\",\"message\":\"There was a server error.\"}";
	this.triggerResponse(500, sErrorResponseBody);
  	
  	assertEquals( "There was a server error.", this.errorResponseBody );
}

DashboardProviderTest.prototype.testCreateApp_OkResponse = function () {
	var oRequest = {
		command:"create-app",
		namespace:"appx"
	}
	this.expectRequest("POST", "servlet/app-manager/apps/app1", oRequest);
	
	this.provider.createApp("app1", "appx", this.successCallback.bind(this), this.errorCallback.bind(this))
	
	this.triggerResponse(200, "");
	
  	assertEquals( "", this.responseBody );
}
DashboardProviderTest.prototype.testCreateApp_ErrorResponse = function () {
	var oRequest = {
		command:"create-app",
		namespace:"appx"
	}
	this.expectRequest("POST", "servlet/app-manager/apps/app1", oRequest);
	
	this.provider.createApp("app1", "appx", this.successCallback.bind(this), this.errorCallback.bind(this))
	
	var sErrorResponseBody = "{\"cause\":\"Server Error!\",\"message\":\"There was a server error.\"}";
	this.triggerResponse(500, sErrorResponseBody);
  	
  	assertEquals( "There was a server error.", this.errorResponseBody );
}

DashboardProviderTest.prototype.testCreateBladeset_OkResponse = function () {
	var oRequest = {
		command:"create-bladeset"
	}
	this.expectRequest("POST", "servlet/app-manager/apps/app1/newBladeset", oRequest);
	
	this.provider.createBladeset("app1", "newBladeset", this.successCallback.bind(this), this.errorCallback.bind(this))

	this.triggerResponse(200, "");
		
  	assertEquals( "", this.responseBody );
}
DashboardProviderTest.prototype.testCreateBladeset_ErrorResponse = function () {
	var oRequest = {
		command:"create-bladeset"
	}
	this.expectRequest("POST", "servlet/app-manager/apps/app1/newBladeset", oRequest);
	
	this.provider.createBladeset("app1", "newBladeset", this.successCallback.bind(this), this.errorCallback.bind(this))

	var sErrorResponseBody = "{\"cause\":\"Server Error!\",\"message\":\"There was a server error.\"}";
	this.triggerResponse(500, sErrorResponseBody);
  	
  	assertEquals( "There was a server error.", this.errorResponseBody );
}

DashboardProviderTest.prototype.testCreateBlade_OkResponse = function () {
	var oRequest = {
		command:"create-blade"
	}
	this.expectRequest("POST", "servlet/app-manager/apps/app1/a-bladeset/newBlade", oRequest);
	
	this.provider.createBlade("app1", "a-bladeset", "newBlade", this.successCallback.bind(this), this.errorCallback.bind(this))
	
	this.triggerResponse(200, "");
  	
  	assertEquals( "", this.responseBody );
}
DashboardProviderTest.prototype.testCreateBlade_ErrorResponse = function () {
	var oRequest = {
		command:"create-blade"
	}
	this.expectRequest("POST", "servlet/app-manager/apps/app1/a-bladeset/newBlade", oRequest);
	
	this.provider.createBlade("app1", "a-bladeset", "newBlade", this.successCallback.bind(this), this.errorCallback.bind(this))
	
	var sErrorResponseBody = "{\"cause\":\"Server Error!\",\"message\":\"There was a server error.\"}";
	this.triggerResponse(500, sErrorResponseBody);
  	
  	assertEquals( "There was a server error.", this.errorResponseBody );
}

DashboardProviderTest.prototype.testGetWarUrl = function () {
  	assertEquals( "servlet/app-manager/export/app1", this.provider.getWarUrl("app1") );
}

DashboardProviderTest.prototype.testRunBladesetTests_OkResponse = function () {
	var oRequest = {
		command:"test",
		type:"ALL",
		recurse:"false"
	}
	this.expectRequest("POST", "servlet/app-manager/test/app1/aBladeset", oRequest);
	
	this.provider.runBladesetTests("app1", "aBladeset", this.successCallback.bind(this), this.errorCallback.bind(this))

	this.triggerResponse(200,"< some test results.... >");
	
  	assertEquals( "< some test results.... >", this.responseBody );
}
DashboardProviderTest.prototype.testRunBladesetTests_ErrorResponse = function () {
	var oRequest = {
		command:"test",
		type:"ALL",
		recurse:"false"
	}
	this.expectRequest("POST", "servlet/app-manager/test/app1/aBladeset", oRequest);
	
	this.provider.runBladesetTests("app1", "aBladeset", this.successCallback.bind(this), this.errorCallback.bind(this))

	var sErrorResponseBody = "{\"cause\":\"Server Error!\",\"message\":\"There was a server error.\"}";
	this.triggerResponse(500, sErrorResponseBody);
  	
  	assertEquals( "There was a server error.", this.errorResponseBody );
}

DashboardProviderTest.prototype.testRunBladeTests_OkResponse = function () {
	var oRequest = {
		command:"test",
		type:"ALL"
	}
	this.expectRequest("POST", "servlet/app-manager/test/app1/aBladeset/blade1", oRequest);
	
	this.provider.runBladeTests("app1", "aBladeset", "blade1", this.successCallback.bind(this), this.errorCallback.bind(this)) 

	this.triggerResponse(200,"< some test results.... >");

  	assertEquals( "< some test results.... >", this.responseBody );
}
DashboardProviderTest.prototype.testRunBladeTests_ErrorResponse = function () {
	var oRequest = {
		command:"test",
		type:"ALL"
	}
	this.expectRequest("POST", "servlet/app-manager/test/app1/aBladeset/blade1", oRequest);
	
	this.provider.runBladeTests("app1", "aBladeset", "blade1", this.successCallback.bind(this), this.errorCallback.bind(this)) 

	var sErrorResponseBody = "{\"cause\":\"Server Error!\",\"message\":\"There was a server error.\"}";
	this.triggerResponse(500, sErrorResponseBody);
  	
  	assertEquals( "There was a server error.", this.errorResponseBody );
}

DashboardProviderTest.prototype.testGetCurrentReleaseNotes_OkResponse = function () {
	this.expectRequest("GET", "servlet/app-manager/note/latest", null);
	
	this.provider.getCurrentReleaseNote(this.successCallback.bind(this), this.errorCallback.bind(this));

	this.triggerResponse(200, "<h1>Some Release Notes</h1>");
	
  	assertEquals( "<h1>Some Release Notes</h1>", this.responseBody);
}
DashboardProviderTest.prototype.testGetCurrentReleaseNotes_ErrorResponse = function () {
	this.expectRequest("GET", "servlet/app-manager/note/latest", null);
	
	this.provider.getCurrentReleaseNote(this.successCallback.bind(this), this.errorCallback.bind(this));

	var sErrorResponseBody = "{\"cause\":\"Server Error!\",\"message\":\"There was a server error.\"}";
	this.triggerResponse(500, sErrorResponseBody);
  	
  	assertEquals( "There was a server error.", this.errorResponseBody );
}

DashboardProviderTest.prototype.testGetSdkVersion = function () {
	this.expectRequest("GET", "servlet/app-manager/sdk/version", null);
	
	this.provider.getSdkVersion(this.successCallback.bind(this), this.errorCallback.bind(this));

	this.triggerResponse(200, "{ \"Version\":\"1.2.3\",\"BuildDate\":\"01/02/03\"}");
	
  	assertEquals( "1.2.3", this.responseBody);
}

DashboardProviderTest.prototype.testImpotMotif_OkResponse = function () {
	if (window.FormData)
	{
		this.mockRequest.expects(once()).open("POST", "servlet/app-manager/apps/newApp", true);
		this.mockRequest.expects(once()).send(ANYTHING);
		
		this.provider.importMotif("newApp", "appx", null, this.successCallback.bind(this), this.errorCallback.bind(this))
		
		this.triggerResponse(200, "");
		
	  	assertEquals( "", this.responseBody );
	}
}
DashboardProviderTest.prototype.testImpotMotif_ErrorResponse = function () {
	if (window.FormData)
	{
		this.mockRequest.expects(once()).open("POST", "servlet/app-manager/apps/newApp", true);
		this.mockRequest.expects(once()).send(ANYTHING);
		
		this.provider.importMotif("newApp", "appx", null, this.successCallback.bind(this), this.errorCallback.bind(this))
		
		var sErrorResponseBody = "{\"cause\":\"Server Error!\",\"message\":\"There was a server error.\"}";
		this.triggerResponse(500, sErrorResponseBody);
		
	  	assertEquals( "There was a server error.", this.errorResponseBody );
	}
}
DashboardProviderTest.prototype.testImportMotifCallsFailureCallbackIfXhr2NotSupported = function () {	
	
	var oOriginalFormData = window.FormData
	window.FormData = null;

	this.provider.importMotif("newApp", "appx", null, this.successCallback.bind(this), this.errorCallback.bind(this))
  	assertEquals( "Browser does not support XHR2.", this.errorResponseBody );
  	
  	window.FormatData = oOriginalFormData;
  	oOriginalFormData = null;
}


/** HELPER METHODS **/

DashboardProviderTest.prototype.successCallback = function(response) 
{
	this.responseStatusCode = 200;
	this.responseBody = response;
	this.errorResponseBody = "";
}
DashboardProviderTest.prototype.errorCallback = function(response) 
{
	this.responseStatusCode = -1;
	this.responseBody = "";
	this.errorResponseBody = response;
}

DashboardProviderTest.prototype.expectRequest = function(sMethodType, sUrl, oData)
{
	this.mockRequest.expects(once()).open(sMethodType, sUrl, true);
	if (oData == null)
	{
		this.mockRequest.expects(once()).send("");
	}
	else
	{
		this.mockRequest.expects(once()).send(JSON.stringify(oData));
	}
}

DashboardProviderTest.prototype.triggerResponse = function(vStatus, sResponseText) 
{
	this.mockRequest.proxy().readyState = 4;
	this.mockRequest.proxy().status = vStatus;
	this.mockRequest.proxy().responseText = sResponseText;
	this.mockRequest.proxy().onreadystatechange();
}


/** HELPER CLASSES **/

DummyXmlHttpRequest = function() {};
DummyXmlHttpRequest.prototype.open = function() {};
DummyXmlHttpRequest.prototype.send = function() {};

MockXHRFactory = function(oMockXhr)
{
	this.m_oMockXhr = oMockXhr;
}
MockXHRFactory.prototype.getRequestObject = function()
{
	return this.m_oMockXhr;
}

