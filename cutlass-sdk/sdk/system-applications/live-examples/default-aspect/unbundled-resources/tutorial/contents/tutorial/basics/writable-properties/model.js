novobank.example.DemoPresentationModel = function()
{
	this.amount = new caplin.presenter.property.WritableProperty(10000);
	this.amount.setValue(42);
};
caplin.implement(novobank.example.DemoPresentationModel, caplin.presenter.PresentationModel);
