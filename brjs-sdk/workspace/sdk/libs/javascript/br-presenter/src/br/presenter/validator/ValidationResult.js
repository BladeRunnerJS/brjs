/**
 * @module br/presenter/validator/ValidationResult
 */

/**
 * Creates a new ValidationResult.
 * @description
 * ValidationResults are used to store the output from a {@link module:br/presenter/validator/Validator}.
 * 
 * @param {module:br/presenter/validator/ValidationResultListener} oValidationResultListener (optional) Listener informed when the validation result is available.
 */
br.presenter.validator.ValidationResult = function(oValidationResultListener)
{
	this.m_bIsValid = null;
	this.m_sFailureMessage = null;
	this.m_oValidationResultListener = oValidationResultListener;
	this.m_bHasResult = false;
};

/**
 * Specifies the result of validation.
 * This should only be called once during the lifetime of the <code>ValidationResult</code>.
 * @param {boolean} bIsValid whether or not the current validation succeeded or failed.
 * @param {String} sFailureMessage a message describing why validation failed if it did. Can be omitted if validation succeeded.
 */
br.presenter.validator.ValidationResult.prototype.setResult = function(bIsValid, sFailureMessage)
{
	this.m_bIsValid = bIsValid;
	this.m_sFailureMessage = sFailureMessage;
	this.m_bHasResult = true;
	if (this.m_oValidationResultListener)
	{
		this.m_oValidationResultListener.onValidationResultReceived(this);
	}
};

/**
 * Whether or not validation has completed for this ValidationResult.
 * 
 * @returns {boolean} true if setResult has been called, false otherwise.
 */
br.presenter.validator.ValidationResult.prototype.hasResult = function()
{
	return this.m_bHasResult;
};

/**
 * Whether or not validation succeeded.
 * 
 * @returns {boolean} true if the validation succeeded, false if it failed and null if it has not yet happened.
 */
br.presenter.validator.ValidationResult.prototype.isValid = function()
{
	return this.m_bIsValid;
};

/**
 * Gets any message that validation may have returned if validation was not successful.
 * 
 * @returns {String} the failure message or null if there isn't one.
 */
br.presenter.validator.ValidationResult.prototype.getFailureMessage = function()
{
	return this.m_sFailureMessage;
};
