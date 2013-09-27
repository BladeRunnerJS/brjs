novobank.example.DemoPresentationModel = function()
{
	this.amount = new caplin.presenter.property.EditableProperty(10000);
	this.amount.addValidator(new caplin.core.validator.NumericValidator("novobank.example.invalid.number"));
	this.amount.addValidationErrorListener(this, "_onAmountError");
	
	var sLabel = ct.i18n("novobank.example.amount.label");
	this.amountLabel = new caplin.presenter.property.Property(sLabel);
};
caplin.implement(novobank.example.DemoPresentationModel, caplin.presenter.PresentationModel);

novobank.example.DemoPresentationModel.prototype._onAmountError = function(sValue, sErrMsg)
{
	alert(sValue + " is an: " + sErrMsg);
};
