caplinx.dashboard.app.model.dialog.validator.AppNamespaceValidator = function()
{
};
caplin.extend(caplinx.dashboard.app.model.dialog.validator.AppNamespaceValidator, caplin.core.Validator);

caplinx.dashboard.app.model.dialog.validator.AppNamespaceValidator.prototype.validate = function(vValue, mAttributes, oValidationResult)
{
	if(!caplinx.dashboard.app.NameValidator.isValidPackageName(vValue))
	{
		oValidationResult.setResult(false, caplinx.dashboard.app.NameValidator.INVALID_PACKAGE_NAME_MESSAGE);
	}
	else if(caplinx.dashboard.app.NameValidator.isReservedJsWord(vValue))
	{
		oValidationResult.setResult(false, caplinx.dashboard.app.NameValidator.RESERVED_JS_WORD_MESSAGE);
	}
	else if((vValue == "caplin") || (vValue == "caplinx"))
	{
		oValidationResult.setResult(false, "'" + vValue + "' is a reserved namespace.");
	}
	else
	{
		oValidationResult.setResult(true);
	}
};
