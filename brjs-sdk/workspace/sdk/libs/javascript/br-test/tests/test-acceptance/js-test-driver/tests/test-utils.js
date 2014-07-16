br.Core.thirdparty("jquery");
br.Core.thirdparty('sl4bdummy');

var testHtml = null;
setupPage = function() {
	if (testHtml == null) {
		//TODO: go back to using the service registry when we can correctly bundle new style javascript classes.
		//testHtml = require("br/ServiceRegistry").getService("br.html-service").getHTMLTemplate("testWrapper");
		var BRHtmlResourceService = require("br/services/html/BRHtmlResourceService");
		testHtml = new BRHtmlResourceService("/test/bundles/html.bundle").getHTMLTemplate("testWrapper");

		jQuery('body').empty();
	}
	jQuery("body").unbind(ALL_EVENTS.join(" "), logEvent);
	jQuery('body').empty();
	clearEvents();
	jQuery('body').append( testHtml );
	jQuery("body").bind(ALL_EVENTS.join(" "), logEvent);
	jQuery('a#defaultFocus').click().focus();
};

tearDownPage = function() {
	bodyElement = jQuery('body').empty();
};
