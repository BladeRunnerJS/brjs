novobank.example.DemoPresentationModel = function()
{
	this.greeting = new caplin.presenter.property.WritableProperty("Hello World");
};
caplin.implement(novobank.example.DemoPresentationModel, caplin.presenter.PresentationModel);