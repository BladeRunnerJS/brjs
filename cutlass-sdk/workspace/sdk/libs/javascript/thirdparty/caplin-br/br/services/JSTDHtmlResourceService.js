define("br/services/JSTDHtmlResourceService", function(require, module, exports) {
	"use strict";
	
	var BRHtmlResourceService = require('./BRHtmlResourceService'); 
	
	/**
	 * @constructor
	 * @class
	 * This class provides access to HTML templates loaded via the HTML bundler for testing purposes.
	 * This is the default Caplin HtmlResourceService
	 *
	 *  @param {String} sUrl A URL to load HTML from.
	 *
	 *
	 * @implements caplin.services.providers.CaplinHtmlResourceService
	 */
	function JSTDHtmlResourceService(sUrl) {
		var sDefaultUrl = (window.jstestdriver) ? "/test/bundles/html.bundle" : null;
		BRHtmlResourceService.call(this, sUrl || sDefaultUrl);
	}
	
	br.extend(JSTDHtmlResourceService, BRHtmlResourceService);
	
	module.exports = JSTDHtmlResourceService;
});