'use strict';

/**
 * @module br-locale/switcher
 */

module.exports = {
	switchLocale: function(locale) {
		var localePageUrl = this.getLocalizedPageUrl(window.location.href.replace(/[^/]+$/, ''), locale);

		require('service!br.locale-provider').setActiveLocale(locale);
		require('service!br.locale-switcher').switchLocale(localePageUrl);
	},

	switchToActiveLocale: function() {
		var activeLocale = require('service!br.locale-provider').getActiveLocale();
		var localePageUrl = this.getLocalizedPageUrl(window.location.href, activeLocale);

		require('service!br.locale-switcher').switchLocale(localePageUrl);
	},

	getLocalizedPageUrl: function(pageUrl, locale) {
		var urlParser = document.createElement('a');
		urlParser.href = pageUrl;

		var protocol = urlParser.protocol;
		var host = urlParser.host;
		var path = urlParser.pathname;
		var query = urlParser.search;
		var hash = urlParser.hash;
		var fullyQualifiedPath = path.match(/\.html$/);
		var normalizedPath = path === "/" ? "" : path.replace(/^\/?(.*?)(\/|\/index\.html)?$/, '/$1');
		var localizedPath = normalizedPath + '/' + locale + ((fullyQualifiedPath) ? '.html' : '');

		var port80isExplicitlyRequested = pageUrl.match(/:80$/) || pageUrl.match(/:80\//);
		var hostContainsPort80 = host.match(/:80$/);

		if (port80isExplicitlyRequested) {
			if (!hostContainsPort80) {
				// Append port 80 to the host, since it was explicitly requested but it is not there
				host += ":80/";
			}
		} else {
			if (hostContainsPort80) {
				// Remove port 80 from the host, since it was not explicitly requested in the URL
				host = host.replace(":80", "");
			}
		}

		// Remove possible double slashes from IE
		localizedPath = localizedPath.replace("//", "/");
		return protocol + '//' + host + localizedPath + query + hash;
	}
};
