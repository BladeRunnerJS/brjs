// TODO: delete this service

"use strict";

/**
 * @module br/services/locale/BRLocaleService
 */

var Errors = require('br/Errors');
var br = require('br/Core');
var LocaleService = require('br/services/LocaleService');
var ServiceRegistry;

/**
 * @class
 * @alias module:br/services/locale/BRLocaleService
 * @implements module:br/services/locale/BRLocaleServices
 *
 * @classdesc
 * The default locale service for BRJS apps. This class should not be constructed
 * directly, but instead used via the service registry.
 *
 * @param {LocaleUtility} localeUtility The locale utility to use
 */
function BRLocaleService( localeUtility ) {
	if (localeUtility) {
		this.localeUtility = localeUtility;
	} else {
		this.localeUtility = require("br-locale-utility");
	}
	this.appMetaService = require('service!br.app-meta-service');
};

br.implement(BRLocaleService, LocaleService);

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
	var localeCookieName = this.appMetaService.getLocaleCookieName();
	this.localeUtility.setCookie( localeCookieName, locale, days, localePath );
};

/**
* Gets the current locale preference
*/
BRLocaleService.prototype.getLocale = function() {
	var localeCookieName = this.appMetaService.getLocaleCookieName();
	var localeCookieValue = this.localeUtility.getCookie( localeCookieName );
	var browserLocales = this.localeUtility.getBrowserAcceptedLocales();
	var appLocales = this.appMetaService.getLocales();
	return LocaleUtility.getActiveLocaleGiven( localeCookieValue, browserLocales, appLocales );
};

/**
* Gets the locale for the current page
*/
BRLocaleService.prototype.getPageLocale = function() {
	var pageUrl = this.localeUtility.getWindowUrl().replace(/^\/|\/$/g, '');
	var pageUrlSplit = pageUrl.split("/");
	return pageUrlSplit[pageUrlSplit.length - 1];
};

module.exports = BRLocaleService;

ServiceRegistry = require('br/ServiceRegistry');
