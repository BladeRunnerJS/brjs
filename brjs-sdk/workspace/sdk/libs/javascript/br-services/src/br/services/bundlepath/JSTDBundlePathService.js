"use strict";

/**
* @module br/services/bundlepath/JSTDBundlePathService
*/

var br = require('br/Core');
var BundlePathService = require('br/services/BundlePathService');
var BundlePathUtil = require("./BundlePathUtil");

/**
* Provides access to the bundle path which is automatically generated by BladeRunnerJS
* @class
* @alias module:br/services/bundlepath/JSTDBundlePathService
* @implements module:br/services/BundlePathService
*/
function JSTDBundlePathService() {
};

JSTDBundlePathService.prototype.getBundlePath = function(bundlePath)	{
	return BundlePathUtil.getBundlePath("../v/dev/", bundlePath);
}

JSTDBundlePathService.prototype.getUnversionedBundlePath = function(bundlePath) {
	return BundlePathUtil.getBundlePath("../", bundlePath);
};


br.implement(JSTDBundlePathService, BundlePathService);

module.exports = JSTDBundlePathService;
