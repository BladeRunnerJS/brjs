"use strict";

var Errors = require('br/Errors');
var br = require('br/Core');
var LocaleService = require('br/services/LocaleService');
var LocaleUtility = require("br-locale-utility");

/**
* @name br.services.locale.BRLocaleService
* @constructor
* @class
* @interface
*/
function BRLocaleService() {
};

function BRLocaleService(getCookieFn, getBrowserLocalesFn, appLocales, urlAccessorFn) {
	this._getCookieFn = (getCookieFn != undefined) ? getCookieFn : LocaleUtility.getCookie;
	this._getBrowserLocalesFn = (getBrowserLocalesFn != undefined) ? getBrowserLocalesFn : LocaleUtility.getBrowserAcceptedLocales;
	this._appLocales = (appLocales != undefined) ? appLocales : window.$BRJS_APP_LOCALES;
	this._urlAccessorFn = (urlAccessorFn != undefined) ? urlAccessorFn : function() { return window.location.href };
	this.LOCALE_COOKIE_NAME = window.$BRJS_LOCALE_COOKIE_NAME;
};


/**
* Sets the locale cookie
*/
BRLocaleService.prototype.setLocaleCookie = function(locale, days) {
	var localePath = "";
	var pageUrl = this._urlAccessorFn().replace(/^\/|\/$/g, '');
	var pageUrlSplit = pageUrl.split("/");
	for (var i = 0; i < pageUrlSplit.length - 1; i++) {
		localePath += pageUrlSplit[i];
	}
	if (pageUrlSplit.length > 1) {
		localePath += "/";
	} else {
		localePath = "/";
	}
	LocaleUtility.setCookie( this.LOCALE_COOKIE_NAME, locale, days, localePath );
};

/**
* Gets the current locale preference
*/
BRLocaleService.prototype.getLocale = function() {
	var localeCookieValue = this._getCookieFn( this.LOCALE_COOKIE_NAME );
	var browserLocales = this._getBrowserLocalesFn();
	return LocaleUtility.getActiveLocale( localeCookieValue, browserLocales, this._appLocales);
};

/**
* Gets the locale for the current page
*/
BRLocaleService.prototype.getPageLocale = function() {
	var pageUrl = this._urlAccessorFn().replace(/^\/|\/$/g, '');
	var pageUrlSplit = pageUrl.split("/");
	return pageUrlSplit[pageUrlSplit.length - 1];
};

br.implement(BRLocaleService, LocaleService);

module.exports = BRLocaleService;
