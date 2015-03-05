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
* @deprecated
*
* @classdesc
* The default locale service for BRJS apps. This class should not be constructed
* directly, but instead used via the service registry.
*/
function BRLocaleService() {
	this.localeProvider = require('service!br.locale-provider');
};

br.implement(BRLocaleService, LocaleService);

/**
* Sets the locale cookie
* @deprecated
*/
BRLocaleService.prototype.setLocaleCookie = function(locale, days) {
	this.localeProvider.setActiveLocale(locale);
};

/**
* Gets the current locale preference
* @deprecated
*/
BRLocaleService.prototype.getLocale = function() {
	return this.localeProvider.getActiveLocale();
};

/**
* Gets the locale for the current page
* @deprecated
*/
BRLocaleService.prototype.getPageLocale = function() {
	var pageUrl = location.pathname.replace(/^\/|\/$/g, '');
	var pageUrlSplit = pageUrl.split("/");
	return pageUrlSplit[pageUrlSplit.length - 1];
};

module.exports = BRLocaleService;

ServiceRegistry = require('br/ServiceRegistry');
