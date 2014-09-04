/**
 * @module br/presenter/node/Button
 */

/**
 * @class
 * @alias module:br/presenter/node/Button
 * @extends module:br/presenter/node/PresentationNode
 * 
 * @classdesc
 * A <code>PresentationNode</code> containing all of the attributes necessary to
 * model a button on screen.
 * 
 * @param vLabel (optional) The text that will be displayed within the button &mdash; can be a <code>String</code> or a {@link module:br/presenter/property/Property}.
 */
br.presenter.node.Button = function(vLabel)
{
	if(!(vLabel instanceof br.presenter.property.Property))
	{
		vLabel = new br.presenter.property.WritableProperty(vLabel || "");
	}
	/**
	 * The text currently displayed within the button.
	 * @type br.presenter.property.WritableProperty
	 */
	this.label = vLabel;
	
	/**
	 * A boolean property representing whether the button is enabled or not.
	 * @type br.presenter.property.WritableProperty
	 */
	this.enabled = new br.presenter.property.WritableProperty(true);
	
	/**
	 * A boolean property representing whether the button is visible or not.
	 * @type br.presenter.property.WritableProperty
	 */
	this.visible = new br.presenter.property.WritableProperty(true);
};

br.Core.extend(br.presenter.node.Button, br.presenter.node.PresentationNode);
