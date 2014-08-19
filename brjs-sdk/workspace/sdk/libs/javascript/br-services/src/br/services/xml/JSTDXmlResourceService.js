"use strict";

/**
* @module br/services/xml/JSTDXmlResourceService
*/

var br = require('br/Core');
var BRXmlResourceService = require('./BRXmlResourceService');

/**
* This class provides access to XML documents loaded via the XML bundler for testing purposes.
*
* @alias module:br/services/xml/JSTDXmlResourceService
* @param {String} sUrl A URL to load XML from.
* @description
* @implements module:br/services/xml/BRXmlResourceService
*/
function JSTDXmlResourceService(sUrl) {
	var sDefaultUrl = (window.jstestdriver) ? "/test/bundles/xml.bundle" : null;
	BRXmlResourceService.call(this, sUrl || sDefaultUrl);
}

br.extend(JSTDXmlResourceService, BRXmlResourceService);

module.exports = JSTDXmlResourceService;
