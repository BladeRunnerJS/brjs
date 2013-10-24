var Errors = require("br/Errors");

/**
 * @class
 * An interface for validators that need to consider the validity of multiple
 * {@link br.presenter.property.Property} instances in relation to each other.
 * 
 * @constructor
 * @interface
 */
br.presenter.validator.CrossPropertyValidator = function()
{
};

/**
 * Validate the set of named properties.
 * 
 * <p>Implementations of <code>CrossPropertyValidator</code> will typically expect a well defined
 * set of named properties to perform validation on. Validators should fail-fast, and throw
 * an exception if the set of properties they receive is not the same as what they expected.</p>
 * 
 * @param {Object} mProperties A named set of properties to validate.
 * @param {br.presenter.validator.ValidationResult} oValidationResult The result object that the outcome of the validation will be set on.
 */
br.presenter.validator.CrossPropertyValidator.prototype.validate = function(mProperties, oValidationResult)
{
	throw new Errors.UnimplementedInterfaceError("CrossPropertyValidator.validate() has not been implemented.");
};
