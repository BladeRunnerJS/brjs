/**
 * @module br/presenter/node/ComponentNode
 */

/**
 * @class
 * @alias module:br/presenter/node/ComponentNode
 * @extends module:br/presenter/node/PresentationNode
 * 
 * @classdesc
 * Allows components to be embedded within a presentation model so that they can be displayed as part of the template.
 * 
 * <p><code>Component</code> instances can be embedded within a template as follows:</p>
 * 
 * <pre>
 *   &lt;div data-bind="component:componentNode, width:widthProperty, height:heightProperty"&gt;&lt;/div&gt;
 * </pre>
 * 
 * <p>where <code>componentNode</code> is an instance of <code>ComponentNode</code>.
 * 
 * @param {module:br/component/Component} oComponent The component to be displayed on the page.
 */
br.presenter.node.ComponentNode = function(oComponent)
{
	/** @private */
	this.m_oComponent = oComponent;
};

br.Core.extend(br.presenter.node.ComponentNode, br.presenter.node.PresentationNode);

/**
 * Retrieve the component stored within this node.
 * @type br.component.Component
 */
br.presenter.node.ComponentNode.prototype.getComponent = function()
{
	return this.m_oComponent;
};
