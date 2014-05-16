brjs.dashboard.app.model.dialog.validator.AppNameValidator = function(oPresentationModel)
{
	this.m_oPresentationModel = oPresentationModel;
};
br.Core.extend(brjs.dashboard.app.model.dialog.validator.AppNameValidator, br.presenter.validator.Validator);

brjs.dashboard.app.model.dialog.validator.AppNameValidator.prototype.validate = function(vValue, mAttributes, oValidationResult)
{
	// TODO: raise a presenter bug about the fact that properties() treats undefined and empty string as the same thing, whereas they are not
	// -- this is whey I need the intial if(vValue && ...
	
	if(!brjs.dashboard.app.NameValidator.isValidDirectoryName(vValue))
	{
		oValidationResult.setResult(false, brjs.dashboard.app.NameValidator.DIRECTORY_CHARACTERS_MESSAGE);
	}
	else if (vValue && this.m_oPresentationModel && this.m_oPresentationModel.appsScreen.apps.properties("appName", vValue).getSize() > 0)
	{
		//TODO: remove the need to check for this.m_oPresentationModel - this is undefined for UTs so the tests will fail without it
		oValidationResult.setResult(false, "An app called '" + vValue + "' already exists.");
	}
	else
	{
		oValidationResult.setResult(true);
	}
};
