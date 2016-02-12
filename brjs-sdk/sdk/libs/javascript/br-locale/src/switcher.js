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

		if (navigator.userAgent.indexOf("MSIE ") > -1 || navigator.userAgent.match(/Trident.*rv\:11\./)) {
			if (pageUrl.indexOf(":80") > -1) {
				localizedPath = localizedPath.substring(1, localizedPath.length);
			} else if (host.indexOf(":80") > -1){
				host = host.substring(0, host.length - 3);
				localizedPath = localizedPath.substring(1, localizedPath.length);
			}
		} else if (pageUrl.indexOf(":80") > -1) {
			host += ":80";
		}

		return protocol + '//' + host + localizedPath + query + hash;
	}
};
