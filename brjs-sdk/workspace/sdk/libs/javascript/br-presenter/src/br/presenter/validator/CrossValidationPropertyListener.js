/**
 * @module br/presenter/validator/CrossValidationPropertyListener
 */

/**
 * @private
 * @class
 * @alias module:br/presenter/validator/CrossValidationPropertyListener
 * @implements module:br/presenter/property/PropertyListener
 * 
 * @param oCrossPropertyValidationProxy
 * @param sPropertyId
 */
br.presenter.validator.CrossValidationPropertyListener = function(oCrossPropertyValidationProxy, sPropertyId)
{
	this.m_oValidationProxy = oCrossPropertyValidationProxy;
	this.m_sPropertyId = sPropertyId;
};

br.Core.inherit(br.presenter.validator.CrossValidationPropertyListener, br.presenter.property.PropertyListener);

br.presenter.validator.CrossValidationPropertyListener.prototype.onPropertyChanged = function()
{
	this.m_oValidationProxy._$onPropertyChanged(this.m_sPropertyId);
};

