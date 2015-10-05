'use strict';

var jQuery = require('jquery');
var Core = require('br/Core');
Core.thirdparty('jquery-browser');

function BrowserDetector() {
}

BrowserDetector.prototype.getBrowserName = function() {
	if (jQuery.browser.name) {
		var sBrowser = jQuery.browser.name;
		if (sBrowser == 'msie') {
			return 'ie';
		}
		return sBrowser;
	}
	return '';
};

BrowserDetector.prototype.getBrowserVersion = function() {
	if (jQuery.browser.versionNumber) {
		return jQuery.browser.versionNumber;
	}
	return '';
};

module.exports = BrowserDetector;

