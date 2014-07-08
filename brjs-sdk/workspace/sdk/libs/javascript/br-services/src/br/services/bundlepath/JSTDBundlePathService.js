"use strict";

var br = require('br/Core');
var BundlePathService = require('br/services/BundlePathService');
var BundlePathUtil = require("./BundlePathUtil");

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
