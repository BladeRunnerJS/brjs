"use strict";

/**
* @module br/services/AppMetaService
*/

var Errors = require('br/Errors');

/**
 * @class
 * @interface
 * @alias module:br/services/AppMetaService
 *
 * @classdesc
 * A service which provides access to meta data for a BRJS app.
 */
function AppMetaService() {
}

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
* Returns the name of the locale cookie set via app.conf
* @returns The locales cookie
*/
AppMetaService.prototype.getLocaleCookieName = function() {
	throw new Errors.UnimplementedInterfaceError("AppMetaService.getLocaleCookieName() has not been implemented.");
};

module.exports = AppMetaService;
