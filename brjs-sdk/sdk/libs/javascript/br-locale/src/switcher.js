'use strict';

/**
 * @module br-locale/switcher
 */

module.exports = {
	switchToActiveLocale: function() {
		var activeLocale = require('service!br.locale-provider').getActiveLocale();
		var localePageUrl = this.getLocalizedPageUrl(window.location.href, activeLocale);
		
		require('service!br.locale-switcher').switch(localePageUrl);
	},

	getLocalizedPageUrl: function(pageUrl, locale) {
		var urlParser = document.createElement('a');
		urlParser.href = pageUrl;

		var protocol = urlParser.protocol;
		var host = urlParser.host;
		var path = urlParser.pathname;
		var query = urlParser.search;
		var hash = urlParser.hash;
		var normalizedPath = path.replace(/\/$/, '').replace(/^\/?(.*)/, '/$1');

		if (normalizedPath.indexOf('.html') !== -1) {
			normalizedPath = normalizedPath.split('/');
			normalizedPath.splice(-1, 0, locale);
			normalizedPath = normalizedPath.join('/');
		}
		else {
			normalizedPath = normalizedPath + '/' + locale + '/';
		}

		return protocol + '//' + host + normalizedPath + query + hash;
	}
};
