/**
 * Constructs a new instance of <code>DisplayField</code>.
 * 
 * @class
 * A <code>PresentationNode</code> containing all of the attributes necessary to
 * model a non-input field on screen.
 * 
 * @constructor
 * @param {Object} vValue (optional) The initial value of the field, either using a
 * primitive type or as a {@link br.presenter.property.Property}.
 * @extends br.presenter.node.PresentationNode
 */
br.presenter.node.DisplayField = function(vValue)
{
	if(!(vValue instanceof br.presenter.property.Property)) {
		vValue = new br.presenter.property.WritableProperty(vValue);
	}
	
	/**
	 * The textual label associated with the field.
	 * @type br.presenter.property.WritableProperty
	 */
	this.label = new br.presenter.property.WritableProperty("");
	
	/**
	 * A boolean property representing whether the field is visible or not.
	 * @type br.presenter.property.WritableProperty
	 */
	this.visible = new br.presenter.property.WritableProperty(true);
	
	/**
	 * The current value displayed within the field.
	 * @type br.presenter.property.WritableProperty
	 */
	this.value = vValue;
};

br.Core.extend(br.presenter.node.DisplayField, br.presenter.node.PresentationNode);
