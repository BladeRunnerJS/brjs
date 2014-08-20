/**
 * @module br/presenter/property/ValidationResultCollator
 */

/**
 * @private
 * @class
 * @alias module:br/presenter/property/ValidationResultCollator
 */
br.presenter.property.ValidationResultCollator = function(oValidationResultListener, nValidators)
{
	/** @private */
	this.m_oValidationResultListener = oValidationResultListener;

	/** @private */
	this.m_pValidationResults = [];

	/** @private */
	this.m_bReceivedValidationError = false;

	/** @private */
	this.m_nValidators = nValidators;
};

/**
 * @private
 */
br.presenter.property.ValidationResultCollator.prototype.createValidationResult = function(nValidatorIndex)
{
	var oValidationResultReceiver = new br.presenter.property.ValidationResultCollator.ValidationResultReceiver(this, nValidatorIndex);

	return new br.presenter.validator.ValidationResult(oValidationResultReceiver);
};

/**
 * @private
 */
br.presenter.property.ValidationResultCollator.prototype.cancelValidationResults = function()
{
	this.m_oValidationResultListener = null;
};

/**
 * @private
 */
br.presenter.property.ValidationResultCollator.prototype._onNextValidationResultReceived = function(oValidationResult, nValidatorIndex)
{
	if (this.m_oValidationResultListener && !this.m_bReceivedValidationError)
	{
		this.m_pValidationResults[nValidatorIndex] = oValidationResult;
		for (var i = 0, max = this.m_nValidators; i < max; ++i)
		{
			if (!this.m_pValidationResults[i]) // when the i-th validator hasn't run yet this will be undefined
			{
				break;
			}
			else if (!this.m_pValidationResults[i].isValid())
			{
				this.m_bReceivedValidationError = true;
				this.m_oValidationResultListener.onValidationResultReceived(this.m_pValidationResults[i]);
				this.m_pValidationResults = [];
				break;
			}
			// Only send success once, when all validators have been successful
			else if (i === (max-1))
			{
				this.m_oValidationResultListener.onValidationResultReceived(this.m_pValidationResults[i]);
				this.m_pValidationResults = [];
			}
		}
	}
};

/**
 * @private
 */
br.presenter.property.ValidationResultCollator.ValidationResultReceiver = function(oCollator, nValidatorIndex)
{
	this.m_oCollator = oCollator;
	this.m_nValidatorIndex = nValidatorIndex;
};
br.Core.implement(br.presenter.property.ValidationResultCollator.ValidationResultReceiver, br.presenter.validator.ValidationResultListener);

/**
 * @private
 */
br.presenter.property.ValidationResultCollator.ValidationResultReceiver.prototype.onValidationResultReceived = function(oValidationResult)
{
	this.m_oCollator._onNextValidationResultReceived(oValidationResult, this.m_nValidatorIndex);
};
