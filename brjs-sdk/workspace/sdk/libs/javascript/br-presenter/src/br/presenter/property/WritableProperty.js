/**
 * @module br/presenter/property/WritableProperty
 */

/**
 * Constructs a new <code>WritableProperty</code> instance.
 * 
 * @class
 * @alias module:br/presenter/property/WritableProperty
 * @extends module:br/presenter/property/Property
 * 
 * @classdesc
 * <code>WritableProperty</code> is identical to {@link module:br/presenter/property/Property},
 * except that it adds the ability to update the value stored within the property.
 * 
 * @param {Object} vValue (optional) The default value for this property.
 */
br.presenter.property.WritableProperty = function(vValue)
{
	br.presenter.property.Property.call(this, vValue);
};
br.Core.extend(br.presenter.property.WritableProperty, br.presenter.property.Property);

/**
 * Sets the unformatted value for this property and notifies listeners of the
 * change.
 * 
 * <p>This method is called from the presentation model only, and does not
 * allow user editable controls to be bound from the view.</p>
 *
 * @param {Variant} vValue The new value for this property.
 * @type br.presenter.property.WritableProperty
 */
br.presenter.property.WritableProperty.prototype.setValue = function(vValue)
{
	return this._$setInternalValue(vValue);
};
