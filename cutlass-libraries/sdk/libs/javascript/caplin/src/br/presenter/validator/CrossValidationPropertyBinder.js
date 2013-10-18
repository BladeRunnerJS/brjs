/**
 * @singleton
 * 
 * @class
 * The <code>CrossValidationPropertyBinder</code> is used to associate a single
 * {@link br.presenter.validator.CrossPropertyValidator} instance with multiple
 * {@link br.presenter.property.Property} instances.
 * 
 * <p>The {@link br.presenter.property.Property} class does not directly support the registration
 * of cross property validators, so the <code>CrossValidationPropertyBinder</code> bridges this gap
 * by creating a standard {@link br.presenter.validator.Validator} that proxies all validation calls to the
 * underlying {@link br.presenter.validator.CrossPropertyValidator}.</p>
 * 
 * @constructor
 */
br.presenter.validator.CrossValidationPropertyBinder = function()
{
	/** @private */
	this.m_mConfiguredValidators = {};

	/** @private */
	this.m_nNextAvaliableId = 0;
};

/**
 * Binds the given validator to the set of named properties provided.
 * 
 * @param {Object} mProperties The set of named properties that <code>oCrossPropertyValidator</code> expects.
 * @param {br.presenter.validator.CrossPropertyValidator} oCrossPropertyValidator The validator that will validate <code>mProperties</code>.
 * @type int
 * @returns A numeric bind ID that can be used to later unbind this validator using {@link #unbindValidator}.
 * @see #unbindValidator
 */
br.presenter.validator.CrossValidationPropertyBinder.prototype.bindValidator = function(mProperties, oCrossPropertyValidator)
{
	var nCurrentId = this.m_nNextAvaliableId;

	this.m_mConfiguredValidators[nCurrentId] = new br.presenter.validator.CrossPropertyValidatorProxy(mProperties, oCrossPropertyValidator);

	this.m_nNextAvaliableId++;
	return nCurrentId;
};

/**
 * Unbinds a cross-property validator previously set-up using {@link #bindValidator}.
 * 
 * @param {int} nBindId A numeric bind ID previously returned by {@link #bindValidator}.
 * @see #bindValidator
 */
br.presenter.validator.CrossValidationPropertyBinder.prototype.unbindValidator = function(nBindId)
{
	this.m_mConfiguredValidators[nBindId].destroy();
	delete this.m_mConfiguredValidators[nBindId];
};

br.presenter.validator.CrossValidationPropertyBinder = new br.presenter.validator.CrossValidationPropertyBinder();
