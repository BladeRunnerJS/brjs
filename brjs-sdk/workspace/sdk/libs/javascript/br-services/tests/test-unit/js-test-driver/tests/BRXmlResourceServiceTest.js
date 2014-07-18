BRXmlResourceServiceTest = TestCase("BRXmlResourceServiceTest");

BRXmlResourceServiceTest.prototype.test_getXmlResource = function()
{
	var oXmlResourceService = new br.services.xml.BRXmlResourceService("/test/unbundled-xml/testAppConfigValid.xml");
	assertEquals(1, oXmlResourceService.getXmlDocument("gridDefinitions").length);
	assertEquals(1, oXmlResourceService.getXmlDocument("rendererDefinitions").length);
};

BRXmlResourceServiceTest.prototype.test_getMalformedXml = function()
{
	if (window.navigator.userAgent == "PhantomJS") {
		return; // don't run this test on PhantomJS as it doesn't support ActiveX objects or selectSingleNode
		//TODO: find a way to run this test on PhantomJS 
	}
	
	var error = null;
	try
	{
		new br.services.xml.BRXmlResourceService("/test/unbundled-xml/testAppConfigMalformed.xml");
	}
	catch(e)
	{
		error = e;
	}

	assertNotNull("Expected an exception to be thrown.", error);
	assertEquals("InvalidDataError", error.name);
	assertEquals("XML is badly formed: [/test/unbundled-xml/testAppConfigMalformed.xml]", error.message);
};

BRXmlResourceServiceTest.prototype.test_getMissingXml = function()
{
	var error = null;
	try
	{
		new br.services.xml.BRXmlResourceService("/test/unbundled-xml/missing.xml");
	}
	catch(e)
	{
		error = e;
	}

	assertNotNull("Expected an exception to be thrown.", error);
	assertEquals("RequestFailedError", error.name);
	assertEquals("Unable to load file /test/unbundled-xml/missing.xml (status 404).", error.message);
};
