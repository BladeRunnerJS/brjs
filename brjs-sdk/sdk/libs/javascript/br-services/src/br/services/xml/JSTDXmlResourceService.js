"use strict";

/**
* @module br/services/xml/JSTDXmlResourceService
*/

var br = require('br/Core');
var BRXmlResourceService = require('./BRXmlResourceService');

/**
 * @class
 * @alias module:br/services/xml/JSTDXmlResourceService
 * @implements module:br/services/xml/BRXmlResourceService
 * 
 * @classdesc
 * This class provides access to XML documents loaded via the XML bundler for testing purposes.
 * 
 * @param {String} sUrl A URL to load XML from.
 */
function JSTDXmlResourceService(sUrl) {
	var sDefaultUrl = (window.jstestdriver) ? "/test/bundles/xml.bundle" : null;
	BRXmlResourceService.call(this, sUrl || sDefaultUrl);
}

br.extend(JSTDXmlResourceService, BRXmlResourceService);

module.exports = JSTDXmlResourceService;
