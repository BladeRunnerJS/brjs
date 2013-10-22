br.thirdparty("momentjs");
/**
 * Constructs a new <code>ISODateProperty</code> instance.
 * 
 * @class
 * <code>ISODateProperty</code> is a {@link br.presenter.property.WritableProperty},
 * representing an ISO date
 * 
 * @constructor
 * @param vValue (optional) A valid ISO Date string (YYYY-MM-DD) or a native Date object
 * @extends br.presenter.property.WritableProperty
 */
br.presenter.property.ISODateProperty = function(vValue)
{
	/** @private */
	this.m_oDateValidator = new br.presenter.validator.ISODateValidator();

	vValue = this._validateDate(vValue);

	// super constructor
	br.presenter.property.WritableProperty.call(this, vValue);
};

br.extend(br.presenter.property.ISODateProperty, br.presenter.property.WritableProperty);

/**
 * Gets the date object that is property represents
 * @type Date
 */
br.presenter.property.ISODateProperty.prototype.getDateValue = function()
{
	var sDate = this.getValue();
	if (sDate)
	{
		return new Date(Number(sDate.substr(0, 4)), Number(sDate.substr(5, 2))-1, Number(sDate.substr(8, 2)));
	}
	return null;
};

/**
 * Sets the value of the date
 * @param {Variant} vValue The new date value (A valid ISO Date string (YYYY-MM-DD) or a native Date object)
 */
br.presenter.property.ISODateProperty.prototype.setValue = function(vValue)
{
	vValue = this._validateDate(vValue);
	br.presenter.property.WritableProperty.prototype.setValue.call(this, vValue);
};

/**
 * @private
 * Returns a date string if the parameter passed in is a valid ISO string or date object.
 * @param vDate A valid ISO Date string (YYYY-MM-DD) or a native Date object
 * @type String
 */
br.presenter.property.ISODateProperty.prototype._validateDate = function(vDate)
{
	if (vDate instanceof Date)
	{
		vDate = moment(vDate).format("YYYY-MM-DD");
		return vDate;
	}
	var oValidationResult = new br.presenter.validator.ValidationResult();
	this.m_oDateValidator.validate(vDate, {}, oValidationResult);
	if(!oValidationResult.isValid())
	{
		throw new br.Errors.CustomError(br.Errors.LEGACY, oValidationResult.getFailureMessage());
	}
	else
	{
		return vDate;
	}
};
