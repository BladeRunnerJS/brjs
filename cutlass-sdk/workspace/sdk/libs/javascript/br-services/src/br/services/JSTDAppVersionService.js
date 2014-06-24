"use strict";

var br = require('br/Core');
var AppVersionService = require('br/services/AppVersionService');

function JSTDAppVersionService() {
};

JSTDAppVersionService.prototype.getBundlePath = function(bundlePath)	{
	return "dev";
}

br.implement(JSTDAppVersionService, AppVersionService);

module.exports = JSTDAppVersionService;
