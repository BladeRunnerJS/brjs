var Errors = require('br/Errors');

/**
* @module br/services/XmlResourceService
*/

/**
 * @class
 * @interface
 * @alias module:br/services/XmlResourceService
 * 
 * @classdesc
 * This class provides access to XML documents loaded via the XML bundler.
 */
function XmlResourceService() {
}

/**
* Access an XML document by name.
*
* @param {String} sElementName The name of the root element of the particular document you wish to retrieve (e.g. 'gridDefinitions')
*/
XmlResourceService.prototype.getXmlDocument = function(sElementName)	{
	throw new Errors.UnimplementedInterfaceError("XmlResourceService.getXmlDocument() has not been implemented.");
};

module.exports = XmlResourceService;
