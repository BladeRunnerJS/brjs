novobank.example.DemoPresentationModel = function()
{
	this.amount = new caplin.presenter.property.EditableProperty("100000");
	this.amount.addValidator(new caplin.core.validator.NumericValidator("novobank.example.invalid.number"));
	this.amount.addValidationErrorListener(this, "_onAmountError");
	this.amount.addChangeListener(this, "_onAmountChanged");
};
caplin.extend(novobank.example.DemoPresentationModel, caplin.presenter.PresentationModel);

novobank.example.DemoPresentationModel.prototype._onAmountError = function(sValue, sErrMsg)
{
	alert(sValue + " is an: " + sErrMsg);
};

novobank.example.DemoPresentationModel.prototype._onAmountChanged = function()
{
	alert("You changed the amount to: " +  this.amount.getValue());
};
