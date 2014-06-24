"use strict";

var br = require('br/Core');
var BundlePathService = require('br/services/BundlePathService');

function JSTDBundlePathService() {
};

JSTDBundlePathService.prototype.getBundlePath = function(bundlePath)	{
	return getBundlePath("../v/dev/", bundlePath);
}

JSTDBundlePathService.prototype.getUnversionedBundlePath = function(bundlePath) {
	return getBundlePath("../", bundlePath);
};

function getBundlePath(prefix, bundlePath) {
	if (bundlePath != undefined) {
		/* make sure there are no leading /s that might mess up the generated path */
		while(bundlePath.charAt(0) === '/') {
			bundlePath = bundlePath.substr(1);
		}
		while(prefix.charAt(0) === '/') {
			prefix = prefix.substr(1);
		}
		return prefix + "/" + bundlePath
	}
	return prefix;
}

br.implement(JSTDBundlePathService, BundlePathService);

module.exports = JSTDBundlePathService;
