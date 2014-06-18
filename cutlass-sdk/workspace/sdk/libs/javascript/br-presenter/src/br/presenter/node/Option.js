/**
 * A single option held within an {@link br.presenter.node.OptionsNodeList} instance.
 * 
 * @param {String} sValue The (logical) value of the option.
 * @param {String} sLabel The label that is displayed on the screen.
 * @param {Boolean} bEnabled Is the option enabled or disabled (enabled by default).
 * @constructor
 *
 * @extends br.presenter.node.PresentationNode
 */
br.presenter.node.Option = function(sValue, sLabel, bEnabled)
{
	/**
	 * The value of the option.
	 * @type String
	 */
	this.value = new br.presenter.property.WritableProperty(sValue);
	/**
	 * The textual label associated with the option.
	 * @type String
	 */
	this.label = new br.presenter.property.WritableProperty(sLabel);
	/**
	 * If option is enabled
	 * @type String
	 */
	this.enabled = new br.presenter.property.WritableProperty(bEnabled === undefined ? true : bEnabled);
};

br.Core.extend(br.presenter.node.Option, br.presenter.node.PresentationNode);

/**
 * Returns the option label.
 * @type String
 */
br.presenter.node.Option.prototype.toString = function()
{
	return this.label.getValue();
};
