"use strict";

var br = require('br/Core');
var BRXmlResourceService = require('./BRXmlResourceService');

/**
 * @constructor
 * @class
 * This class provides access to XML documents loaded via the XML bundler for testing purposes.
 *
 * @param {String} sUrl A URL to load XML from.
 *
 * @implements br.services.BRXmlResourceService
 */
function JSTDXmlResourceService(sUrl) {
	var sDefaultUrl = (window.jstestdriver) ? "/test/bundles/xml.bundle" : null;
	BRXmlResourceService.call(this, sUrl || sDefaultUrl);
}

br.extend(JSTDXmlResourceService, BRXmlResourceService);

module.exports = JSTDXmlResourceService;
