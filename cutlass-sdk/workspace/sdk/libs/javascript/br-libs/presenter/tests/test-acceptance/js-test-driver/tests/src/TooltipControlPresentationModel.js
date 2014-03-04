TooltipControlPresentationModel = function()
{
	this.tooltipField1 = new br.presenter.node.ToolTipField("");
	this.tooltipField2 = new br.presenter.node.ToolTipField("");
	
	this.tooltipField1.value.addValidator(new TooltipControlPresentationModel.aValidator());
	this.tooltipField2.value.addValidator(new TooltipControlPresentationModel.anotherValidator());
	
	this.tooltipNode = new br.presenter.node.ToolTipNode();
	this.errorMonitor = new br.presenter.util.ErrorMonitor(this.tooltipNode);
	this.errorMonitor.monitorField(this.tooltipField1);
	this.errorMonitor.monitorField(this.tooltipField2);
};

br.Core.extend(TooltipControlPresentationModel, br.presenter.PresentationModel);

TooltipControlPresentationModel.aValidator = function()
{
};

br.Core.implement(TooltipControlPresentationModel.aValidator, br.presenter.validator.Validator);

TooltipControlPresentationModel.aValidator.prototype.validate = function(vValue, mAttributes, oValidationResult)
{
		oValidationResult.setResult (vValue == 1, "ERR" );
	return;
};

TooltipControlPresentationModel.anotherValidator = function()
{
};

br.Core.implement(TooltipControlPresentationModel.anotherValidator, br.presenter.validator.Validator);

TooltipControlPresentationModel.anotherValidator.prototype.validate = function(vValue, mAttributes, oValidationResult)
{
	oValidationResult.setResult (vValue == 0, "ANOTHERERR" );
	return;
};