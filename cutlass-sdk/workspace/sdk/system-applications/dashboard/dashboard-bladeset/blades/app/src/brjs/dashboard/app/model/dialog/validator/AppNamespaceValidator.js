brjs.dashboard.app.model.dialog.validator.AppNamespaceValidator = function()
{
};
br.Core.extend(brjs.dashboard.app.model.dialog.validator.AppNamespaceValidator, br.presenter.validator.Validator);

brjs.dashboard.app.model.dialog.validator.AppNamespaceValidator.prototype.validate = function(vValue, mAttributes, oValidationResult)
{
	if(!brjs.dashboard.app.NameValidator.isValidPackageName(vValue))
	{
		oValidationResult.setResult(false, brjs.dashboard.app.NameValidator.INVALID_PACKAGE_NAME_MESSAGE);
	}
	else if(brjs.dashboard.app.NameValidator.isReservedJsWord(vValue))
	{
		oValidationResult.setResult(false, brjs.dashboard.app.NameValidator.RESERVED_JS_WORD_MESSAGE);
	}
	else if((vValue == "caplin") || (vValue == "brjs"))
	{
		oValidationResult.setResult(false, "'" + vValue + "' is a reserved namespace.");
	}
	else
	{
		oValidationResult.setResult(true);
	}
};
