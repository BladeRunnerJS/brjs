BRHtmlResourceServiceTest = TestCase("BRHtmlResourceServiceTest");

BRHtmlResourceServiceTest.prototype.test_scriptTagsAreParsedByTheBrowser = function()
{
	var oService = br.ServiceRegistry.getService("br.html-service");
	var eTemplate = oService.getHTMLTemplate('br.services.testing-template');
	assertEquals(eTemplate.innerHTML.toLowerCase(), "<div>script</div>");
};

BRHtmlResourceServiceTest.prototype.test_templatesInBundle = function()
{
	var oService = br.ServiceRegistry.getService("br.html-service");
	assertEquals(oService.getHTMLTemplate("br.services.template1").innerHTML.toLowerCase(), "some html1");
	assertEquals(oService.getHTMLTemplate("br.services.template2").innerHTML.toLowerCase(), "some html2");
};

BRHtmlResourceServiceTest.prototype.test_loadHTMLFilesDoesNotExist = function()
{
	var bErrorThrown = false;
	var error = null;

	var sFileUrl = "/test/resources/unbundled-html/doesnotexist.html";
	try
	{
		BRHtmlResourceServiceTest.getService(sFileUrl);
	}
	catch(e)
	{
		error = e;
		if (e.message.match(sFileUrl))
		{
			bErrorThrown = true;
		}
	}
	assertTrue(bErrorThrown);
	assertEquals("RequestFailedError", error.name);
	assertEquals("Unable to load file /test/resources/unbundled-html/doesnotexist.html (status 404).", error.message);
};

BRHtmlResourceServiceTest.getService = function(sUrl)
{
	return new br.services.BRHtmlResourceService(sUrl);
};
