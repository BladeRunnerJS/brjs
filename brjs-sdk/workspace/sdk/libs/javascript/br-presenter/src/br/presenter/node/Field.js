/**
 * @module br/presenter/node/Field
 */

/**
 * @class
 * @alias module:br/presenter/node/Field
 * @extends module:br/presenter/node/PresentationNode
 * 
 * @classdesc
 * A <code>PresentationNode</code> containing all of the attributes necessary to
 * model an input field on screen.
 * 
 * @param {Object} vValue (optional) The initial value of the field, either using a
 * primitive type or as a {@link module:br/presenter/property/EditableProperty}.
 */
br.presenter.node.Field = function(vValue)
{
	if((vValue instanceof br.presenter.property.Property) && !(vValue instanceof br.presenter.property.EditableProperty)){
		throw new br.Errors.InvalidParametersError("Field constructor: can't pass non-editable property as parameter");
	}
	
	if(!(vValue instanceof br.presenter.property.EditableProperty)) {
		vValue = new br.presenter.property.EditableProperty(vValue);
	}
	
	/**
	 * The textual label associated with the input field.
	 * @type br.presenter.property.WritableProperty
	 */
	this.label = new br.presenter.property.WritableProperty("");
	
	/**
	 * The current value displayed within the input field.
	 * @type br.presenter.property.EditableProperty
	 */
	this.value = vValue;
	
	/**
	 * A boolean property that is <code>true</code> if {@link #value} has any validation errors, and <code>false</code> otherwise.
	 * @type br.presenter.property.WritableProperty
	 */
	this.hasError = new br.presenter.property.WritableProperty(false);
	
	/**
	 * A textual description of the currently failing validation message when {@link #hasError} is <code>true</code>.
	 * @type br.presenter.property.WritableProperty
	 */
	this.failureMessage = new br.presenter.property.WritableProperty();
	
	/**
	 * A boolean property representing whether the input field is enabled or not.
	 * @type br.presenter.property.WritableProperty
	 */
	this.enabled = new br.presenter.property.WritableProperty(true);
	
	/**
	 * A boolean property representing whether the input field is visible or not.
	 * @type br.presenter.property.WritableProperty
	 */
	this.visible = new br.presenter.property.WritableProperty(true);

	/**
	 * The logical control-name the field is being bound to &mdash; this
	 * value will appear within the <code>name</code> attribute if being bound
	 * to a native HTML control.
	 * @type br.presenter.property.WritableProperty
	 */
	this.controlName = new br.presenter.property.WritableProperty("");
	
	/** @private */
	this.m_oValueListener = new br.presenter.node.FieldValuePropertyListener(this);
};
br.Core.extend(br.presenter.node.Field, br.presenter.node.PresentationNode);
