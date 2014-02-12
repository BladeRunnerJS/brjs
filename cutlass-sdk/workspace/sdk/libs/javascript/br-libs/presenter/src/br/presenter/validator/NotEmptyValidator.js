/**
 * @implements br.presenter.validator.Validator
 */
br.presenter.validator.NotEmptyValidator = function(sFailureMessage)
{
	this.sMessage = sFailureMessage;
};

br.Core.implement(br.presenter.validator.NotEmptyValidator, br.presenter.validator.Validator);

br.presenter.validator.NotEmptyValidator.prototype.validate = function(vValue, mAttributes, oValidationResult)
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
