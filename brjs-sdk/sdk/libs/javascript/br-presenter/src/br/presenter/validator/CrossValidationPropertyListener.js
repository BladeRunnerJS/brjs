/**
 * @module br/presenter/validator/CrossValidationPropertyListener
 */

var PropertyListener = require('br/presenter/property/PropertyListener');

/**
 * @private
 * @class
 * @alias module:br/presenter/validator/CrossValidationPropertyListener
 * @implements module:br/presenter/property/PropertyListener
 *
 * @param oCrossPropertyValidationProxy
 * @param sPropertyId
 */
function CrossValidationPropertyListener(oCrossPropertyValidationProxy, sPropertyId)
{
	this.m_oValidationProxy = oCrossPropertyValidationProxy;
	this.m_sPropertyId = sPropertyId;
}

br.Core.inherit(CrossValidationPropertyListener, PropertyListener);

CrossValidationPropertyListener.prototype.onPropertyChanged = function()
{
	this.m_oValidationProxy._$onPropertyChanged(this.m_sPropertyId);
};

module.exports = CrossValidationPropertyListener;
