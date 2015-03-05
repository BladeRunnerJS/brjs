/**
 * @module br/presenter/validator/CrossPropertyValidatorProxy
 */

var Validator = require('br/validation/Validator');
var CrossValidationPropertyListener = require('br/presenter/validator/CrossValidationPropertyListener');

/**
 * @private
 * @class
 * @alias module:br/presenter/validator/CrossPropertyValidatorProxy
 * @implements module:br/validation/Validator
 *
 * @classdesc
 * This class is to be used via the {@link module:br/presenter/validator/CrossValidationPropertyBinder}.
 *
 * <p>This proxy class implements the {@link module:br/validation/Validator} interface so that it can be added as a validator
 * of {@link module:br/presenter/property/EditableProperty} instances. It then handles the routing pf validation calls
 * to the instance of {@link module:br/presenter/validator/CrossPropertyValidator} that it is constructed with. If there
 * are instances of non-editable properties, this proxy listens to changes on them and triggers re-validation when they happen.</p>
 *
 * <p>The <code>mProperties</code> parameter that this class is constructed with must match what the <code>oCrossPropertyValidator</code>'s
 * {@link module:br/presenter/validator/CrossPropertyValidator#validate} expects.</p>
 *
 * @param {Object} mProperties A name-to-property mapping of all the properties that <code>oCrossPropertyValidator</code> expects.
 * @param {module:br/presenter/validator/CrossPropertyValidator} oCrossPropertyValidator The validator to proxy validations to.
 */
function CrossPropertyValidatorProxy(mProperties, oCrossPropertyValidator)
{
	/** @private */
	this.m_mAllProperties = mProperties;

	/** @private */
	this.m_mEditableProperties = {}; // populated below

	/** @private */
	this.m_oCrossPropertyValidator = oCrossPropertyValidator;

	/** @private */
	this.m_bPropagateValidation = true;

	/** @private */
	this.m_mCrossValidationPropertyListeners = {};

	for (var sPropId in this.m_mAllProperties)
	{
		var oProperty = this.m_mAllProperties[sPropId];
		if(oProperty instanceof br.presenter.property.EditableProperty)
		{
			this.m_mEditableProperties[sPropId] = oProperty;
			oProperty.addValidator(this, {"sPropertyId": sPropId});
		}
		var oListener = new CrossValidationPropertyListener(this, sPropId);
		oProperty.addListener(oListener);
		this.m_mCrossValidationPropertyListeners[sPropId] = oListener;
	}
}
br.Core.implement(CrossPropertyValidatorProxy, Validator);

/**
 * @private
 * @param vValue IGNORED (interface compatibility)
 * @param mAttributes
 * @param oValidationResult passed to the Cross Validator
 */
CrossPropertyValidatorProxy.prototype.validate = function(vValue, mAttributes, oValidationResult) {
	var Utility = require('br/core/Utility');

	if(!Utility.isEmpty(this.m_mAllProperties))
	{
		this.m_oCrossPropertyValidator.validate(this.m_mAllProperties, oValidationResult);

		if(this.m_bPropagateValidation)
		{
			var sCurrentProperty = mAttributes["sPropertyId"];
			this._propagateValidation(sCurrentProperty);
		}
	}
	else
	{
		oValidationResult.setResult(true, "");
	}
};

CrossPropertyValidatorProxy.prototype._propagateValidation = function(sPropertyToSkip)
{
	this.m_bPropagateValidation = false;
	for (var sPropId in this.m_mEditableProperties)
	{
		if(sPropId === sPropertyToSkip)
		{
			continue;
		}
		this.m_mEditableProperties[sPropId].forceValidation();
	}
	this.m_bPropagateValidation = true;
};

CrossPropertyValidatorProxy.prototype.destroy = function()
{
	this.m_mAllProperties = {};
	this.m_mEditableProperties = {};
	for(var sPropId in this.m_mAllProperties)
	{
		if(this.m_mAllProperties[sPropId].removeListener)
		{
			this.m_mAllProperties[sPropId].removeListener(this.m_mCrossValidationPropertyListeners[sPropId]);
		}
	}
};

/**
 * @private
 * @param sPropId ID of the property that just changed
 */
CrossPropertyValidatorProxy.prototype._$onPropertyChanged = function(sPropId)
{
	this._propagateValidation(sPropId); // force-validate all editable properties
};

module.exports = CrossPropertyValidatorProxy;
