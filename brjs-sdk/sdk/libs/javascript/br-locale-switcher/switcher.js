var localeSwitcher = {
	getLocalizedPageUrl: function(pageUrl, locale) {
		var urlParser = document.createElement('a');
		urlParser.href = pageUrl;

		var protocol = urlParser.protocol;
		var host = urlParser.host;
		var url = urlParser.pathname;
		url = (url.charAt(0) != "/") ? "/"+url : url; /* some IE versions don't prefix pathname with / */
		url = ( !(/\/$/.test(url)) ) ? url+"/" : url; /* make sure the URL has a trailing / */
		var anchor = urlParser.hash;
		var queryString = urlParser.search;

		return protocol+"//"+host+url+locale+"/"+queryString+anchor;
	},

	switch: function() {
		var activeLocale = LocaleUtility.getActiveLocale(window.$BRJS_APP_LOCALES, window.$BRJS_LOCALE_COOKIE_NAME);
		window.location = this.getLocalizedPageUrl(window.location.href, activeLocale);
	}
};
