"use strict";

/**
* @module br/services/AppMetaService
*/

var Errors = require('br/Errors');

/**
* A service which provides access to meta data for a BRJS app
* @classdesc
* @interface
* @alias module:br/services/AppMetaService
*/
function AppMetaService() {};

/**
* Returns the app version
* @returns The app version
*/
AppMetaService.prototype.getVersion = function() {
	throw new Errors.UnimplementedInterfaceError("AppMetaService.getVersion() has not been implemented.");
};

/**
* Returns the path to content plugins/bundles.
* @param {String} [bundlePath] The path to a bundle to be appended to the returned path
* @returns The path to content plugins/bundles.
*/
AppMetaService.prototype.getVersionedBundlePath = function(bundlePath) {
	throw new Errors.UnimplementedInterfaceError("AppMetaService.getVersionedBundlePath() has not been implemented.");
};

/**
* Returns the locales for the app
* @returns The locales
*/
AppMetaService.prototype.getLocales = function() {
	throw new Errors.UnimplementedInterfaceError("AppMetaService.getLocales() has not been implemented.");
};

/**
* Returns the name of the cookie used to store the locale preference
* @returns The cookie name
*/
AppMetaService.prototype.getLocaleCookieName = function() {
	throw new Errors.UnimplementedInterfaceError("AppMetaService.getLocaleCookieName() has not been implemented.");
};

module.exports = AppMetaService;
