require("jquery");

var testHtml = null;
setupPage = function() {
	if (testHtml == null) {
		testHtml = require("service!br.html-service").getHTMLTemplate("testWrapper");

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
