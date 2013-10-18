/**
 * @private
 * @class
 * This class is to be used via the {@link br.presenter.validator.CrossValidationPropertyBinder}.
 *
 * <p>This proxy class implements the {@link br.presenter.validator.Validator} interface so that it can be added as a validator
 * of {@link br.presenter.property.EditableProperty} instances. It then handles the routing pf validation calls
 * to the instance of {@link br.presenter.validator.CrossPropertyValidator} that it is constructed with. If there
 * are instances of non-editable properties, this proxy listens to changes on them and triggers re-validation when they happen.</p>
 *
 * <p>The <code>mProperties</code> parameter that this class is constructed with must match what the <code>oCrossPropertyValidator</code>'s
 * {@link br.presenter.validator.CrossPropertyValidator#validate} expects.</p>
 *
 * @constructor
 * @param {Object} mProperties A name-to-property mapping of all the properties that <code>oCrossPropertyValidator</code> expects.
 * @param {br.presenter.validator.CrossPropertyValidator} oCrossPropertyValidator The validator to proxy validations to.
 * @implements br.presenter.validator.Validator
 */
br.presenter.validator.CrossPropertyValidatorProxy = function(mProperties, oCrossPropertyValidator)
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
		var oListener = new br.presenter.validator.CrossValidationPropertyListener(this, sPropId);
		oProperty.addListener(oListener);
		this.m_mCrossValidationPropertyListeners[sPropId] = oListener;
	}
};
br.implement(br.presenter.validator.CrossPropertyValidatorProxy, br.presenter.validator.Validator);

/**
 * @private
 * @param vValue IGNORED (interface compatibility)
 * @param mAttributes
 * @param oValidationResult passed to the Cross Validator
 */
br.presenter.validator.CrossPropertyValidatorProxy.prototype.validate = function(vValue, mAttributes, oValidationResult) {
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

br.presenter.validator.CrossPropertyValidatorProxy.prototype._propagateValidation = function(sPropertyToSkip)
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

br.presenter.validator.CrossPropertyValidatorProxy.prototype.destroy = function()
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
br.presenter.validator.CrossPropertyValidatorProxy.prototype._$onPropertyChanged = function(sPropId)
{
	this._propagateValidation(sPropId); // force-validate all editable properties
};
