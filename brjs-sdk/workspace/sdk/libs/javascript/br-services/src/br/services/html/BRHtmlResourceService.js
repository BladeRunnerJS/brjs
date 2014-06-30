var br = require('br/Core');
var File = require('br/core/File');
var HtmlResourceService = require('br/services/HtmlResourceService');
var i18n = require('br/I18n');

/**
 * Provides access to HTML templates loaded via the HTML bundler.
 * This is the default HtmlResourceService in BladeRunnerJS
 *
 * @class
 * @constructor
 *
 * @param {String} url A URL to load HTML from.
 *
 * @implements br.services.HtmlResourceService
 */
function BRHtmlResourceService(url) {
	var ServiceRegistry = require("br/ServiceRegistry");
	/** @private */
	this.url = url || ServiceRegistry.getService('br.bundle-path-service').getBundlePath("/html/bundle.html");

	/** @private */
	this.templates = {};

	/** @private */
	this.element = document.createElement("div");
	this.element.style.display = "none";

	this._loadHtml();
}

/**
 * Access an HTML template by name.
 *
 * @param {String} templateId The identifier of the template that is required. Note that templates should be contained
 * within a template tag (preferably).
 *
 * @returns {HTMLElement}
 */
BRHtmlResourceService.prototype.getHTMLTemplate = function (templateId) {
	var template = null;
	if (this.templates[templateId]) {
		template = this.templates[templateId]
	}
	else {
		template = getTemplate(document.getElementById(templateId));
		this.templates[templateId] = template;
	}

    if (template != null) {
        return template.cloneNode(true);
    }
    return null;
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
		this.templates[this.element.children[i].id] = getTemplate(this.element.children[i]);
	}

	document.body.removeChild(this.element);
};

/**
 * Gets the template by removing the template tag if needed.
 */
function getTemplate(template) {
	if (template != null && template.tagName.toLowerCase() === "template") {
		// Template being a non fully supported tag yet, check if we can use content, else use a dummy div to get the
		// inner content.
		if (template.content) {
			template = template.content;
		}
		else {
			// First clone the contents of the template to avoid modifying the original as we will be reparenting.
			var docFrag = document.createDocumentFragment(),
				tempClone = template.cloneNode(true),
				children = tempClone.childNodes;

			while(children.length > 0) {
				docFrag.appendChild(children[0]);
			}

			template = docFrag;
		}
	}
	return template;
}

function sanitizeHtml(html) {
	// IE and old Firefox's don't allow assigning text with script tag in it to innerHTML.
	if (html.match(/<script(.*)type=\"text\/html\"/)) {
		function replacer(str, p1) {
			return '<div' + p1;
		}

		// TODO: Log the fact there is a script tag in the template and that it should be replaced with a div.
		html = html.replace(/<script(.*)type=\"text\/html\"/g, replacer).replace(/<\/script>/g, '</div>');
	}

	return html;
}


br.implement(BRHtmlResourceService, HtmlResourceService);

module.exports = BRHtmlResourceService;
