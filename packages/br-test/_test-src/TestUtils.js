require('br-presenter/_resources-test-at/html/test-form.html');
require('../_resources-test-at/html/view-fixture-elements.html');
var jQuery = require("jquery");
var EventLogging = require('./EventLogging');
var Events = require('./Events');

function TestUtils() {
}

var testHtml = null;

TestUtils.setupPage = function() {
	if (testHtml == null) {
		testHtml = require("service!br.html-service").getTemplateElement("testWrapper");

		jQuery('body').empty();
	}
	jQuery("body").unbind(Events.ALL_EVENTS.join(" "), EventLogging.logEvent);
	jQuery('body').empty();
	EventLogging.clearEvents();
	jQuery('body').append( testHtml );
	jQuery("body").bind(Events.ALL_EVENTS.join(" "), EventLogging.logEvent);
	jQuery('a#defaultFocus').click().focus();
};

TestUtils.tearDownPage = function() {
	bodyElement = jQuery('body').empty();
};

module.exports = TestUtils;
