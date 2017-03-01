/**
 * @module br/presenter/validator/CrossPropertyValidator
 */

var Errors = require("br/Errors");

/**
 * @class
 * @interface
 * @alias module:br/presenter/validator/CrossPropertyValidator
 *
 * @classdesc
 * An interface for validators that need to consider the validity of multiple
 * {@link module:br/presenter/property/Property} instances in relation to each other.
 */
function CrossPropertyValidator() {
}

/**
 * Validate the set of named properties.
 *
 * <p>Implementations of <code>CrossPropertyValidator</code> will typically expect a well defined
 * set of named properties to perform validation on. Validators should fail-fast, and throw
 * an exception if the set of properties they receive is not the same as what they expected.</p>
 *
 * @param {Object} mProperties A named set of properties to validate.
 * @param {module:br/validation/ValidationResult} oValidationResult The result object that the outcome of the validation will be set on.
 */
CrossPropertyValidator.prototype.validate = function(mProperties, oValidationResult) {
	throw new Errors.UnimplementedInterfaceError("CrossPropertyValidator.validate() has not been implemented.");
};

module.exports = CrossPropertyValidator;
