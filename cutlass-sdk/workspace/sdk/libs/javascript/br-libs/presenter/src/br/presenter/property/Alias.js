/**
 * Constructs a new <code>Alias</code> instance.
 * 
 * @class
 * <code>Alias</code> instances wrap instances of {@link br.presenter.property.Property} and keep the
 * values synchronised, to allow access to the same property from multiple paths (e.g. double binding).
 * 
 * <p></p>
 * 
 * @constructor
 * @param {br.presenter.property.Property} oProperty The property to wrap.
 * @extends br.presenter.property.Property
 */
br.presenter.property.Alias = function(oProperty)
{
	if (!(oProperty instanceof br.presenter.property.Property))
	{
		throw new br.Errors.CustomError(br.Errors.LEGACY, "An Alias can only constructed with a presenter Property");
	}
	/** @private */
	this.m_oWrappedProperty = oProperty;
	
	oProperty.addChangeListener(this, "_onWrappedChanged", false);
	var vInitial = oProperty.getValue();
	br.presenter.property.Property.call(this, vInitial);
};

br.Core.extend(br.presenter.property.Alias, br.presenter.property.Property);

/**
 * @private
 */
br.presenter.property.Alias.prototype._onWrappedChanged = function()
{
	var vValue = this.m_oWrappedProperty.getValue();
	this._$setInternalValue(vValue);
};

br.presenter.property.Alias.prototype.getValue = function()
{
	return this.m_oWrappedProperty.getValue();
};

br.presenter.property.Alias.prototype.getFormattedValue = function()
{
	return this.m_oWrappedProperty.getFormattedValue();
};

br.presenter.property.Alias.prototype.getRenderedValue = function()
{
	return this.m_oWrappedProperty.getRenderedValue();
};
