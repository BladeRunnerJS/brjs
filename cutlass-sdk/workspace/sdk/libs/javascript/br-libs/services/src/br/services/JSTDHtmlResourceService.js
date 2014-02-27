"use strict";

var BRHtmlResourceService = require('./BRHtmlResourceService'); 

/**
 * @constructor
 * @class
 * This class provides access to HTML templates loaded via the HTML bundler for testing purposes.
 * This is the default BRHtmlResourceService
 *
 *  @param {String} sUrl A URL to load HTML from.
 *
 *
 * @implements br.services.BRHtmlResourceService
 */
function JSTDHtmlResourceService(sUrl) {
	var sDefaultUrl = (window.jstestdriver) ? "/test/bundles/html.bundle" : null;
	BRHtmlResourceService.call(this, sUrl || sDefaultUrl);
}

br.Core.extend(JSTDHtmlResourceService, BRHtmlResourceService);

module.exports = JSTDHtmlResourceService;
