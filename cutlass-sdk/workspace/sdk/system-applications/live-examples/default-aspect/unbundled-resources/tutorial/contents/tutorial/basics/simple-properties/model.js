novobank.example.DemoPresentationModel = function()
{
	this.amount = new caplin.presenter.property.Property(10000);
};
caplin.implement(novobank.example.DemoPresentationModel, caplin.presenter.PresentationModel);
