/**
 * Constructs a new instance of <code>TemplateNode</code> that will cause the given template name to be displayed when
 * {@link #getTemplateName} is invoked.
 * 
 * @class
 * Utility used for displaying a template in a {@link br.presenter.node.NodeList}, where the template
 * does not need to bind to a real {@link br.presenter.node.PresentationNode}.
 * 
 * @constructor
 * @param {String} sTemplateName The name of the template to use.
 * @extends br.presenter.node.PresentationNode
 * @implements br.presenter.node.TemplateAware
 */
br.presenter.node.TemplateNode = function(sTemplateName)
{
	/** @private */
	this.m_sTemplateName = sTemplateName;
};

br.Core.extend(br.presenter.node.TemplateNode, br.presenter.node.PresentationNode);
br.Core.implement(br.presenter.node.TemplateNode, br.presenter.node.TemplateAware);

/**
 * @private
 * @see br.presenter.node.TemplateAware#getTemplateName
 */
br.presenter.node.TemplateNode.prototype.getTemplateName = function() {
	return this.m_sTemplateName;
};
