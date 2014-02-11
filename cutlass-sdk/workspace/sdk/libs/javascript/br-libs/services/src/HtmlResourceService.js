var Errors = require('br/Errors');

/**
 * @constructor
 * @class
 * @interface
 * A service that provides access to HTML templates.
 */
function HtmlResourceService() {};

/**
 * Access an HTML template by name.
 *
 * @param {String} templateId The identifier of the root element of the template you wish to retrieve.
 */
HtmlResourceService.prototype.getHTMLTemplate = function(templateId) {
	throw new Errors.UnimplementedInterfaceError("caplin.services.HtmlResourceService.getHTMLTemplate() has not been implemented.");
};

module.exports = HtmlResourceService;
