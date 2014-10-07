/**
* @module br/services/html/BRHtmlResourceService
*/

var br = require('br/Core');
var File = require('br/core/File');
var HtmlResourceService = require('br/services/HtmlResourceService');
var i18n = require('br/I18n');

/**
 * @class
 * @alias module:br/services/html/BRHtmlResourceService
 * @implements module:br/services/HtmlResourceService
 * 
 * @classdesc
 * Provides access to HTML templates loaded via the HTML bundler.
 * This is the default HtmlResourceService in BladeRunnerJS
 * 
 * @param {String} url A URL to load HTML from.
 */
function BRHtmlResourceService(url) {
	var ServiceRegistry = require("br/ServiceRegistry");
	/** @private */
	this.url = url || ServiceRegistry.getService('br.app-meta-service').getVersionedBundlePath("html/bundle.html");

	/** @private */
	this.templates = {};

	/** @private */
	this.element = document.createElement("div");
	this.element.style.display = "none";

	this._loadHtml();
}

br.implement(BRHtmlResourceService, HtmlResourceService);

/**
 * Access an HTML template by name.
 *
 * @param {String} templateId The identifier of the template that is required. Note that templates should be contained
 * within a template tag (preferably).
 *
 * @returns {HTMLElement}
 */
BRHtmlResourceService.prototype.getHTMLTemplate = function (templateId) {
	if (this.templates[templateId]) {
		return this.templates[templateId];
	}
	return document.getElementById(templateId);
};

/**
 * @private
 */
BRHtmlResourceService.prototype._loadHtml = function () {
	document.body.appendChild(this.element);

	var rawHtml = File.readFileSync(this.url);
	var translatedHtml = i18n.getTranslator().translate(rawHtml, "html");
	this.element.innerHTML = sanitizeHtml(translatedHtml);

	for (var i = 0, max = this.element.children.length; i < max; i++) {
		this.templates[this.element.children[i].id] = this.element.children[i].cloneNode(true);
	}

	document.body.removeChild(this.element);
};

function sanitizeHtml(html) {
	// IE and old Firefox's don't allow assigning text with script tag in it to innerHTML.
	if (html.match(/<script(.*)type=\"text\/html\"/)) {
	 	function replacer(str, p1) {
	 		return '<div' + p1;
	 	};
	 	// TODO: Log the fact there is a script tag in the template and that it should be replaced with a div.
	 	html = html.replace(/<script(.*)type=\"text\/html\"/g, replacer).replace(/<\/script>/g, '</div>');
	}

	return html;
};

module.exports = BRHtmlResourceService;
