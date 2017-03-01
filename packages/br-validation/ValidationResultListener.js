/**
 * @module br/validation/ValidationResultListener
 */

var Errors = require('br/Errors');
var brCore = require("br/Core");

/**
 * @class
 * @interface
 * @alias module:br/validation/ValidationResultListener
 * 
 * @classdesc
 * A ValidationResultListener is notified when a validator completes.
 */
function ValidationResultListener() {
}

/**
 * Callback to notify this class of a completed validation result.
 * 
 * @param {module:br/validation/ValidationResult} oValidationResult the result for the validation.  Will not be null.
 */
ValidationResultListener.prototype.onValidationResultReceived = function(oValidationResult) {
	throw new Errors.UnimplementedInterfaceError("ValidationResultListener.onValidationResultReceived() has not been implemented.");
};

module.exports = ValidationResultListener;
