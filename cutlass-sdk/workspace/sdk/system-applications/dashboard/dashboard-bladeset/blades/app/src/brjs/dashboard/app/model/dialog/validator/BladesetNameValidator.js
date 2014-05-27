brjs.dashboard.app.model.dialog.validator.BladesetNameValidator = function(oPresentationModel)
{
	this.m_oPresentationModel = oPresentationModel;
};
br.Core.extend(brjs.dashboard.app.model.dialog.validator.BladesetNameValidator, br.presenter.validator.Validator);

brjs.dashboard.app.model.dialog.validator.BladesetNameValidator.prototype.validate = function(vValue, mAttributes, oValidationResult)
{
	if(!brjs.dashboard.app.NameValidator.isValidPackageName(vValue))
	{
		oValidationResult.setResult(false, brjs.dashboard.app.NameValidator.INVALID_PACKAGE_NAME_MESSAGE);
	}
	else if(brjs.dashboard.app.NameValidator.isReservedJsWord(vValue))
	{
		oValidationResult.setResult(false, brjs.dashboard.app.NameValidator.RESERVED_JS_WORD_MESSAGE);
	}
	else if (this.m_oPresentationModel && this.m_oPresentationModel.appDetailScreen.getBladeset(vValue))
	{
		oValidationResult.setResult(false, "A bladeset called '" + vValue + "' already exists.");
	}
	else
	{
		oValidationResult.setResult(true);
	}
};
