/**
 * @module br/presenter/validator/NumericValidator
 */

/**
 * @class
 * @alias module:br/presenter/validator/NumericValidator
 * @implements module:br/presenter/validator/Validator
 */
br.presenter.validator.NumericValidator = function(sFailureMessage)
{
	this.sMessage = sFailureMessage;
	this.m_oRegex = new RegExp(/^\d+$/);
};

br.Core.implement(br.presenter.validator.NumericValidator, br.presenter.validator.Validator);

br.presenter.validator.NumericValidator.prototype.validate = function(vValue, mAttributes, oValidationResult)
{
	if(this.m_oRegex.test(vValue))
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

