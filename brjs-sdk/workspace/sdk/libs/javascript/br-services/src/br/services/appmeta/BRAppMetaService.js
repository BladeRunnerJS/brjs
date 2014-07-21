"use strict";

/**
* @module br/services/bundlepath/BRAppMetaService
*/

var br = require('br/Core');
var AppMetaService = require('br/services/AppMetaService');

/**
* @implements module:br/services/AppMetaService
*/
function BRAppMetaService() {
};

BRAppMetaService.prototype.getVersion = function() {
	return window.$BRJS_APP_VERSION;
};

BRAppMetaService.prototype.getVersionedBundlePath = function(bundlePath) {
	return getBundlePath(window.$BRJS_VERSIONED_BUNDLE_PATH, bundlePath);
};

BRAppMetaService.prototype.getLocales = function() {
	return window.$BRJS_APP_LOCALES;
};

BRAppMetaService.prototype.getLocaleCookieName = function() {
	return window.$BRJS_LOCALE_COOKIE_NAME
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


br.implement(BRAppMetaService, AppMetaService);

module.exports = BRAppMetaService;
