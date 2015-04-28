
var BRHtmlResourceService = require('br/services/html/BRHtmlResourceService');
var ServiceRegistry = require('br/ServiceRegistry');

BRHtmlResourceServiceTest = TestCase("BRHtmlResourceServiceTest");

// Check if the browser is IE8 (or less)
if (window.attachEvent && !window.addEventListener) {
	document.createElement('template');
}

BRHtmlResourceServiceTest.prototype.setup = function()
{
	var brjsTemplates = document.getElementById("brjs-html-templates");
	if (brjsTemplates) {
		brjsTemplates.parentNode.removeChild(brjsTemplates);
	}
};

BRHtmlResourceServiceTest.prototype.test_scriptTagsAreParsedByTheBrowser = function()
{
	var oService = getService();
	var eTemplate = oService.getTemplateElement('br.services.testing-template');
	assertEquals(eTemplate.innerHTML.toLowerCase(), "<div>script</div>");
};

BRHtmlResourceServiceTest.prototype.test_templatesInBundle = function()
{
	var oService = getService();
	assertEquals(oService.getTemplateElement("br.services.template1").innerHTML.toLowerCase(), "some html1");
	assertEquals(oService.getTemplateElement("br.services.template2").innerHTML.toLowerCase(), "some html2");
};

// Failing in IE8 resulting in red build. See #678
BRHtmlResourceServiceTest.prototype.test_templatesInTemplateTagBundle = function()
{
	assertTemplateContentsMatch("br.services.template3", "some html3");
	assertTemplateContentsMatch("br.services.template4", "<div>some html4</div>");
	assertTemplateContentsMatch("br.services.template5", "<div>some html5</div><div>some html51</div>");
	assertTemplateContentsMatch("br.services.template6", "<div>some html6<div>some html61</div></div>");
};


BRHtmlResourceServiceTest.prototype.test_loadHTMLFilesDoesNotExist = function()
{
	// remove templates first so that the HTMLResourceService is forced to reload them
	var templates = document.querySelector('div#brjs-html-templates');
	if(templates) {
		templates.parentNode.removeChild(templates);
	}

	var bErrorThrown = false;
	var error = null;

	var sFileUrl = "/test/resources/unbundled-html/doesnotexist.html";
	try
	{
		getService(sFileUrl);
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

var getService = function(sUrl)
{
	if (!sUrl) { sUrl = "/test/bundles/html.bundle"; }
	return new BRHtmlResourceService(sUrl);
};

var assertTemplateContentsMatch = (function(){
	var oService = getService();
	var tempDiv = document.createElement("div"); // Needed as you cannot call innerHTML on a document fragment.
	return function assertTemplateContentsMatch(templateId, expected) {
		var templateDocFrag = oService.getTemplateFragment(templateId);

		tempDiv.innerHTML = "";
		tempDiv.appendChild(templateDocFrag);
		assertEquals(expected, tempDiv.innerHTML.toLowerCase().replace(/[\n\r]/g, ''));
	};
})();
