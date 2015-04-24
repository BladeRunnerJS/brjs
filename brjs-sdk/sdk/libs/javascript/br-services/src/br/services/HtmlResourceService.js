var Errors = require('br/Errors');

/**
* @module br/services/HtmlResourceService
*/

/**
 * @class
 * @interface
 * @alias module:br/services/HtmlResourceService
 * 
 * @classdesc
 * A service that provides access to HTML templates.
 */
function HtmlResourceService() {
}

/**
 * Retrieve a given template as a document-fragment.
 * 
 * @param {String} templateId The identifier of the template you wish to retrieve.
 * @returns {DocumentFragment}
 */
HtmlResourceService.prototype.getTemplateFragment = function(templateId) {
	throw new Errors.UnimplementedInterfaceError("br.services.HtmlResourceService.getTemplateFragment() has not been implemented.");
};

/**
 * Retrieve a given template as an element &mdash; only singly rooted templates can be retrieved this way.
 * 
 * @param {String} templateId The identifier of the template you wish to retrieve.
 * @returns {HTMLElement}
 * @throws RangeError if the template contains more than one root node.
 */
HtmlResourceService.prototype.getTemplateElement = function(templateId) {
	throw new Errors.UnimplementedInterfaceError("br.services.HtmlResourceService.getTemplateElement() has not been implemented.");
};

/**
 * @deprecated as this method has now been renamed to #getTemplateElement.
 */
HtmlResourceService.prototype.getHTMLTemplate = function(templateId) {
	throw new Errors.UnimplementedInterfaceError("br.services.HtmlResourceService.getHTMLTemplate() has not been implemented.");
};

module.exports = HtmlResourceService;
