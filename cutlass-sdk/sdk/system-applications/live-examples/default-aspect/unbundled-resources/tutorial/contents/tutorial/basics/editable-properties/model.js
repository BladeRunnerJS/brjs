novobank.example.DemoPresentationModel = function()
{
	this.amount = new caplin.presenter.property.EditableProperty("100000");
	this.amount.addChangeListener(this, "_onAmountChanged");
};
caplin.extend(novobank.example.DemoPresentationModel, caplin.presenter.PresentationModel);

novobank.example.DemoPresentationModel.prototype._onAmountChanged = function()
{
	alert("You changed the amount to: " +  this.amount.getValue());
};
