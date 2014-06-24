function getUserAcceptedLocales() {
	var userAcceptedLocales;

	if(navigator.languages) {
		userAcceptedLocales = navigator.languages;
	}
	else if(navigator.language) {
		userAcceptedLocales = [navigator.language];
	}
	else {
		userAcceptedLocales = [navigator.userLanguage];
	}

	return userAcceptedLocales;
}

function getCookie(name) {
	var nameEQ = name + "=";
	var ca = document.cookie.split(';');
	for(var i=0;i < ca.length;i++) {
		var c = ca[i];
		while (c.charAt(0)==' ') c = c.substring(1,c.length);
		if (c.indexOf(nameEQ) == 0) return c.substring(nameEQ.length,c.length);
	}
	return null;
}

function getFirstMatchingLocale(appSupportedLocales, userAcceptedLocales) {
	var firstMatchingLocale;

	for(var i = 0, l = userAcceptedLocales.length; i < l; ++i) {
		var userAcceptedLocale = userAcceptedLocales[i];

		if(appSupportedLocales[userAcceptedLocale]) {
			firstMatchingLocale = userAcceptedLocale;
			break;
		}
	}

	return firstMatchingLocale;
}

function getActiveLocale(userPreferredLocale, userAcceptedLocales, appSupportedLocales) {
	var activeLocale;

	if(appSupportedLocales[userPreferredLocale]) {
		activeLocale = userPreferredLocale;
	}
	else {
		var firstMatchingLocale = getFirstMatchingLocale(appSupportedLocales, userAcceptedLocales);

		if(firstMatchingLocale) {
			activeLocale = firstMatchingLocale;
		}
		else {
			for(var appSupportedLocale in appSupportedLocales) {
				activeLocale = appSupportedLocale;
				break;
			}
		}
	}

	return activeLocale;
}

function getLocalizedPageUrl(pageUrl, locale) {
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
}

function forwardToLocalePage() {
	// The localeCookieName token is replaced before serving the content to the browser. The value of localeCookieName is set in app.conf.
	window.location = getLocalizedPageUrl(window.location.href, getActiveLocale(getCookie("@localeCookieName@"), getUserAcceptedLocales(), $appSupportedLocales));
}
