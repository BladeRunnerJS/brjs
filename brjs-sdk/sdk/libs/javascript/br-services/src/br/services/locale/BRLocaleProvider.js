'use strict';

var topiarist = require('topiarist');
var LocaleProvider = require('br/services/LocaleProvider');

/**
 * @module br/services/locale/BRLocaleProvider
 */

/**
 * <code>BRLocaleProvider</code> is an implementation of {br/services/BRLocaleProvider} that works in the following way:
 *
 * <ol>
 *   <li>The locale cookie is used if one is present.</li>
 *   <li>If no locale cookie is present, the browser's <code>Accept-Language</code> headers are used to set the locale
 *    cookie.</li>
 *   <li>If {#setActiveLocale} is invoked the locale cookie is updated.</li>
 * </ol>
 */
function BRLocaleProvider() {
}
topiarist.implement(BRLocaleProvider, LocaleProvider);

BRLocaleProvider.prototype.getActiveLocale = function() {
	var localeCookie = getCookie(window.$BRJS_LOCALE_COOKIE_NAME);
	var browserAcceptedLocales = getBrowserAcceptedLocales();
	var appLocales = window.$BRJS_APP_LOCALES;
	
	return this.getActiveLocaleGiven(localeCookie, browserAcceptedLocales, appLocales);
};

BRLocaleProvider.prototype.getActiveLocaleGiven = function(userPreferredLocale, userAcceptedLocales, appSupportedLocales) {
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
};

BRLocaleProvider.prototype.setActiveLocale = function(locale) {
	// TODO: why does the cookie involve a page location?
	var pageUrl = location.href.replace(/^\/|\/$/g, '');
	var lastSlashIndex = pageUrl.lastIndexOf("/");
	var localePath = pageUrl.substring(0, lastSlashIndex);
	if (localePath.charAt(0) != '/') {
		localePath = "/"+localePath;
	}
	setCookie( window.$BRJS_LOCALE_COOKIE_NAME, locale, 365, localePath );
};

function getBrowserAcceptedLocales() {
	var userAcceptedLocales;

	if (navigator.languages) {
		userAcceptedLocales = navigator.languages;
	}
	else if (navigator.language) {
		userAcceptedLocales = [navigator.language];
	}
	else {
		var parts = navigator.userLanguage.split('-');
		var locale = (parts.length == 1) ? parts[0] : parts[0] + '-' + parts[1].toUpperCase()
		
		userAcceptedLocales = [locale];
	}

	// convert locale codes to use underscores like we do on the server
	for(var i = 0, l = userAcceptedLocales.length; i < l; ++i) {
		var userAcceptedLocale = userAcceptedLocales[i];
		userAcceptedLocales[i] = userAcceptedLocale.replace('-', '_');
	}

	return userAcceptedLocales;
}

function getCookie(name) {
	var nameEQ = name + "=";
	var ca = document.cookie.split(';');
	for(var i=0;i < ca.length;i++) {
		var c = ca[i];
		while (c.charAt(0)===' ') {
			c = c.substring(1,c.length);
		}

		if (c.indexOf(nameEQ) === 0) {
			return c.substring(nameEQ.length,c.length);
		}
	}
	return null;
}

function setCookie(name, value, days, path) {
	var expires = "";
	if (days) {
		var date = new Date();
		var expiresDate = new Date( date.getTime()+(days*24*60*60*1000) );
		expires = "; expires="+expiresDate.toGMTString();
	}
	path = (path) ? path : "/";
	document.cookie = name+"="+value+expires+"; path="+path;
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

module.exports = BRLocaleProvider;
