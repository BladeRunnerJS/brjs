"use strict";

var Errors = require('br/Errors');
var br = require('br/Core');
var LocaleService = require('br/services/LocaleService');

/**
* @name br.services.locale.BRLocaleService
* @constructor
* @class
* @interface
*/
function BRLocaleService() {
	this.localeUtility = require("br-locale-utility");
};

function BRLocaleService( localeUtility ) {
	this.localeUtility = localeUtility;
};


/**
* Sets the locale cookie
*/
BRLocaleService.prototype.setLocaleCookie = function(locale, days) {
	var localePath = "";
	var pageUrl = this.localeUtility.getWindowUrl().replace(/^\/|\/$/g, '');
	var pageUrlSplit = pageUrl.split("/");
	for (var i = 0; i < pageUrlSplit.length - 1; i++) {
		localePath += pageUrlSplit[i];
	}
	if (pageUrlSplit.length > 1) {
		localePath += "/";
	} else {
		localePath = "/";
	}
	this.localeUtility.setCookie( window.$BRJS_LOCALE_COOKIE_NAME, locale, days, localePath );
};

/**
* Gets the current locale preference
*/
BRLocaleService.prototype.getLocale = function() {
	var localeCookieValue = this.localeUtility.getCookieValue( window.$BRJS_LOCALE_COOKIE_NAME );
	var browserLocales = this.localeUtility.getBrowserAcceptedLocales();
	var appLocales = this.localeUtility.getAppLocales();
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
