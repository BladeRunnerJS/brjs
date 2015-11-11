"use strict";

/**
 * @module br/services/appmeta/JSTDAppMetaService
 */

var br = require('br/Core');
var BRAppMetaService = require('./BRAppMetaService');

/**
 * @class
 * @alias module:br/services/appmeta/JSTDAppMetaService
 * @extends module:br/services/appmeta/BRAppMetaService
 *
 * @classdesc
 * This class provides access to App meta data for testing purposes.
 */
function JSTDAppMetaService() {
	this._testMetaData = {};
	BRAppMetaService.call(this);
}

br.extend(JSTDAppMetaService, BRAppMetaService);

JSTDAppMetaService.prototype.getVersion = function() {
	return this._testMetaData.APP_VERSION || BRAppMetaService.prototype.getVersion.call(this);
};

JSTDAppMetaService.prototype.isDev = function() {
	return this.getVersion() === "dev";
};

/**
 * Sets the app version to be used in the test.
 * @param {String} version The app version.
 */
JSTDAppMetaService.prototype.setVersion = function(version) {
	this._testMetaData.APP_VERSION = version;
};

JSTDAppMetaService.prototype.getLocales = function() {
	return this._testMetaData.APP_LOCALES || BRAppMetaService.prototype.getLocales.call(this);
};

/**
 * Sets the app locales to be used in the test.
 * @param locales The app locales.
 */
JSTDAppMetaService.prototype.setLocales = function(locales) {
	this._testMetaData.APP_LOCALES = locales;
};

JSTDAppMetaService.prototype.getLocaleCookieName = function() {
	return this._testMetaData.LOCALE_COOKIE_NAME || BRAppMetaService.prototype.getLocaleCookieName.call(this);
};

/**
 * Sets the locale cookie name to be used in the test.
 * @param {String} version The app version.
 */
JSTDAppMetaService.prototype.setLocaleCookieName = function(localeCookieName) {
	this._testMetaData.LOCALE_COOKIE_NAME = localeCookieName;
};

/**
 * Resets all properties to their default values. This should be called after every test where one or more properties were set.
 */
JSTDAppMetaService.prototype.resetAllValues = function() {
	this._testMetaData = {};
};


module.exports = JSTDAppMetaService;
