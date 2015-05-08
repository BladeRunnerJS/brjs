"use strict";

// use a variable to store this so the modle doesn't try to find the source module that represents it during the dependency analysis
var metaDataRequirePath = "app-meta!$data";
var metaData;

/**
* @module br/services/bundlepath/BRAppMetaService
*/

var br = require('br/Core');
var AppMetaService = require('br/services/AppMetaService');

/**
 * @class
 * @alias module:br/services/bundlepath/BRAppMetaService
 * @implements module:br/services/AppMetaService
 */
function BRAppMetaService() {
};

br.implement(BRAppMetaService, AppMetaService);

BRAppMetaService.prototype.getVersion = function() {
	return metaData.APP_VERSION;
};

BRAppMetaService.prototype.getVersionedBundlePath = function(bundlePath) {
	return getBundlePath(metaData.VERSIONED_BUNDLE_PATH, bundlePath);
};

BRAppMetaService.prototype.getLocales = function() {
	return metaData.APP_LOCALES;
};

BRAppMetaService.prototype.getLocaleCookieName = function() {
	return metaData.LOCALE_COOKIE_NAME;
};

function getBundlePath(prefix, bundlePath) {
	if (bundlePath != undefined) {
		/* make sure there are no leading or trailing /s that might mess up the generated path */
		prefix = prefix.replace(/^\/|\/$/g, '');
		if (bundlePath.substring(0, 1) == '/') {
			bundlePath = bundlePath.substring(1);
		}
		return prefix + "/" + bundlePath
	}
	return prefix;
}

module.exports = BRAppMetaService;

metaData = require(metaDataRequirePath);
