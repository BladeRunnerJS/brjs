/**
 * @private
 * @constructor
 * @param oCrossPropertyValidationProxy
 * @param sPropertyId
 * @implements br.presenter.property.PropertyListener
 *
 */
br.presenter.validator.CrossValidationPropertyListener = function(oCrossPropertyValidationProxy, sPropertyId)
{
	this.m_oValidationProxy = oCrossPropertyValidationProxy;
	this.m_sPropertyId = sPropertyId;
};

br.inherit(br.presenter.validator.CrossValidationPropertyListener, br.presenter.property.PropertyListener);

br.presenter.validator.CrossValidationPropertyListener.prototype.onPropertyChanged = function()
{
	this.m_oValidationProxy._$onPropertyChanged(this.m_sPropertyId);
};

