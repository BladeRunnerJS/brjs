"use strict";

/**
* @module br/services/appversion/JSTDAppVersionService
*/

var br = require('br/Core');
var AppVersionService = require('br/services/AppVersionService');

/**
* A test environment version of the {@link module:br/services/AppVersionService}, always
* returns 'dev' as the version.
* @class
* @alias module:br/services/appversion/JSTDAppVersionService
* @implements module:br/services/AppVersionService
*/
function JSTDAppVersionService() {
};

JSTDAppVersionService.prototype.getBundlePath = function(bundlePath)	{
	return "dev";
}

br.implement(JSTDAppVersionService, AppVersionService);

module.exports = JSTDAppVersionService;
