/**
 * @module br/presenter/validator/CrossValidationPropertyBinder
 */

var CrossPropertyValidatorProxy = require('br/presenter/validator/CrossPropertyValidatorProxy');

/**
 * @class
 * @alias module:br/presenter/validator/CrossValidationPropertyBinder
 *
 * @classdesc
 * The <code>CrossValidationPropertyBinder</code> is used to associate a single
 * {@link module:br/presenter/validator/CrossPropertyValidator} instance with multiple
 * {@link module:br/presenter/property/Property} instances.
 *
 * <p>The {@link module:br/presenter/property/Property} class does not directly support the registration
 * of cross property validators, so the <code>CrossValidationPropertyBinder</code> bridges this gap
 * by creating a standard {@link module:br/validation/Validator} that proxies all validation calls to the
 * underlying {@link module:br/presenter/validator/CrossPropertyValidator}.</p>
 */
function CrossValidationPropertyBinder()
{
	/** @private */
	this.m_mConfiguredValidators = {};

	/** @private */
	this.m_nNextAvaliableId = 0;
}

/**
 * Binds the given validator to the set of named properties provided.
 *
 * @param {Object} mProperties The set of named properties that <code>oCrossPropertyValidator</code> expects.
 * @param {module:br/presenter/validator/CrossPropertyValidator} oCrossPropertyValidator The validator that will validate <code>mProperties</code>.
 * @type int
 * @returns A numeric bind ID that can be used to later unbind this validator using {@link #unbindValidator}.
 * @see #unbindValidator
 */
CrossValidationPropertyBinder.prototype.bindValidator = function(mProperties, oCrossPropertyValidator)
{
	var nCurrentId = this.m_nNextAvaliableId;

	this.m_mConfiguredValidators[nCurrentId] = new CrossPropertyValidatorProxy(mProperties, oCrossPropertyValidator);

	this.m_nNextAvaliableId++;
	return nCurrentId;
};

/**
 * Unbinds a cross-property validator previously set-up using {@link #bindValidator}.
 *
 * @param {int} nBindId A numeric bind ID previously returned by {@link #bindValidator}.
 * @see #bindValidator
 */
CrossValidationPropertyBinder.prototype.unbindValidator = function(nBindId)
{
	this.m_mConfiguredValidators[nBindId].destroy();
	delete this.m_mConfiguredValidators[nBindId];
};

module.exports = new CrossValidationPropertyBinder();
