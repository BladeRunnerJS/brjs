caplinx.dashboard.app.model.dialog.validator.BladeNameValidator = function(oPresentationModel)
{
	this.m_oPresentationModel = oPresentationModel;
};
br.Core.extend(caplinx.dashboard.app.model.dialog.validator.BladeNameValidator, br.presenter.validator.Validator);

caplinx.dashboard.app.model.dialog.validator.BladeNameValidator.prototype.setBladeset = function(sBladeset)
{
	this.m_sBladeset = sBladeset;
};

caplinx.dashboard.app.model.dialog.validator.BladeNameValidator.prototype.validate = function(vValue, mAttributes, oValidationResult)
{
	if(!caplinx.dashboard.app.NameValidator.isValidPackageName(vValue))
	{
		oValidationResult.setResult(false, caplinx.dashboard.app.NameValidator.INVALID_PACKAGE_NAME_MESSAGE);
	}
	else if(caplinx.dashboard.app.NameValidator.isReservedJsWord(vValue))
	{
		oValidationResult.setResult(false, caplinx.dashboard.app.NameValidator.RESERVED_JS_WORD_MESSAGE);
	}
	else if (this.m_oPresentationModel && this.m_oPresentationModel.appDetailScreen.getBladeset(this.m_sBladeset).getBlade(vValue))
	{
		//TODO: remove the need to check for this.m_oPresentationModel - this is undefined for UTs so the tests will fail without it
		oValidationResult.setResult(false, "A blade called '" + vValue + "' already exists.");
	}
	else
	{
		oValidationResult.setResult(true);
	}
};
