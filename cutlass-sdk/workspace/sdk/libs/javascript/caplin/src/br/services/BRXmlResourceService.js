var br = require('br');
var Errors = require('br/Errors');
var XmlResourceService = require('./XmlResourceService ');

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
	this.url = sUrl || "xml.bundle";

	/** @private */
	this.element = caplin.core.XmlParser.parse("<div></div>");

	this._loadXml();
};

BRXmlResourceService.prototype.getXmlDocument = function(elementName) {
	try {
		return this.element.getElementsByTagName(elementName);
	} catch (e) {
		caplin.core.Logger.log(caplin.core.LogLevel.ERROR, "Error: requested XML resource tagName '" + elementName + "' does not exist in application config.");
	}
};

/**
 * @private
 */
BRXmlResourceService.prototype._loadXml = function() {
	var rawXml = caplin.getFileContents(this.url);
	var translatedXml = caplin.i18n.Translator.getTranslator().translate(rawXml);
	var data = caplin.core.XmlParser.parse(translatedXml);

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
