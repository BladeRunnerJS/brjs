var Errors = require('br/Errors');

/**
 * @interface
 * @class
 * A ValidationResultListener is notified when a validator completes.
 */
br.presenter.validator.ValidationResultListener = function() {};

/**
 * Callback to notify this class of a completed validation result.
 * 
 * @param {br.presenter.validator.ValidationResult} oValidationResult the result for the validation.  Will not be null.
 */
br.presenter.validator.ValidationResultListener.prototype.onValidationResultReceived = function(oValidationResult)
{
	throw new Errors.UnimplementedInterfaceError("ValidationResultListener.onValidationResultReceived() has not been implemented.");
};
