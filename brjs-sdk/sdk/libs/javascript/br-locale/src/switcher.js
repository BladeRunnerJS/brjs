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
		var normalizedPath = path.replace(/^\/?(.*?)(\/|\/index\.html)?$/, '/$1');
		var localizedPath = normalizedPath + '/' + locale + ((fullyQualifiedPath) ? '.html' : '');

		return protocol + '//' + host + localizedPath + query + hash;
	}
};
