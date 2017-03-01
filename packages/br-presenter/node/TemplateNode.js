'use strict';

var TemplateAware = require('br-presenter/node/TemplateAware');
var PresentationNode = require('br-presenter/node/PresentationNode');
var Core = require('br/Core');

/**
 * @module br/presenter/node/TemplateNode
 */

/**
 * Constructs a new instance of <code>TemplateNode</code> that will cause the given template name to be displayed when
 * {@link #getTemplateName} is invoked.
 * 
 * @class
 * @alias module:br/presenter/node/TemplateNode
 * @extends module:br/presenter/node/PresentationNode
 * @implements module:br/presenter/node/TemplateAware
 * 
 * @classdesc
 * Utility used for displaying a template in a {@link module:br/presenter/node/NodeList}, where the template
 * does not need to bind to a real {@link module:br/presenter/node/PresentationNode}.
 * 
 * @param {String} sTemplateName The name of the template to use.
 */
function TemplateNode(sTemplateName) {
	/** @private */
	this.m_sTemplateName = sTemplateName;
}

Core.extend(TemplateNode, PresentationNode);
Core.implement(TemplateNode, TemplateAware);

/**
 * @private
 * @see br.presenter.node.TemplateAware#getTemplateName
 */
TemplateNode.prototype.getTemplateName = function() {
	return this.m_sTemplateName;
};

module.exports = TemplateNode;
