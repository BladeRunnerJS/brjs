"use strict";

var Errors = require('br/Errors');

/**
* @name br.services.LocaleService
* @constructor
* @class
* @interface
* This class allows getting and setting the current app locale cookie
*/
function LocaleService() {};

/**
* Sets the locale cookie
*/
LocaleService.prototype.setLocaleCookie = function(locale, days) {
	throw new Errors.UnimplementedInterfaceError("LocaleService.setLocaleCookie(locale, days) has not been implemented.");
};

/**
* Gets the current locale preference
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
