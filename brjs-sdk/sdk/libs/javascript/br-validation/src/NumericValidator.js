/**
 * @module br/validation/NumericValidator
 */

/**
 * @class
 * @alias module:br/validation/NumericValidator
 * @implements module:br/validation/Validator
 */
br.validation.NumericValidator = function(sFailureMessage)
{
	this.sMessage = sFailureMessage;
	/*
	* The first boolean part of the Regex allows for:
	* 123, .123, 1.23,
	* but will not match "123." hence [\d]+\.
	 */
	this.m_oRegex = new RegExp(/^[-+]?(([\d]*\.?[\d]+)|([\d]+\.))$/);
};

br.Core.implement(br.validation.NumericValidator, br.validation.Validator);

br.validation.NumericValidator.prototype.validate = function(vValue, mAttributes, oValidationResult)
{
	if((typeof vValue === 'string' || typeof vValue === 'number') && this.m_oRegex.test(vValue))
	{
		var bIsValid = true;
	}
	else
	{
		var bIsValid = false;
	}

	var oTranslator = require("br/I18n").getTranslator();
	var sFailureMessage = oTranslator.tokenExists(this.sMessage) ? oTranslator.getMessage(this.sMessage,{sInput:vValue}) : this.sMessage;

	oValidationResult.setResult(bIsValid, sFailureMessage);
};
