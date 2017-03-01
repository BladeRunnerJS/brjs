/**
 * @module br/validation/ValidationResult
 */

var brCore = require("br/Core");

/**
 * Creates a new instance of <code>ValidationResult</code>.
 * 
 * @class
 * @alias module:br/validation/ValidationResult
 * 
 * @classdesc
 * ValidationResults are used to store the output from a {@link module:br/validation/Validator}.
 * 
 * @param {module:br/validation/ValidationResultListener} oValidationResultListener (optional) Listener informed when the validation result is available.
 */
var ValidationResult = function(oValidationResultListener)
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
ValidationResult.prototype.setResult = function(bIsValid, sFailureMessage)
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
ValidationResult.prototype.hasResult = function()
{
	return this.m_bHasResult;
};

/**
 * Whether or not validation succeeded.
 * 
 * @returns {boolean} true if the validation succeeded, false if it failed and null if it has not yet happened.
 */
ValidationResult.prototype.isValid = function()
{
	return this.m_bIsValid;
};

/**
 * Gets any message that validation may have returned if validation was not successful.
 * 
 * @returns {String} the failure message or null if there isn't one.
 */
ValidationResult.prototype.getFailureMessage = function()
{
	return this.m_sFailureMessage;
};

module.exports = ValidationResult;