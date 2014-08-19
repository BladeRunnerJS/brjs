"use strict";

/**
* @module br/services/html/JSTDHtmlResourceService
*/

var br = require('br/Core');
var BRHtmlResourceService = require('./BRHtmlResourceService');

/**
* This class provides access to HTML templates loaded via the HTML bundler for testing purposes.
* This is the default BRHtmlResourceService
* @alias module:br/services/html/JSTDHtmlResourceService
* @param {String} sUrl A URL to load HTML from.
* @classdesc
* @implements module:br/services/html/BRHtmlResourceService
*/
function JSTDHtmlResourceService(sUrl) {
	var sDefaultUrl = (window.jstestdriver) ? "/test/bundles/html.bundle" : null;
	BRHtmlResourceService.call(this, sUrl || sDefaultUrl);
}

br.extend(JSTDHtmlResourceService, BRHtmlResourceService);

module.exports = JSTDHtmlResourceService;
