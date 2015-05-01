/**
 * @module br/validation/Validator
 */

var Errors = require('br/Errors');
var brCore = require("br/Core");

/**
 * This is an interface and should not be constructed.
 * 
 * @class
 * @interface
 * @alias module:br/validation/Validator
 * 
 * @classdesc
 * A validator is a piece of code that can determine whether particular input should be considered valid.
 * 
 * <p>Since you may want to run many validators on a piece of data, the result of the validation is stored on an
 * object passed into the validate method.</p>
 */
function Validator() {
}

/**
 * Determine whether the provided value is valid or not and set the result on the provided {@link module:br/validation/ValidationResult}.
 * 
 * @param {Object} vValue The value to validate.
 * @param {Object} mAttributes attributes to control the validation process. Will not be null.
 * @param {module:br/validation/ValidationResult} oValidationResult the ValidationResult to store the result of this validation in.
 */
Validator.prototype.validate = function(vValue, mAttributes, oValidationResult) {
	throw new Errors.UnimplementedInterfaceError("Validator.validate() has not been implemented.");
};

module.exports = Validator;
