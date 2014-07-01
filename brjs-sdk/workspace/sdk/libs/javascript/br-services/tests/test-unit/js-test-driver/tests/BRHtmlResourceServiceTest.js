
var BRHtmlResourceService = require('br/services/html/BRHtmlResourceService');
var ServiceRegistry = require('br/ServiceRegistry');

BRHtmlResourceServiceTest = TestCase("BRHtmlResourceServiceTest");

BRHtmlResourceServiceTest.prototype.test_scriptTagsAreParsedByTheBrowser = function()
{
	var oService = ServiceRegistry.getService("br.html-service");
	var eTemplate = oService.getHTMLTemplate('br.services.testing-template');
	assertEquals(eTemplate.innerHTML.toLowerCase(), "<div>script</div>");
};

BRHtmlResourceServiceTest.prototype.test_templatesInBundle = function()
{
	var oService = ServiceRegistry.getService("br.html-service");
	assertEquals(oService.getHTMLTemplate("br.services.template1").innerHTML.toLowerCase(), "some html1");
	assertEquals(oService.getHTMLTemplate("br.services.template2").innerHTML.toLowerCase(), "some html2");
};

/* Failing in IE8 resulting in red build. See #678
BRHtmlResourceServiceTest.prototype.test_templatesInTemplateTagBundle = function()
{
	assertTemplateContentsMatch("br.services.template3", "some html3");
	assertTemplateContentsMatch("br.services.template4", "<div>some html4</div>");
	assertTemplateContentsMatch("br.services.template5", "<div>some html5</div><div>some html51</div>");
	assertTemplateContentsMatch("br.services.template6", "<div>some html6<div>some html61</div></div>");
};
*/

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

/**
 * 
 */
var assertTemplateContentsMatch = (function(){
	var oService = ServiceRegistry.getService("br.html-service");
	var tempDiv = document.createElement("div"); // Needed as you cannot call innerHTML on a document fragment.
	return function assertTemplateContentsMatch(templateId, expected) {
		var templateDocFrag = oService.getHTMLTemplate(templateId);
		
		tempDiv.innerHTML = "";
		tempDiv.appendChild(templateDocFrag);
		assertEquals(expected, tempDiv.innerHTML.toLowerCase());
	}
})();

BRHtmlResourceServiceTest.getService = function(sUrl)
{
	return new BRHtmlResourceService(sUrl);
};
