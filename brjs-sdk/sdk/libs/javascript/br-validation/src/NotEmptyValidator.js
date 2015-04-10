/**
 * @module br/validation/NotEmptyValidator
 */

var brCore = require("br/Core");
var Validator = require("br/validation/Validator");

/**
 * @class
 * @alias module:br/validation/NotEmptyValidator
 * @implements module:br/validation/Validator
 */
var NotEmptyValidator = function(sFailureMessage)
{
	this.sMessage = sFailureMessage;
};

brCore.implement(NotEmptyValidator, Validator);

NotEmptyValidator.prototype.validate = function(vValue, mAttributes, oValidationResult)
{
	if(vValue=="")
	{
		var bIsValid = false;
	}
	else
	{
		var bIsValid = true;
	}
	
	var oTranslator = require("br/I18n").getTranslator();
	var sFailureMessage = oTranslator.tokenExists(this.sMessage) ? oTranslator.getMessage(this.sMessage) : this.sMessage;
	
	oValidationResult.setResult(bIsValid, sFailureMessage);
};

module.exports = NotEmptyValidator;
