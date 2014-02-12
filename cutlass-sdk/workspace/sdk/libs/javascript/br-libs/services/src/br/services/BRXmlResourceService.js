var br = require('br/Core');
var Errors = require('br/Errors');
var XmlResourceService = require('br/services/XmlResourceService');
var XmlParser = require('br/util/XmlParser');
var File = require('br/core/File');
var i18n = require('br/i18n');

/**
 * @constructor
 * @class This class provides access to XML documents loaded via the XML
 *        bundler.
 * 
 * @param {String}
 *          sUrl The url to load to retrieve the XML resource.
 * 
 * @implements caplin.services.XmlResourceService
 */
function BRXmlResourceService(sUrl) {
	/** @private */
	this.url = sUrl || "bundle.xml";

	/** @private */
	this.element = XmlParser.parse("<div></div>");

	this._loadXml();
};

BRXmlResourceService.prototype.getXmlDocument = function(elementName) {
	try {
		return this.element.getElementsByTagName(elementName);
	} catch (e) {
		throw new Errors.INVALID_PARAMETERS("Requested XML resource tagName '" + elementName + "' does not exist in application config.");
	}
};

/**
 * @private
 */
BRXmlResourceService.prototype._loadXml = function() {
	var rawXml = File.readFileSync(this.url);
	var translatedXml = i18n.getTranslator().translate(rawXml);
	var data = XmlParser.parse(translatedXml);

	if (data) {
		if (data.nodeName === 'parsererror' || data.getElementsByTagName('parsererror').length > 0) {
			throw new Errors.InvalidDataError("XML is badly formed: [" + this.url + "]");
		} else {
			this.element.appendChild(data);
		}
	}
};

br.implement(BRXmlResourceService, XmlResourceService);

module.exports = BRXmlResourceService;
