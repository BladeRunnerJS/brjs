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

function getActiveLocale() {
	var userPreferredLocale = getCookie("BRJS.LOCALE");
	var activeLocale;
	
	if(appSupportedLocales[userPreferredLocale]) {
		activeLocale = userPreferredLocale;
	}
	else {
		var userAcceptedLocales = getUserAcceptedLocales();
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

window.location = window.location + getActiveLocale() + "/";