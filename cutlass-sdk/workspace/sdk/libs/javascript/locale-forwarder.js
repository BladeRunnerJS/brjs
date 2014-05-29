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
	var parts = pageUrl.split("#");
	var url = parts[0];
	var anchor = parts[1]
	
	return url + locale + "/" + ((anchor) ? "#" + anchor : "");
}

function forwardToLocalePage() {
	window.location = getLocalizedPageUrl(window.location.href, getActiveLocale(getCookie("BRJS.LOCALE"), getUserAcceptedLocales(), $appSupportedLocales));
}
