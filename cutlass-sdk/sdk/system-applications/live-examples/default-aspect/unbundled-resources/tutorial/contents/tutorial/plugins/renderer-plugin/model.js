novobank.example.DemoPresentationModel = function()
{
	this.renderer_text = new caplin.presenter.property.Property("This element has had its class applied by the ClassStyler");
};
caplin.implement(novobank.example.DemoPresentationModel, caplin.presenter.PresentationModel);

caplin.element.ElementFactory.getDisplayBuffer().start();