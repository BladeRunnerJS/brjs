"use strict";

/*
* @module br/services/LocaleService
*/

var Errors = require('br/Errors');

/**
* A service that allows getting and setting the current app locale cookie
* @class
* @interface
* @alias module:br/services/LocaleService
*/
function LocaleService() {};

/**
* Sets the locale cookie
* @param {String} locale The locale value for the cookie
* @param {Number} days The number of days to set for this cookie
*/
LocaleService.prototype.setLocaleCookie = function(locale, days) {
	throw new Errors.UnimplementedInterfaceError("LocaleService.setLocaleCookie(locale, days) has not been implemented.");
};

/**
* Gets the current locale preference.
*/
LocaleService.prototype.getLocale = function() {
	throw new Errors.UnimplementedInterfaceError("LocaleService.getAppLocale() has not been implemented.");
};

/**
* Gets the locale for the current page
*/
LocaleService.prototype.getPageLocale = function() {
	throw new Errors.UnimplementedInterfaceError("LocaleService.getCurrentLocalePage() has not been implemented.");
};


module.exports = LocaleService;
