"use strict";

/**
* @module br/services/locale/BRLocaleService
*/

var Errors = require('br/Errors');
var br = require('br/Core');
var LocaleService = require('br/services/LocaleService');

/**
* The default locale service for BRJS apps. This class should not be constructed
* directly, but instead used via the service registry.
* @alias module:br/services/locale/BRLocaleService
* @param {LocaleUtility} localeUtility The locale utility to use
* @class
* @implements module:br/services/locale/BRLocaleServices
*/
function BRLocaleService( localeUtility ) {
	if (localeUtility) {
		this.localeUtility = localeUtility;
	} else {
		this.localeUtility = require("br-locale-utility");
	}
};


/**
* Sets the locale cookie
*/
BRLocaleService.prototype.setLocaleCookie = function(locale, days) {
	var pageUrl = this.localeUtility.getWindowUrl().replace(/^\/|\/$/g, '');
	var lastSlashIndex = pageUrl.lastIndexOf("/");
	var localePath = pageUrl.substring(0, lastSlashIndex);
	if (localePath.charAt(0) != '/') {
		localePath = "/"+localePath;
	}
	this.localeUtility.setCookie( window.$BRJS_LOCALE_COOKIE_NAME, locale, days, localePath );
};

/**
* Gets the current locale preference
*/
BRLocaleService.prototype.getLocale = function() {
	var localeCookieValue = this.localeUtility.getCookie( window.$BRJS_LOCALE_COOKIE_NAME );
	var browserLocales = this.localeUtility.getBrowserAcceptedLocales();
	var appLocales = window.$BRJS_APP_LOCALES;
	return LocaleUtility.getActiveLocale( localeCookieValue, browserLocales, appLocales );
};

/**
* Gets the locale for the current page
*/
BRLocaleService.prototype.getPageLocale = function() {
	var pageUrl = this.localeUtility.getWindowUrl().replace(/^\/|\/$/g, '');
	var pageUrlSplit = pageUrl.split("/");
	return pageUrlSplit[pageUrlSplit.length - 1];
};

br.implement(BRLocaleService, LocaleService);

module.exports = BRLocaleService;
