/**
 * @module br/presenter/node/FieldValuePropertyListener
 */

/**
 * @private
 * @class
 * @alias module:br/presenter/node/FieldValuePropertyListener
 * @implements module:br/presenter/property/PropertyListener
 * 
 * @param {module:br/presenter/node/Field} oField
 *
 */
br.presenter.node.FieldValuePropertyListener = function(oField)
{
	this.m_oField = oField;
	oField.value.addListener(this, true);
	// TODO: we need to invoke removeListener() in our destructor
};

br.Core.inherit(br.presenter.node.FieldValuePropertyListener, br.presenter.property.PropertyListener);


// *********************** PropertyListener Interface ***********************

/**
 * @private
 * @see br.presenter.property.PropertyListener#onValidationSuccess
 */
br.presenter.node.FieldValuePropertyListener.prototype.onValidationSuccess = function(vPropertyValue, sErrorMessage)
{
	this.m_oField.hasError.setValue(false);
	this.m_oField.failureMessage.setValue("");
};

/**
 * @private
 * @see br.presenter.property.PropertyListener#onValidationError
 */
br.presenter.node.FieldValuePropertyListener.prototype.onValidationError = function(vPropertyValue, sErrorMessage)
{
	this.m_oField.hasError.setValue(true);
	this.m_oField.failureMessage.setValue(sErrorMessage);
};
