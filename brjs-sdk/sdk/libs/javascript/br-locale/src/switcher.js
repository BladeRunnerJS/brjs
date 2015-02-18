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
		var fullyQualifiedPath = path.match(/\.html$/);
		var normalizedPath = path.replace(/^\/?(.*?)(\/|\/index\.html)?$/, '/$1');
		var localizedPath = normalizedPath + '/' + locale + ((fullyQualifiedPath) ? '.html' : '');

		return protocol + '//' + host + localizedPath + query + hash;
	}
};
